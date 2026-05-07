# 好友模块 (friend)

**负责成员:** ________

业务逻辑最重的一个模块，包含三层子功能。

## 子功能划分

### 1. 好友关系 (FriendService / FriendController)

```
controller/FriendController.java     GET /friends, PUT /friends/{id}/group, PUT /friends/{id}/remark, DELETE /friends/{id}
entity/Friend.java                   id, userId, friendId, groupId, remark
mapper/FriendMapper.java
service/FriendService.java
service/impl/FriendServiceImpl.java
dto/ FriendMoveDTO, FriendRemarkDTO
vo/   FriendVO
```

TODO:
- `list` — 查用户所有好友，联表拿对方信息 + 在线状态（Redis）
- `moveToGroup` — 改好友的 groupId
- `updateRemark` — 改备注
- `delete` — **双向删除**：删两条 Friend 记录（userId→friendId 和 friendId→userId）

### 2. 好友分组 (FriendGroupService / FriendGroupController)

```
controller/FriendGroupController.java  CRUD /friend-groups
entity/FriendGroup.java                id, userId, name, sortOrder
mapper/FriendGroupMapper.java
service/FriendGroupService.java
service/impl/FriendGroupServiceImpl.java
dto/   FriendGroupDTO
vo/    FriendGroupVO (含组内好友列表)
```

TODO:
- `listWithFriends` — 查所有分组 + 每组下的好友（拼成树）
- `create` — 新建空分组
- `rename` — 改名
- `delete` — 删分组前，把组内好友移到"我的好友"默认分组（**不要直接删好友**）

### 3. 好友申请 (FriendRequestService / FriendRequestController)

```
controller/FriendRequestController.java  POST /friend-requests, accept/reject/resend
entity/FriendRequest.java                id, fromUserId, toUserId, message, status
mapper/FriendRequestMapper.java
service/FriendRequestService.java
service/impl/FriendRequestServiceImpl.java
dto/   FriendRequestSendDTO
vo/    FriendRequestVO
```

TODO:
- `send` — 校验：目标存在、未拉黑、非好友、无待处理申请 → 插入 PENDING
- `accept` — 改 ACCEPTED → 双方各插一条 Friend（默认分组）→ 初始化双向聊天关系
- `reject` — 改 REJECTED
- `resend` — 旧 PENDING 过期后重新发送（要么改状态，要么删旧建新）
- `incoming / outgoing` — 分页查申请列表，联表拿对方头像昵称

## 依赖

- `UserMapper` — 校验用户存在、查对方信息
- `FriendGroupMapper` — accept 时取默认分组 ID
- `RedisTemplate` — 查在线状态
