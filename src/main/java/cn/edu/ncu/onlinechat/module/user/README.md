# 用户模块 (user)

**负责成员:** ________

## 功能概览

- 查看自己的资料（UserVO，不含密码）
- 搜索用户（按 username / nickname / email / phone 模糊匹配，上限 20 条）
- 修改个人资料（昵称 / 头像 / 邮箱 / 手机 / 签名，仅更新非空字段）
- 修改密码（校验旧密码，BCrypt 加密新密码后存储）

## 涉及文件

```
user/
├── entity/User.java                    数据库实体（不含密码之外的敏感字段）
├── mapper/UserMapper.java              MyBatis 接口
├── controller/UserController.java      GET/PUT /users/me, /users/search
├── service/UserService.java            接口
├── service/impl/UserServiceImpl.java   实现
├── dto/UserUpdateDTO.java              修改资料请求体（可选字段）
├── dto/PasswordUpdateDTO.java          修改密码请求体
└── vo/UserVO.java                      返回给前端的用户信息（不含 password）
```

## 接口说明

接口示例与字段结构可在 Swagger UI 查看：`/api/swagger-ui.html`。

### 获取自己的资料

`GET /users/me`

需要登录。返回当前用户的 `UserVO`。

### 搜索用户

`GET /users/search?keyword=xxx`

需要登录。返回匹配的用户列表（最多 20 条）。

### 修改资料

`PUT /users/me`

```json
{
	"nickname": "新昵称",
	"avatar": "https://...",
	"email": "new@example.com",
	"phone": "13800138000",
	"signature": "新签名"
}
```

仅更新非空字段，其余保持不变。需要登录。

### 修改密码

`PUT /users/me/password`

```json
{
	"oldPassword": "当前密码",
	"newPassword": "新密码（6-32 位）"
}
```

先校验 oldPassword 是否正确，正确则加密存储 newPassword。需要登录。

## 依赖的外部接口

- `UserMapper` — selectById, searchByKeyword, update, updatePassword
- `PasswordEncoder` — matches（校验旧密码）, encode（新密码）
