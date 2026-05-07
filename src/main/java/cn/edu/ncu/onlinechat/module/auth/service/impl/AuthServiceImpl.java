package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.module.auth.dto.LoginDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.RegisterDTO;
import cn.edu.ncu.onlinechat.module.auth.service.AuthService;
import cn.edu.ncu.onlinechat.module.auth.vo.LoginVO;
import cn.edu.ncu.onlinechat.module.user.mapper.UserMapper;
import cn.edu.ncu.onlinechat.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        throw new UnsupportedOperationException("TODO: 校验用户名密码 → 生成 JWT → 返回");
    }

    @Override
    public LoginVO register(RegisterDTO dto) {
        throw new UnsupportedOperationException("TODO: 校验用户名唯一 → 加密密码 → 落库 → 自动登录返回");
    }

    @Override
    public void logout(Long userId) {
        throw new UnsupportedOperationException("TODO: 把当前 token 拉黑或删 redis 在线状态");
    }
}
