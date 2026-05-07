# 消息模块 (chat)

**负责成员:** ________

最核心模块，涉及 HTTP REST + WebSocket 实时推送。

## 涉及文件

```
chat/
├── entity/Message.java                   消息实体
├── mapper/MessageMapper.java            MyBatis 接口
├── controller/ChatController.java       POST /messages, GET 历史, PUT 已读
├── service/ChatService.java
├── service/impl/ChatServiceImpl.java
├── dto/SendMessageDTO.java              chatType, messageType, targetId, content, file
├── vo/MessageVO.java                    含发信人信息的前端展示对象
└── ws/ChatWebSocketHandler.java         WebSocket 核心处理器
```

## 你的 TODO

### REST 部分

1. **`ChatServiceImpl.send`** — 消息落库 MySQL → 通过 WebSocket 推送给在线接收方 → 接收方离线则存 Redis 离线队列
2. **`ChatServiceImpl.getHistory`** — 按 `beforeId` 倒序翻页（只让私聊双方/群成员查看）
3. **`markRead`** — 把对方（私聊）或群里（群聊）的未读消息标记为已读

### WebSocket 部分 (`ChatWebSocketHandler`)

这是**最难的部分**，需要处理:

```
连接 → ws://localhost:8080/api/ws/chat?token=xxx

消息协议（JSON）:
{
  "type": "chat",           // chat | mark_read | ping | pong
  "chatType": "PRIVATE",    // PRIVATE | GROUP
  "toTargetId": 123,        // 接收者（用户ID 或 群ID）
  "messageType": "TEXT",    // TEXT | IMAGE | FILE | VOICE
  "content": "hello"
}
```

**连接管理:**
- `afterConnectionEstablished` — 从 URL param 拿 token → 解析 userId → Redis 设在线状态 → session 存入 ConcurrentHashMap
- `afterConnectionClosed` — Redis 删在线状态 → 从 ConcurrentHashMap 移除

**消息路由:**
- 私聊 → 对方在线就 `session.sendMessage()` 推送；不在线就存 Redis 离线队列
- 群聊 → 查 GroupMemberMapper 拿到所有成员 → 逐个检查在线状态 → 推送在线者 → 离线者存队列

**心跳:**
- 客户端定时发 `{"type":"ping"}` → 服务端回复 `{"type":"pong"}` → 超时无心跳断开

## 依赖

- `MessageMapper` — 消息 CRUD
- `GroupMemberMapper` — 群发时查群成员
- `FriendMapper` — 私聊时校验好友关系
- `RedisTemplate` — 在线状态、离线消息队列
- `JwtUtil` — WebSocket 连接时验证 token
