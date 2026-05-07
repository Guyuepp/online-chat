# 配置层 (config)

**负责成员:** ________（建议 user 模块的同学兼管）

## 内容

| 文件 | 职责 |
|---|---|
| `SecurityConfig.java` | Spring Security 无状态配置：白名单 `/auth/**` `/ws/**` `/swagger-ui/**`，其余需认证，CORS 开启，CSRF 关闭 |
| `WebSocketConfig.java` | 注册 `/ws/chat` 的 WebSocket handler，允许所有来源 |
| `WebMvcConfig.java` | 全局 CORS 配置 |
| `MybatisConfig.java` | MapperScan 扫描所有模块的 mapper 包 |
| `RedisConfig.java` | RedisTemplate 序列化（JSON） |
| `OpenApiConfig.java` | Swagger UI 配置 + Bearer JWT 全局鉴权 |

## application.yaml 关键项

- `server.servlet.context-path: /api` — 所有接口统一 `/api` 前缀
- `jwt.secret` — **上线前务必换成随机长字符串（至少 32 字节）**
- `jwt.expiration: 7200000` — token 2 小时过期
- 数据库/Redis 连接按你的环境改

## 注意事项

- `SecurityConfig.WHITELIST` 如果需要放行新路径，改这里
- Redis 不用就把 `spring-data-redis` 依赖去掉，不然启动报连不上
