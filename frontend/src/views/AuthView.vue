<template>
  <div class="layout">

    <!-- Left brand panel -->
    <div class="brand">
      <div class="brand-wordmark">
        Online Chat<em>在 线 · 交 流</em>
      </div>
      <p class="brand-copy">连接你我，分享每一个瞬间。<br>安全、实时、轻量。</p>
      <ul class="brand-list">
        <li><span class="pip"></span>邮箱验证码 / 密码两种登录方式</li>
        <li><span class="pip"></span>JWT 无状态认证，数据安全有保障</li>
        <li><span class="pip"></span>WebSocket 实时消息，即时触达</li>
      </ul>
      <div class="pulse-wrap" aria-hidden="true">
        <div v-for="n in 5" :key="n" class="pulse-ring"></div>
        <div class="pulse-core"></div>
      </div>
    </div>

    <!-- Right form panel -->
    <div class="form-panel rise">
      <div class="form-title">{{ mode === 'login' ? '欢迎回来' : '创建账号' }}</div>
      <div class="form-sub">{{ mode === 'login' ? '输入邮箱和验证码即可登录' : '填写信息，完成注册' }}</div>

      <!-- Tab -->
      <div class="tabs">
        <button :class="['tab', mode === 'login'    && 'on']" @click="switchMode('login')">登录</button>
        <button :class="['tab', mode === 'register' && 'on']" @click="switchMode('register')">注册</button>
      </div>

      <!-- Login method (login only) -->
      <Transition name="slide">
        <div class="method-tabs" v-if="mode === 'login'">
          <button :class="['method-tab', loginMethod === 'code'     && 'on']" @click="loginMethod = 'code'">验证码</button>
          <button :class="['method-tab', loginMethod === 'password' && 'on']" @click="loginMethod = 'password'">密码</button>
        </div>
      </Transition>

      <!-- Email -->
      <div class="field">
        <label class="field-label">邮箱地址</label>
        <div class="field-row">
          <input v-model="email" type="email" class="inp" placeholder="your@email.com" :disabled="codeSent && loginMethod === 'code'">
          <button v-if="mode === 'register' || loginMethod === 'code'" class="send-btn" :disabled="sending || countdown > 0" @click="sendCode">
            {{ countdown > 0 ? `${countdown}s` : (sending ? '发送中…' : codeSent ? '重新发送' : '发送验证码') }}
          </button>
        </div>
      </div>

      <!-- Code -->
      <div class="field" v-if="mode === 'register' || loginMethod === 'code'">
        <label class="field-label">验证码</label>
        <input v-model="code" type="text" class="inp" placeholder="6 位验证码" maxlength="8" :disabled="!codeSent && loginMethod === 'code'">
      </div>

      <!-- Password (login with password) -->
      <Transition name="slide">
        <div class="field" v-if="mode === 'login' && loginMethod === 'password'">
          <label class="field-label">密码</label>
          <input v-model="password" type="password" class="inp" placeholder="输入密码">
        </div>
      </Transition>

      <!-- Nickname (register only) -->
      <Transition name="slide">
        <div class="field" v-if="mode === 'register'">
          <label class="field-label">昵称</label>
          <input v-model="nickname" type="text" class="inp" placeholder="给自己起个名字吧" maxlength="20">
        </div>
      </Transition>

      <!-- Submit -->
      <button class="sub-btn" :disabled="loading" @click="doSubmit">
        <span v-if="loading" class="spin"></span>
        {{ loading ? (mode === 'login' ? '登录中…' : '注册中…') : (mode === 'login' ? '登录' : '注册') }}
      </button>

      <div v-if="msg.text" :class="['msg', msg.type]">{{ msg.text }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api/index.js'

const router      = useRouter()
const mode        = ref('login')
const loginMethod = ref('code')
const email       = ref('')
const code        = ref('')
const password    = ref('')
const nickname    = ref('')
const codeSent    = ref(false)
const sending     = ref(false)
const countdown   = ref(0)
const loading     = ref(false)
const msg         = ref({ text: '', type: '' })

let timer = null

function switchMode(m) {
  mode.value = m
  loginMethod.value = 'code'
  msg.value  = { text: '', type: '' }
}

function setMsg(text, type) { msg.value = { text, type } }

async function sendCode() {
  const em = email.value.trim()
  if (!em || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(em)) return setMsg('请输入合法的邮箱地址', 'err')

  sending.value = true
  try {
    const data = await authApi.sendCode(em)
    if (!data || data.code !== 200) { setMsg(data?.message || '发送失败，稍后重试', 'err'); return }

    setMsg('验证码已发送，请查收邮件 📬', 'ok')
    codeSent.value = true
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) { clearInterval(timer); timer = null }
    }, 1000)
  } catch { setMsg('网络错误，请检查连接', 'err') }
  finally  { sending.value = false }
}

async function doSubmit() {
  const em = email.value.trim()
  const cd = code.value.trim()
  const pw = password.value
  const nk = nickname.value.trim()
  if (!em) return setMsg('请输入邮箱', 'err')

  if (mode.value === 'login' && loginMethod.value === 'password') {
    if (!pw) return setMsg('请输入密码', 'err')
  } else {
    if (!cd) return setMsg('请输入验证码', 'err')
  }
  if (mode.value === 'register' && !nk) return setMsg('请输入昵称', 'err')

  loading.value = true
  try {
    let data
    if (mode.value === 'register') {
      data = await authApi.register(em, cd, nk)
    } else if (loginMethod.value === 'password') {
      data = await authApi.loginByPassword(em, pw)
    } else {
      data = await authApi.login(em, cd)
    }

    if (!data || data.code !== 200) { setMsg(data?.message || '操作失败', 'err'); return }

    localStorage.setItem('token', data.data.token)
    if (data.data.user) localStorage.setItem('user', JSON.stringify(data.data.user))
    setMsg(mode.value === 'login' ? '登录成功，正在跳转…' : '注册成功，正在跳转…', 'ok')
    setTimeout(() => router.push('/profile'), 700)
  } catch { setMsg('网络错误，请检查连接', 'err') }
  finally  { loading.value = false }
}

onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.layout {
  display: flex;
  width: 100%;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

/* ── Brand panel ── */
.brand {
  flex: 1;
  display: flex; flex-direction: column; justify-content: center;
  padding: 64px 72px;
  position: relative; overflow: hidden;
}
.brand::after {
  content: '';
  position: absolute; inset: 0;
  background: linear-gradient(145deg, rgba(124,58,237,.07) 0%, rgba(244,114,182,.04) 100%);
  border-right: 1px solid var(--border);
}

.brand-wordmark {
  font-family: 'Cormorant Garamond', serif;
  font-size: 68px; font-weight: 300; letter-spacing: .03em; line-height: 1.05;
  position: relative; z-index: 1;
  background: linear-gradient(135deg, #fff 30%, rgba(244,114,182,.85) 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}
.brand-wordmark em {
  display: block; font-style: italic; font-size: .58em; letter-spacing: .14em;
  background: linear-gradient(135deg, rgba(167,139,250,.9), rgba(244,114,182,.9));
  -webkit-background-clip: text; background-clip: text;
}

.brand-copy {
  margin-top: 28px; font-size: 14px; color: var(--muted);
  line-height: 1.75; max-width: 290px; position: relative; z-index: 1;
}

.brand-list {
  margin-top: 52px; list-style: none;
  display: flex; flex-direction: column; gap: 18px;
  position: relative; z-index: 1;
}
.brand-list li {
  display: flex; align-items: center; gap: 14px;
  font-size: 13px; color: rgba(226,226,240,.55);
}
.pip {
  width: 7px; height: 7px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, var(--violet), var(--rose));
}

/* Pulse rings */
.pulse-wrap {
  position: absolute; right: -90px; top: 50%; transform: translateY(-50%);
  width: 420px; height: 420px; z-index: 1; pointer-events: none;
}
.pulse-ring {
  position: absolute; border-radius: 50%; border: 1px solid;
  top: 50%; left: 50%; transform: translate(-50%, -50%);
}
.pulse-ring:nth-child(1){ width:80px;  height:80px;  border-color:rgba(244,114,182,.55); animation:pr 4s ease-in-out infinite 0s; }
.pulse-ring:nth-child(2){ width:160px; height:160px; border-color:rgba(244,114,182,.30); animation:pr 4s ease-in-out infinite .4s; }
.pulse-ring:nth-child(3){ width:240px; height:240px; border-color:rgba(167,139,250,.18); animation:pr 4s ease-in-out infinite .8s; }
.pulse-ring:nth-child(4){ width:320px; height:320px; border-color:rgba(167,139,250,.09); animation:pr 4s ease-in-out infinite 1.2s; }
.pulse-ring:nth-child(5){ width:400px; height:400px; border-color:rgba(167,139,250,.04); animation:pr 4s ease-in-out infinite 1.6s; }
@keyframes pr {
  0%,100%{ opacity:.5; transform:translate(-50%,-50%) scale(1); }
  50%    { opacity:1;  transform:translate(-50%,-50%) scale(1.025); }
}
.pulse-core {
  position: absolute; width: 8px; height: 8px; border-radius: 50%;
  background: var(--rose); top: 50%; left: 50%; transform: translate(-50%,-50%);
  box-shadow: 0 0 24px var(--rose), 0 0 48px rgba(244,114,182,.4);
}

/* ── Form panel ── */
.form-panel {
  width: 440px; flex-shrink: 0;
  display: flex; flex-direction: column; justify-content: center;
  padding: 60px 48px;
}

.form-title {
  font-family: 'Cormorant Garamond', serif;
  font-size: 34px; font-weight: 400; letter-spacing: .02em;
  transition: opacity .2s;
}
.form-sub { margin-top: 8px; font-size: 13px; color: var(--muted); }

.tabs {
  display: flex; margin: 32px 0 28px;
  background: var(--input-bg); border: 1px solid var(--border); border-radius: 10px; padding: 4px;
}
.tab {
  flex: 1; padding: 9px; border: none; background: transparent;
  color: var(--muted); font-family: 'DM Sans', sans-serif; font-size: 14px;
  cursor: pointer; border-radius: 7px; transition: all .18s;
}
.tab.on { background: var(--rose-dim); color: var(--rose); }

.method-tabs {
  display: flex; margin: 0 0 20px;
  background: var(--input-bg); border: 1px solid var(--border); border-radius: 8px; padding: 3px;
}
.method-tab {
  flex: 1; padding: 6px; border: none; background: transparent;
  color: var(--muted); font-family: 'DM Sans', sans-serif; font-size: 12px;
  cursor: pointer; border-radius: 6px; transition: all .18s;
}
.method-tab.on { background: var(--rose-dim); color: var(--rose); }

.field { margin-bottom: 18px; }
.field-label {
  display: block; font-size: 11px; letter-spacing: .09em; text-transform: uppercase;
  color: var(--muted); margin-bottom: 8px; font-weight: 500;
}
.field-row { display: flex; gap: 8px; }

.send-btn {
  flex-shrink: 0; padding: 0 16px; min-width: 100px;
  background: var(--rose-dim); border: 1px solid rgba(244,114,182,.22);
  border-radius: 10px; color: var(--rose);
  font-family: 'DM Sans', sans-serif; font-size: 13px;
  cursor: pointer; transition: background .18s;
}
.send-btn:hover:not(:disabled) { background: rgba(244,114,182,.2); }
.send-btn:disabled { opacity: .45; cursor: not-allowed; }

.sub-btn {
  width: 100%; margin-top: 6px; padding: 14px;
  background: linear-gradient(135deg, #f472b6, #a78bfa);
  border: none; border-radius: 10px; color: #fff;
  font-family: 'DM Sans', sans-serif; font-size: 15px; font-weight: 500;
  cursor: pointer; box-shadow: 0 4px 28px rgba(244,114,182,.28);
  transition: transform .15s, box-shadow .15s;
}
.sub-btn:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 7px 36px rgba(244,114,182,.38); }
.sub-btn:active:not(:disabled){ transform: translateY(0); }
.sub-btn:disabled { opacity: .65; cursor: not-allowed; }

/* Nickname slide transition */
.slide-enter-active, .slide-leave-active { transition: all .2s ease; overflow: hidden; }
.slide-enter-from, .slide-leave-to { opacity: 0; max-height: 0; margin-bottom: 0; }
.slide-enter-to, .slide-leave-from { opacity: 1; max-height: 120px; margin-bottom: 18px; }

@media (max-width: 760px) {
  .brand { display: none; }
  .form-panel { width: 100%; padding: 40px 24px; }
}
</style>
