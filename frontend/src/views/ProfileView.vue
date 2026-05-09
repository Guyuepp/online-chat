<template>
  <div style="position:relative; z-index:1; min-height:100vh;">

    <!-- Topbar -->
    <nav class="topbar">
      <div class="logo">Online Chat</div>
      <div class="topbar-right">
        <div class="topbar-user" v-if="me">
          <UserAvatar :url="me.avatar" :name="me.nickname || me.username" :size="34" />
          <strong>{{ me.nickname || me.username }}</strong>
        </div>
        <button class="logout-btn" @click="doLogout">退出登录</button>
      </div>
    </nav>

    <div class="page">

      <!-- Hero -->
      <div class="hero rise" v-if="me">
        <UserAvatar :url="profileForm.avatar || me.avatar" :name="me.nickname || me.username" :size="80"
          style="box-shadow: 0 0 0 3px rgba(244,114,182,.2), 0 0 40px rgba(244,114,182,.12);" />
        <div class="hero-info">
          <div class="hero-name">{{ me.nickname || me.username }}</div>
          <div class="hero-email">{{ me.email }}</div>
          <div class="hero-sig" v-if="me.signature">" {{ me.signature }} "</div>
        </div>
      </div>
      <div class="hero rise skeleton" v-else>
        <div class="skeleton-avatar"></div>
        <div class="skeleton-lines">
          <div class="skeleton-line w60"></div>
          <div class="skeleton-line w40"></div>
        </div>
      </div>

      <!-- Tab bar -->
      <div class="tab-bar">
        <button v-for="t in tabs" :key="t.key"
          :class="['tab-btn', activeTab === t.key && 'on']"
          @click="activeTab = t.key">
          {{ t.label }}
        </button>
      </div>

      <!-- ── 个人资料 ── -->
      <div v-show="activeTab === 'profile'" class="rise">
        <div class="card">
          <div class="card-title">编辑资料</div>
          <div class="field">
            <label class="field-label">昵称</label>
            <input v-model="profileForm.nickname" type="text" class="inp" placeholder="你的昵称">
          </div>
          <div class="field">
            <label class="field-label">邮箱</label>
            <input v-model="profileForm.email" type="email" class="inp" placeholder="your@email.com">
          </div>
          <div class="field">
            <label class="field-label">手机号</label>
            <input v-model="profileForm.phone" type="tel" class="inp" placeholder="13800000000">
          </div>
          <div class="field">
            <label class="field-label">个性签名</label>
            <textarea v-model="profileForm.signature" class="inp" placeholder="说点什么…"></textarea>
          </div>
          <div class="field">
            <label class="field-label">头像 URL</label>
            <input v-model="profileForm.avatar" type="url" class="inp" placeholder="https://…">
          </div>
          <div class="btn-row">
            <button class="btn-primary" :disabled="profileLoading" @click="saveProfile">
              <span v-if="profileLoading" class="spin"></span>
              {{ profileLoading ? '保存中…' : '保存修改' }}
            </button>
            <button class="btn-ghost" @click="resetProfile">重置</button>
          </div>
          <div v-if="profileMsg.text" :class="['msg', profileMsg.type]">{{ profileMsg.text }}</div>
        </div>
      </div>

      <!-- ── 修改密码 ── -->
      <div v-show="activeTab === 'password'" class="rise">
        <div class="card">
          <div class="card-title">修改密码</div>

          <div class="method-tabs">
            <button :class="['method-tab', pwMethod === 'old'  && 'on']" @click="pwMethod = 'old'">旧密码修改</button>
            <button :class="['method-tab', pwMethod === 'code' && 'on']" @click="pwMethod = 'code'">验证码重置</button>
          </div>

          <!-- Old-password flow -->
          <template v-if="pwMethod === 'old'">
            <div class="field">
              <label class="field-label">当前密码</label>
              <input v-model="pwForm.oldPassword" type="password" class="inp" placeholder="当前使用的密码">
            </div>
            <div class="field">
              <label class="field-label">新密码</label>
              <input v-model="pwForm.newPassword" type="password" class="inp" placeholder="6 – 32 位">
            </div>
            <div class="field">
              <label class="field-label">确认新密码</label>
              <input v-model="pwForm.confirm" type="password" class="inp" placeholder="再输入一遍">
            </div>
          </template>

          <!-- Email-code reset flow -->
          <template v-if="pwMethod === 'code'">
            <div class="field">
              <label class="field-label">邮箱</label>
              <div class="field-row">
                <input v-model="resetForm.email" type="email" class="inp" placeholder="your@email.com" :disabled="me && !!me.email">
                <button class="send-btn" :disabled="resetSending || resetCountdown > 0" @click="sendResetCode">
                  {{ resetCountdown > 0 ? `${resetCountdown}s` : (resetSending ? '发送中…' : resetCodeSent ? '重新发送' : '发送验证码') }}
                </button>
              </div>
            </div>
            <div class="field">
              <label class="field-label">验证码</label>
              <input v-model="resetForm.code" type="text" class="inp" placeholder="6 位验证码" maxlength="8">
            </div>
            <div class="field">
              <label class="field-label">新密码</label>
              <input v-model="resetForm.newPassword" type="password" class="inp" placeholder="6 – 32 位">
            </div>
            <div class="field">
              <label class="field-label">确认新密码</label>
              <input v-model="resetForm.confirm" type="password" class="inp" placeholder="再输入一遍">
            </div>
          </template>

          <div class="btn-row">
            <button class="btn-primary" :disabled="pwLoading" @click="savePassword">
              <span v-if="pwLoading" class="spin"></span>
              {{ pwLoading ? '提交中…' : (pwMethod === 'old' ? '修改密码' : '重置密码') }}
            </button>
          </div>
          <div v-if="pwMsg.text" :class="['msg', pwMsg.type]">{{ pwMsg.text }}</div>
        </div>
      </div>

      <!-- ── 搜索用户 ── -->
      <div v-show="activeTab === 'search'" class="rise">
        <div class="card">
          <div class="card-title">搜索用户</div>
          <div class="search-row">
            <input v-model="searchKeyword" type="text" class="inp"
              placeholder="昵称 / 用户名 / 邮箱 / 手机号"
              @keydown.enter="doSearch">
            <button class="btn-primary" :disabled="searchLoading" @click="doSearch">
              <span v-if="searchLoading" class="spin"></span>
              {{ searchLoading ? '搜索中' : '搜索' }}
            </button>
          </div>

          <div class="results">
            <div v-if="searchResults.length === 0 && searched" class="result-empty">
              没有找到匹配的用户
            </div>
            <div v-for="u in searchResults" :key="u.id" class="result-card">
              <UserAvatar :url="u.avatar" :name="u.nickname || u.username" :size="44" />
              <div class="result-info">
                <div class="result-name">{{ u.nickname || u.username }}</div>
                <div class="result-meta">{{ u.email }}{{ u.phone ? ' · ' + u.phone : '' }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi, authApi } from '../api/index.js'
import UserAvatar from '../components/UserAvatar.vue'

const router = useRouter()

const me          = ref(null)
const activeTab   = ref('profile')
const tabs        = [
  { key: 'profile',  label: '个人资料' },
  { key: 'password', label: '修改密码' },
  { key: 'search',   label: '搜索用户' }
]

const profileForm    = reactive({ nickname: '', email: '', phone: '', signature: '', avatar: '' })
const profileLoading = ref(false)
const profileMsg     = reactive({ text: '', type: '' })

const pwMethod   = ref('old')
const pwForm     = reactive({ oldPassword: '', newPassword: '', confirm: '' })
const pwLoading  = ref(false)
const pwMsg      = reactive({ text: '', type: '' })

const resetForm      = reactive({ email: '', code: '', newPassword: '', confirm: '' })
const resetSending   = ref(false)
const resetCountdown = ref(0)
const resetCodeSent  = ref(false)
let   resetTimer     = null

const searchKeyword = ref('')
const searchResults = ref([])
const searchLoading = ref(false)
const searched      = ref(false)

function setMsg(target, text, type) { target.text = text; target.type = type }

// ── Load profile ──
async function loadMe() {
  const data = await userApi.me()
  if (!data || data.code !== 200) return
  me.value = data.data
  resetProfile()
  resetForm.email = me.value.email || ''
}

function resetProfile() {
  if (!me.value) return
  profileForm.nickname  = me.value.nickname  || ''
  profileForm.email     = me.value.email     || ''
  profileForm.phone     = me.value.phone     || ''
  profileForm.signature = me.value.signature || ''
  profileForm.avatar    = me.value.avatar    || ''
}

// ── Save profile ──
async function saveProfile() {
  const body = {}
  if (profileForm.nickname)  body.nickname  = profileForm.nickname
  if (profileForm.email)     body.email     = profileForm.email
  if (profileForm.phone)     body.phone     = profileForm.phone
  if (profileForm.signature) body.signature = profileForm.signature
  if (profileForm.avatar)    body.avatar    = profileForm.avatar
  if (!Object.keys(body).length) return setMsg(profileMsg, '没有要修改的内容', 'err')

  profileLoading.value = true
  try {
    const data = await userApi.update(body)
    if (!data || data.code !== 200) { setMsg(profileMsg, data?.message || '保存失败', 'err'); return }
    me.value = { ...me.value, ...body }
    setMsg(profileMsg, '已保存 ✓', 'ok')
  } catch { setMsg(profileMsg, '网络错误', 'err') }
  finally  { profileLoading.value = false }
}

// ── Change password ──
async function savePassword() {
  if (pwMethod.value === 'code') {
    await savePasswordByCode()
  } else {
    await savePasswordByOld()
  }
}

async function savePasswordByOld() {
  if (!pwForm.oldPassword) return setMsg(pwMsg, '请输入当前密码', 'err')
  if (pwForm.newPassword.length < 6) return setMsg(pwMsg, '新密码至少 6 位', 'err')
  if (pwForm.newPassword !== pwForm.confirm) return setMsg(pwMsg, '两次输入不一致', 'err')

  pwLoading.value = true
  try {
    const data = await userApi.updatePassword(pwForm.oldPassword, pwForm.newPassword)
    if (!data || data.code !== 200) { setMsg(pwMsg, data?.message || '修改失败', 'err'); return }
    setMsg(pwMsg, '密码已修改，即将跳转登录…', 'ok')
    pwForm.oldPassword = ''; pwForm.newPassword = ''; pwForm.confirm = ''
    setTimeout(() => { localStorage.clear(); router.push('/auth') }, 1500)
  } catch { setMsg(pwMsg, '网络错误', 'err') }
  finally  { pwLoading.value = false }
}

async function savePasswordByCode() {
  const em = resetForm.email.trim()
  if (!em) return setMsg(pwMsg, '请输入邮箱', 'err')
  if (!resetForm.code) return setMsg(pwMsg, '请输入验证码', 'err')
  if (resetForm.newPassword.length < 6) return setMsg(pwMsg, '新密码至少 6 位', 'err')
  if (resetForm.newPassword !== resetForm.confirm) return setMsg(pwMsg, '两次输入不一致', 'err')

  pwLoading.value = true
  try {
    const data = await userApi.resetPassword(em, resetForm.code, resetForm.newPassword)
    if (!data || data.code !== 200) { setMsg(pwMsg, data?.message || '重置失败', 'err'); return }
    setMsg(pwMsg, '密码已重置，即将跳转登录…', 'ok')
    resetForm.code = ''; resetForm.newPassword = ''; resetForm.confirm = ''
    setTimeout(() => { localStorage.clear(); router.push('/auth') }, 1500)
  } catch { setMsg(pwMsg, '网络错误', 'err') }
  finally  { pwLoading.value = false }
}

async function sendResetCode() {
  const em = resetForm.email.trim()
  if (!em || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(em)) return setMsg(pwMsg, '请输入合法的邮箱地址', 'err')

  resetSending.value = true
  try {
    const data = await authApi.sendCode(em)
    if (!data || data.code !== 200) { setMsg(pwMsg, data?.message || '发送失败', 'err'); return }
    setMsg(pwMsg, '验证码已发送，请查收邮件', 'ok')
    resetCodeSent.value = true
    resetCountdown.value = 60
    resetTimer = setInterval(() => {
      resetCountdown.value--
      if (resetCountdown.value <= 0) { clearInterval(resetTimer); resetTimer = null }
    }, 1000)
  } catch { setMsg(pwMsg, '网络错误', 'err') }
  finally  { resetSending.value = false }
}

// ── Search ──
async function doSearch() {
  const kw = searchKeyword.value.trim()
  if (!kw) return
  searchLoading.value = true
  searched.value = false
  try {
    const data = await userApi.search(kw)
    searchResults.value = (data?.code === 200 && Array.isArray(data.data)) ? data.data : []
    searched.value = true
  } catch { searchResults.value = []; searched.value = true }
  finally  { searchLoading.value = false }
}

// ── Logout ──
async function doLogout() {
  try { await authApi.logout() } catch { /* ignore */ }
  localStorage.clear()
  router.push('/auth')
}

onMounted(loadMe)
onUnmounted(() => { if (resetTimer) clearInterval(resetTimer) })
</script>

<style scoped>
/* ── Topbar ── */
.topbar {
  position: sticky; top: 0; z-index: 100;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 32px; height: 60px;
  background: rgba(8,8,15,.88);
  backdrop-filter: blur(24px);
  border-bottom: 1px solid var(--border);
}
.logo {
  font-family: 'Cormorant Garamond', serif;
  font-size: 22px; font-weight: 300; letter-spacing: .06em;
  background: linear-gradient(135deg, #fff, rgba(244,114,182,.8));
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}
.topbar-right { display: flex; align-items: center; gap: 16px; }
.topbar-user  { display: flex; align-items: center; gap: 10px; font-size: 14px; }
.topbar-user strong { color: var(--text); font-weight: 400; }
.logout-btn {
  padding: 7px 16px; background: transparent;
  border: 1px solid var(--border); border-radius: 8px;
  color: var(--muted);
  font-family: 'DM Sans', sans-serif; font-size: 13px;
  cursor: pointer; transition: all .18s;
}
.logout-btn:hover { border-color: rgba(251,113,133,.35); color: var(--err); }

/* ── Page ── */
.page { max-width: 680px; margin: 0 auto; padding: 40px 24px 80px; }

/* ── Hero ── */
.hero {
  background: var(--surface); border: 1px solid var(--border);
  border-radius: 20px; padding: 36px;
  display: flex; align-items: center; gap: 28px;
  margin-bottom: 24px; backdrop-filter: blur(20px);
  position: relative; overflow: hidden;
}
.hero::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 2px;
  background: linear-gradient(90deg, var(--violet), var(--rose), transparent);
}
.hero-info { flex: 1; min-width: 0; }
.hero-name {
  font-family: 'Cormorant Garamond', serif;
  font-size: 28px; font-weight: 400; letter-spacing: .02em;
}
.hero-email { margin-top: 4px; font-size: 13px; color: var(--muted); font-family: 'JetBrains Mono', monospace; }
.hero-sig   { margin-top: 10px; font-size: 13px; color: rgba(226,226,240,.5); font-style: italic; }

/* Skeleton */
.skeleton { opacity: .4; }
.skeleton-avatar { width: 80px; height: 80px; border-radius: 50%; background: rgba(255,255,255,.08); flex-shrink: 0; }
.skeleton-lines  { display: flex; flex-direction: column; gap: 12px; }
.skeleton-line   { height: 14px; border-radius: 6px; background: rgba(255,255,255,.08); }
.w60 { width: 60%; }
.w40 { width: 40%; }

/* ── Tab bar ── */
.tab-bar {
  display: flex; background: var(--input-bg); border: 1px solid var(--border);
  border-radius: 12px; padding: 4px; margin-bottom: 20px;
}
.tab-btn {
  flex: 1; padding: 10px; border: none; background: transparent;
  color: var(--muted); font-family: 'DM Sans', sans-serif; font-size: 14px;
  cursor: pointer; border-radius: 9px; transition: all .18s;
}
.tab-btn.on { background: var(--rose-dim); color: var(--rose); }

/* ── Card ── */
.card {
  background: var(--surface); border: 1px solid var(--border);
  border-radius: 16px; padding: 28px 28px 32px;
  backdrop-filter: blur(20px);
}
.card-title {
  font-family: 'Cormorant Garamond', serif;
  font-size: 20px; font-weight: 400; letter-spacing: .02em;
  margin-bottom: 24px;
}

/* ── Form ── */
.field { margin-bottom: 18px; }
.field-label {
  display: block; font-size: 11px; letter-spacing: .09em; text-transform: uppercase;
  color: var(--muted); margin-bottom: 8px; font-weight: 500;
}
.field-row { display: flex; gap: 8px; }
.btn-row { display: flex; gap: 10px; margin-top: 24px; }

.method-tabs {
  display: flex; margin-bottom: 20px;
  background: var(--input-bg); border: 1px solid var(--border); border-radius: 8px; padding: 3px;
}
.method-tab {
  flex: 1; padding: 6px; border: none; background: transparent;
  color: var(--muted); font-family: 'DM Sans', sans-serif; font-size: 12px;
  cursor: pointer; border-radius: 6px; transition: all .18s;
}
.method-tab.on { background: var(--rose-dim); color: var(--rose); }

.send-btn {
  flex-shrink: 0; padding: 0 16px; min-width: 100px;
  background: var(--rose-dim); border: 1px solid rgba(244,114,182,.22);
  border-radius: 10px; color: var(--rose);
  font-family: 'DM Sans', sans-serif; font-size: 13px;
  cursor: pointer; transition: background .18s;
}
.send-btn:hover:not(:disabled) { background: rgba(244,114,182,.2); }
.send-btn:disabled { opacity: .45; cursor: not-allowed; }

/* ── Search ── */
.search-row { display: flex; gap: 10px; margin-bottom: 16px; }
.search-row .inp { flex: 1; }

.results { display: flex; flex-direction: column; gap: 10px; }
.result-empty { text-align: center; padding: 32px; color: var(--muted); font-size: 14px; }
.result-card {
  display: flex; align-items: center; gap: 14px; padding: 14px 16px;
  background: var(--input-bg); border: 1px solid var(--border); border-radius: 12px;
  transition: border-color .18s;
}
.result-card:hover { border-color: rgba(244,114,182,.22); }
.result-info { flex: 1; min-width: 0; }
.result-name { font-size: 14px; font-weight: 500; }
.result-meta { font-size: 12px; color: var(--muted); margin-top: 2px; font-family: 'JetBrains Mono', monospace; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
