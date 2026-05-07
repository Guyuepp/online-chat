package cn.edu.ncu.onlinechat.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public String generateToken(Long userId, String username) {
        throw new UnsupportedOperationException("TODO: 用 jjwt 生成 token，subject=userId，claim 加 username");
    }

    public Long parseUserId(String token) {
        throw new UnsupportedOperationException("TODO: 解析 token 拿 userId，签名失败/过期抛异常");
    }

    public String parseUsername(String token) {
        throw new UnsupportedOperationException("TODO: 解析 token 拿 username");
    }

    public boolean isValid(String token) {
        throw new UnsupportedOperationException("TODO: 校验签名与过期时间");
    }
}
