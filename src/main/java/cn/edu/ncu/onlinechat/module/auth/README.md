# 登录注册模块 (auth)

**负责成员:** ________

## 功能概览

- 短信验证码发送/校验（阿里云 Dypnsapi）
- 手机号注册/登录（验证码登录，首次登录自动注册）
- JWT 生成与返回
- 注册时自动创建默认好友分组（"我的好友"）
- 短信发送频控（按手机号限流）

## 涉及文件

```
auth/
├── controller/AuthController.java      POST /auth/sms/send, /auth/login, /auth/register, /auth/logout
├── service/AuthService.java            接口
├── service/SmsVerifyService.java       短信验证码接口
├── service/impl/AuthServiceImpl.java   登录/注册实现
├── service/impl/AliyunSmsVerifyService.java  阿里云短信实现
├── dto/LoginDTO.java                   phone, code
├── dto/RegisterDTO.java                phone, code, nickname
├── dto/SmsSendDTO.java                 phone
├── vo/LoginVO.java                     token + 用户信息
└── vo/SmsSendVO.java                   bizId, requestId
```

## 接口说明

接口示例与字段结构可在 Swagger UI 查看：`/api/swagger-ui.html`。

### 发送验证码

`POST /auth/sms/send`

请求体：

```json
{
	"phone": "13800138000"
}
```

返回：`bizId` + `requestId`（部分场景可能为空，取决于短信平台返回）。

### 短信注册

`POST /auth/register`

```json
{
	"phone": "13800138000",
	"code": "1234",
	"nickname": "Nick"
}
```

### 短信登录

`POST /auth/login`

```json
{
	"phone": "13800138000",
	"code": "1234"
}
```

### 登出

`POST /auth/logout`

## 配置项

`application.yaml` 中使用以下配置：

```yaml
aliyun:
	dypnsapi:
		endpoint: dypnsapi.aliyuncs.com
		access-key-id: ${ALIBABA_CLOUD_ACCESS_KEY_ID}
		access-key-secret: ${ALIBABA_CLOUD_ACCESS_KEY_SECRET}
		scheme-name: ${ALIYUN_SMS_SCHEME_NAME}
		sign-name: ${ALIYUN_SMS_SIGN_NAME}
		template-code: ${ALIYUN_SMS_TEMPLATE_CODE}
		country-code: 86
		code-length: 6
		valid-time: 300
		interval: 60
		return-verify-code: false
```

## 校验与风控

- `phone`：`^1\d{10}$`（11 位大陆手机号）
- `code`：长度 4-8
- 发送频控：同一手机号在 `interval` 秒内只允许发送一次（Redis）

## 备注

- 由于使用短信验证码登录，`t_user.password` 在注册时会自动生成随机密码并加密存储。
- 登出与 token 黑名单、在线状态同步目前未实现，如需可补 Redis 存储。

## 依赖的外部接口

- `UserMapper` — selectByUsername, selectByPhone, insert
- `FriendGroupMapper` — insert（注册时建默认分组）
- `JwtUtil` — generateToken, parseUserId
- `PasswordEncoder` — encode
- `StringRedisTemplate` — 短信发送频控
