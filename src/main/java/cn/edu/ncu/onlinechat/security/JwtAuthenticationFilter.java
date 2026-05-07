package cn.edu.ncu.onlinechat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final SecurityUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        // TODO:
        // 1. 从 request header 取出 token（去掉 Bearer 前缀）
        // 2. 没有就放行，让后续 SecurityFilterChain 决定是否拒绝
        // 3. 有则用 jwtUtil 校验并解析 username，加载 UserDetails
        // 4. 构造 UsernamePasswordAuthenticationToken 放进 SecurityContextHolder
        chain.doFilter(request, response);
    }
}
