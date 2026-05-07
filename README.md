# 在线聊天系统

Spring Boot 4.0 + Java 21 大作业项目。

## 启动

使用 IntelliJ IDEA 配置好环境后启动。

- API 文档: <http://localhost:8080/api/swagger-ui.html>
- WebSocket: `ws://localhost:8080/api/ws/chat`

## 项目结构

```
common/        通用：统一返回体、异常、枚举、常量
config/        配置：Security、WebSocket、CORS、Redis、MyBatis、Swagger
security/      认证：JWT 签发校验、SecurityUser、登录拦截
module/
  auth/        登录注册
  user/        用户资料
  friend/      好友关系、好友分组、好友申请
  group/       群聊、群成员
  chat/        消息收发、历史记录、WebSocket 推送
```

## 技术栈

| 层   | 选型                                |
|-----|-----------------------------------|
| 框架  | Spring Boot 4.0.6                 |
| 安全  | Spring Security + JWT (jjwt 0.12) |
| ORM | MyBatis                           |
| 缓存  | Redis                             |
| 实时  | 原生 WebSocket                      |
| 文档  | SpringDoc OpenAPI                 |

## 系统架构

```mermaid
flowchart TB
    subgraph 前端
        Browser[浏览器\nWebSocket + HTTP]
    end

    subgraph 网关层
        Security[Spring Security\nJWT 无状态认证]
        CORS[全局跨域]
    end

    subgraph 业务层
        Auth[登录注册模块]
        User[用户模块]
        Friend[好友模块\n关系 / 分组 / 申请]
        Group[群聊模块]
        Chat[消息模块\nREST + WebSocket]
    end

    subgraph 基础设施
        MySQL[(MySQL\n持久化存储)]
        Redis[(Redis\n在线状态 / Token / 离线消息)]
    end

    Browser -->|HTTP| Security
    Browser -->|WS| Chat
    Security --> Auth
    Security --> User
    Security --> Friend
    Security --> Group
    Security --> Chat

    Auth --> MySQL
    Auth --> Redis
    User --> MySQL
    Friend --> MySQL
    Friend --> Redis
    Group --> MySQL
    Chat --> MySQL
    Chat --> Redis
```

## 数据库 ER 图

```mermaid
erDiagram
    t_user {
        bigint id PK "用户ID"
        varchar username UK "登录名"
        varchar password "BCrypt 密文"
        varchar nickname "昵称"
        varchar avatar "头像 URL"
        varchar email "邮箱"
        varchar phone "手机号"
        varchar signature "个性签名"
        tinyint status "0正常 1禁用"
    }

    t_friend_group {
        bigint id PK "分组ID"
        bigint user_id "所属用户ID"
        varchar name "分组名称"
        int sort_order "排序"
    }

    t_friend {
        bigint id PK "关系ID"
        bigint user_id "用户ID"
        bigint friend_id "好友用户ID"
        bigint group_id FK "所属分组ID"
        varchar remark "备注名"
    }

    t_friend_request {
        bigint id PK "申请ID"
        bigint from_user_id "发起方"
        bigint to_user_id "接收方"
        varchar message "附言"
        varchar status "PENDING/ACCEPTED/REJECTED/EXPIRED"
    }

    t_chat_group {
        bigint id PK "群ID"
        varchar name "群名称"
        varchar avatar "群头像"
        varchar description "群简介"
        bigint owner_id FK "群主用户ID"
    }

    t_group_member {
        bigint id PK "记录ID"
        bigint group_id FK "群ID"
        bigint user_id FK "用户ID"
        varchar role "OWNER/ADMIN/MEMBER"
        varchar nickname "群内昵称"
    }

    t_message {
        bigint id PK "消息ID"
        varchar chat_type "PRIVATE/GROUP"
        varchar message_type "TEXT/IMAGE/FILE/VOICE/SYSTEM"
        bigint from_user_id FK "发送方"
        bigint to_target_id "接收方ID或群ID"
        text content "消息正文"
        varchar file_url "文件 URL"
        varchar file_name "文件名"
        bigint file_size "文件大小"
        tinyint is_read "0未读 1已读"
    }

    t_user ||--o{ t_friend : "双向各存一条"
    t_user ||--o{ t_friend_request : "发起 / 接收"
    t_user ||--o{ t_friend_group : "拥有"
    t_user ||--o{ t_group_member : "加入"
    t_user ||--o{ t_message : "发送"
    t_friend_group ||--o{ t_friend : "包含"
    t_chat_group ||--o{ t_group_member : "拥有"
    t_chat_group ||--o{ t_message : "群消息"
```

### 关键设计说明

- **好友双向存储**: A 加 B 为好友，`t_friend` 插入 `(A,B)` 和 `(B,A)` 两条记录。好处是查"我的好友"只需 `WHERE user_id = ?`，不用反向 UNION。删好友时两条一起删。
- **分组归用户私有**: 每个用户管理自己的分组，注册时自动创建默认分组"我的好友"。
- **消息不分表**: 私聊和群聊共用 `t_message`，靠 `chat_type` + `to_target_id` 区分。核心查询索引为 `(chat_type, to_target_id, create_time DESC)`。
- **完整建表 SQL**: 见 `src/main/resources/sql/schema.sql`。

## API 约定

### 统一返回体

所有 HTTP 接口返回以下格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- `code=200` 成功，其余见 `ResultCode` 枚举
- 401 → 未登录或 token 过期，前端需跳转登录页
- 接口文档：启动后访问 `/api/swagger-ui.html`

### 认证方式

```
Authorization: Bearer <jwt_token>
```

白名单路径（无需 token）：`/auth/**`、`/ws/**`、`/swagger-ui/**`

### WebSocket

连接时带 token 参数：

```
ws://localhost:8080/api/ws/chat?token=<jwt_token>
```

消息格式：

```json
{
  "type": "chat",
  "chatType": "PRIVATE",
  "toTargetId": 123,
  "messageType": "TEXT",
  "content": "hello"
}
```

## 模块分工

| 模块              | 要点                     |
|-----------------|------------------------|
| auth + security | JWT 签发/校验、登录注册、安全配置    |
| user + config   | 用户资料 CRUD、配置调优         |
| friend          | 好友关系 + 分组 + 申请，三层子功能   |
| group           | 群 CRUD + 成员管理 + 权限控制   |
| chat            | 消息持久化 + WebSocket 实时推送 |

各模块 README 在对应 `module/*/README.md`，含完整 TODO 清单。
