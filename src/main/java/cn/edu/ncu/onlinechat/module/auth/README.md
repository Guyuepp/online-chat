# 登录注册模块 (auth)

**负责成员:** ________

## 涉及文件

```
auth/
├── controller/AuthController.java      POST /auth/login, /auth/register, /auth/logout
├── service/AuthService.java            接口
├── service/impl/AuthServiceImpl.java   实现（TODO 待填）
├── dto/LoginDTO.java                   username, password
├── dto/RegisterDTO.java                username, password, nickname
└── vo/LoginVO.java                     token + 用户信息
```

## 你的 TODO

1. **`AuthServiceImpl.login`** — 查 UserMapper 验密码 → 调 JwtUtil 生成 token → 把 token 存 Redis（在线标记）→ 返回 LoginVO
2. **`AuthServiceImpl.register`** — 校验重名 → BCrypt 加密密码 → 插入 user 表 → 初始化默认好友分组 → 自动登录返回 token
3. **`AuthServiceImpl.logout`** — 删 Redis 里的 token + 在线状态
4. **`security/JwtUtil`** — 用 jjwt 实现 `generateToken` / `parseUserId` / `parseUsername` / `isValid`，从 yaml 读 secret/expiration
5. **`security/JwtAuthenticationFilter`** — 从 request header 取 Bearer token → 解析 → 查 SecurityUserDetailsService → 注入 SecurityContextHolder

## 依赖的外部接口

- `UserMapper` — selectByUsername, insert
- `FriendGroupMapper` — insert（注册时建默认分组）
- `JwtUtil` — generateToken, parseUserId
- `PasswordEncoder` — encode, matches
- `RedisTemplate` — 存/删 token 和在线状态
