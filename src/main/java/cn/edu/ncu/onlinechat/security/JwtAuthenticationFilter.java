package cn.edu.ncu.onlinechat.security;

import cn.edu.ncu.onlinechat.common.constant.RedisKeyConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final SecurityUserDetailsService userDetailsService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(jwtProperties.getHeader());
        if (header == null || !header.startsWith(jwtProperties.getPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(jwtProperties.getPrefix().length()).trim();
        if (token.isEmpty() || !jwtUtil.isValid(token)) {
            chain.doFilter(request, response);
            return;
        }

        String jti = jwtUtil.parseJti(token);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisKeyConstant.LOGIN_TOKEN_PREFIX + jti))) {
            chain.doFilter(request, response);
            return;
        }

        String username = jwtUtil.parseUsername(token);
        var userDetails = userDetailsService.loadUserByUsername(username);
        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        chain.doFilter(request, response);
    }
}
