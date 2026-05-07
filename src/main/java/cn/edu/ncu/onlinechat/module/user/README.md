# 用户模块 (user)

**负责成员:** ________

## 涉及文件

```
user/
├── entity/User.java                    数据库实体
├── mapper/UserMapper.java              MyBatis 接口（resources/mapper/user/ 放 XML）
├── controller/UserController.java      GET/PUT /users/me, /users/search
├── service/UserService.java            接口
├── service/impl/UserServiceImpl.java   实现（TODO 待填）
├── dto/UserUpdateDTO.java             修改个人资料
├── dto/PasswordUpdateDTO.java         修改密码
└── vo/UserVO.java                     返回给前端的用户信息（不含密码）
```

## 你的 TODO

1. **UserMapper XML** — 在 `resources/mapper/user/UserMapper.xml` 写 SQL
2. **`UserServiceImpl.getById`** — 简单查库
3. **`UserServiceImpl.getProfile`** — 查库转 UserVO
4. **`UserServiceImpl.search`** — 按 username/nickname 模糊查
5. **`UserServiceImpl.updateProfile`** — 更新昵称/头像/邮箱等字段
6. **`UserServiceImpl.updatePassword`** — 先验旧密码再加密更新

## 注意事项

- UserVO 绝对不能包含 password 字段
- 搜索建议加 LIMIT 防止结果过多
