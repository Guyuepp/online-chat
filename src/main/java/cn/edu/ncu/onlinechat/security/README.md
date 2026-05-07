# 安全模块 (security)

**负责成员:** ________（建议 auth 模块同学实现）

## 涉及文件

```
security/
├── JwtProperties.java              @ConfigurationProperties 从 yaml 读 jwt.*
├── JwtUtil.java                    JWT 生成/解析/校验（TODO）
├── JwtAuthenticationFilter.java    OncePerRequestFilter：从 header 取 token 注入 SecurityContext（TODO）
├── SecurityUser.java               implements UserDetails，包裹 User 实体
├── SecurityUserDetailsService.java 按 username 查库返回 SecurityUser（TODO）
├── SecurityUtil.java               快捷方法 currentUserId() / current()
└── handler/
    ├── JwtAuthenticationEntryPoint.java  未登录返回 JSON 401
    └── JwtAccessDeniedHandler.java       无权限返回 JSON 403
```

## 你的 TODO

### 1. JwtUtil

用 jjwt 库实现四个方法：

```java
String generateToken(Long userId, String username)  // 用 userId 做 subject，username 放 claim
Long parseUserId(String token)                       // 解析拿 userId
String parseUsername(String token)                   // 解析拿 username
boolean isValid(String token)                        // 验证签名 + 是否过期
```

密钥和过期时间从 `JwtProperties` 读（已经注入好了）。

### 2. JwtAuthenticationFilter

```java
protected void doFilterInternal(request, response, chain) {
    // 1. 从 request header "Authorization" 取出 token，去掉 "Bearer " 前缀
    // 2. 如果没有 token → chain.doFilter (放行，让 SecurityConfig 拦截)
    // 3. 调 jwtUtil.parseUserId(token) 拿到 userId
    // 4. 用 userId 调 userDetailsService.loadUserByUsername(用户名)
    // 5. 构造 UsernamePasswordAuthenticationToken 放入 SecurityContextHolder
    // 6. chain.doFilter
}
```

### 3. SecurityUserDetailsService

```java
loadUserByUsername(String username) {
    User user = userMapper.selectByUsername(username);
    if (user == null) throw new UsernameNotFoundException(...);
    return new SecurityUser(user);
}
```

## 依赖

- `JwtProperties` — 已自动从 yaml 注入
- `UserMapper` — 需要 `selectByUsername` 方法有对应的 XML SQL
