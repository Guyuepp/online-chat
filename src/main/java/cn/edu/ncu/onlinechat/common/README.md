# 通用层 (common)

**全局共享，所有人可改，但改之前通知组员。**

## 内容

| 文件 | 用途 |
|---|---|
| `result/Result.java` | 统一 API 返回体 `{code, message, data}` |
| `result/ResultCode.java` | 错误码枚举：成功/参数错/未登录/无权限/各业务错误码 |
| `result/PageResult.java` | 分页返回体 |
| `exception/BusinessException.java` | 业务异常，抛出后由 GlobalExceptionHandler 转为 Result |
| `exception/GlobalExceptionHandler.java` | 全局异常捕获，拦截 Business/Validation/Auth/未知异常 |
| `enums/ChatType.java` | PRIVATE / GROUP |
| `enums/MessageType.java` | TEXT / IMAGE / FILE / VOICE / SYSTEM |
| `enums/FriendRequestStatus.java` | PENDING / ACCEPTED / REJECTED / EXPIRED |
| `enums/GroupRole.java` | OWNER / ADMIN / MEMBER |
| `constant/Constants.java` | 系统常量（JWT 头名前缀、默认分组名等） |
| `constant/RedisKeyConstant.java` | Redis Key 前缀约定 |
