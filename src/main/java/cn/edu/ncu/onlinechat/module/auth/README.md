# 登录注册模块 (auth)

**负责成员:** ________

## 功能概览

- 邮箱验证码发送/校验（Spring Boot Mail + Redis）
- 邮箱注册/登录（验证码登录，首次登录自动注册）
- JWT 生成与返回
- 注册时自动创建默认好友分组（"我的好友"）
- 邮件发送频控（按邮箱限流）

## 涉及文件

```
auth/
├── controller/AuthController.java      POST /auth/email/send, /auth/login, /auth/register, /auth/logout
├── service/AuthService.java            接口
├── service/VerifyCodeService.java      邮箱验证码接口
├── service/impl/AuthServiceImpl.java   登录/注册实现
├── service/impl/MailVerifyService.java SMTP 邮件实现
├── dto/LoginDTO.java                   email, code
├── dto/RegisterDTO.java                email, code, nickname
├── dto/EmailSendDTO.java               email
├── vo/LoginVO.java                     token + 用户信息
└── vo/EmailSendVO.java                 requestId, expiresIn
```

## 接口说明

接口示例与字段结构可在 Swagger UI 查看：`/api/swagger-ui.html`。

### 发送验证码

`POST /auth/email/send`

请求体：

```json
{
	"email": "user@example.com"
}
```

返回：`requestId` + `expiresIn`（有效期秒数）。

### 邮箱注册

`POST /auth/register`

```json
{
	"email": "user@example.com",
	"code": "1234",
	"nickname": "Nick"
}
```

### 邮箱登录

`POST /auth/login`

```json
{
	"email": "user@example.com",
	"code": "1234"
}
```

### 登出

`POST /auth/logout`

## 配置项

`application.yaml` 中使用以下配置：

```yaml
spring:
	mail:
		host: ${SMTP_HOST}
		port: ${SMTP_PORT}
		username: ${SMTP_USERNAME}
		password: ${SMTP_PASSWORD}
		protocol: smtps

mail:
	verify:
		subject: "你的验证码来啦 | Online Chat"
		from: ${SMTP_FROM}
		template: "嗨~ 你的验证码是 {code}，{minutes} 分钟内有效。非本人操作请忽略。"
		code-length: 6
		valid-time: 300
		interval: 60
		ip-interval: 60
		max-fail-times: 5
		lock-time: 600
```

## 校验与风控

- `email`：合法邮箱格式
- `code`：长度 4-8
- 发送频控：同一邮箱在 `interval` 秒内只允许发送一次（Redis）
- IP 频控：同一 IP 在 `ip-interval` 秒内只允许发送一次
- 验证失败：同一邮箱累计 `max-fail-times` 次失败后锁定 `lock-time` 秒

## 备注

- 由于使用邮箱验证码登录，`t_user.password` 在注册时会自动生成随机密码并加密存储。
- 登出与 token 黑名单、在线状态同步目前未实现，如需可补 Redis 存储。

## 依赖的外部接口

- `UserMapper` — selectByUsername, selectByEmail, insert
- `FriendGroupMapper` — insert（注册时建默认分组）
- `JwtUtil` — generateToken, parseUserId
- `PasswordEncoder` — encode
- `JavaMailSender` — 发送验证邮件
- `StringRedisTemplate` — 邮件发送频控 + 验证码缓存
