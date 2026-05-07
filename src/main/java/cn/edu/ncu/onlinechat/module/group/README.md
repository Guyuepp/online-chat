# 群聊模块 (group)

**负责成员:** ________

## 涉及文件

```
group/
├── entity/ChatGroup.java                群实体
├── entity/GroupMember.java              群成员实体（role: OWNER/ADMIN/MEMBER）
├── mapper/ChatGroupMapper.java
├── mapper/GroupMemberMapper.java
├── controller/GroupController.java      建群/解散/邀请/踢人/退群/设角色/搜群/成员列表
├── service/GroupService.java
├── service/impl/GroupServiceImpl.java
├── dto/ CreateGroupDTO, UpdateGroupDTO, InviteMemberDTO
└── vo/  GroupVO, GroupMemberVO
```

## 你的 TODO

1. **`create`** — 建群 → 群主自动以 OWNER 身份入群
2. **`update`** — 仅 OWNER/ADMIN 可改群名/头像/简介
3. **`dismiss`** — 仅 OWNER 可解散 → 删群 + 删所有成员 + **删相关消息**（慎重）
4. **`getById` / `myGroups` / `search`** — 常规查群
5. **`inviteMembers`** — 邀请者必须是群成员 → 被邀请者必须是好友 → 批量插入成员
6. **`removeMember`** — 仅 OWNER/ADMIN → 不能踢 OWNER
7. **`leave`** — OWNER 不能直接退（需先转让群主或解散）
8. **`members`** — 群成员列表，联表取用户头像昵称
9. **`setRole`** — 仅 OWNER 可设/撤 ADMIN

## 权限矩阵

| 操作 | OWNER | ADMIN | MEMBER |
|---|---|---|---|
| 改群资料 | ✓ | ✓ | ✗ |
| 解散群 | ✓ | ✗ | ✗ |
| 邀请成员 | ✓ | ✓ | ✓ |
| 踢人 | ✓ | ✓ | ✗ |
| 设管理员 | ✓ | ✗ | ✗ |
| 退群 | ✗ (需先转让) | ✓ | ✓ |

## 依赖

- `UserMapper` — 查用户信息
- `FriendMapper` — 邀请时校验好友关系
- `MessageMapper` — 解散群时删消息
