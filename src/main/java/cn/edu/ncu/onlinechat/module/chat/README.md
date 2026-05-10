# 消息模块 (chat)

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
└── ws/
    ├── ChatWebSocketHandler.java         WebSocket 连接入口
    ├── WebSocketSessionManager.java      Session 池 + Redis 在线状态 + 离线队列
    ├── MessageDispatcher.java            消息解析与路由分发
    └── protocol/
        ├── WsInboundMessage.java         入站 JSON 协议
        ├── WsOutboundMessage.java        出站 JSON 协议
        └── WsMessageType.java            消息类型枚举
```

## 连接方式

```
ws://localhost:8080/api/ws/chat?token=<jwt_token>
```

## 消息协议（JSON）

入站（客户端 → 服务端）：
```json
{
  "type": "chat",
  "chatType": "PRIVATE",
  "toTargetId": 123,
  "messageType": "TEXT",
  "content": "hello"
}
```

出站（服务端 → 客户端）：
```json
{
  "type": "chat",
  "chatType": "PRIVATE",
  "messageType": "TEXT",
  "fromUserId": 456,
  "toTargetId": 123,
  "content": "hello",
  "createTime": "2026-05-10T12:00:00"
}
```

支持的 type：`chat` | `mark_read` | `ping` | `pong` | `ack` | `error`

## 实现状态

### WebSocket ✅

- 连接建立时 JWT 认证 + 注册在线状态（Redis）
- 心跳 ping/pong
- 私聊消息实时推送（对方在线）或暂存离线队列（对方离线）
- 用户重连时自动推送离线消息
- 连接断开自动清理在线状态
- 支持多端登录（同一用户多个 session）

### REST（待实现）

1. **`ChatServiceImpl.send`** — 消息落库 MySQL → 通过 WebSocket 推送给在线接收方 → 接收方离线则存 Redis 离线队列
2. **`ChatServiceImpl.getHistory`** — 按 `beforeId` 倒序翻页（只让私聊双方/群成员查看）
3. **`markRead`** — 把对方（私聊）或群里（群聊）的未读消息标记为已读

### 待接入

- 好友关系校验（私聊时检查是否为好友）
- 群聊成员查询与群发
- 消息内容过滤/敏感词
