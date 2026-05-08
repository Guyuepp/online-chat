package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.module.auth.dto.LoginDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.RegisterDTO;
import cn.edu.ncu.onlinechat.module.auth.service.AuthService;
import cn.edu.ncu.onlinechat.module.auth.service.SmsVerifyService;
import cn.edu.ncu.onlinechat.module.auth.vo.LoginVO;
import cn.edu.ncu.onlinechat.module.user.entity.User;
import cn.edu.ncu.onlinechat.module.user.mapper.UserMapper;
import cn.edu.ncu.onlinechat.module.user.vo.UserVO;
import cn.edu.ncu.onlinechat.security.JwtUtil;
import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SmsVerifyService smsVerifyService;

    @Override
    public LoginVO login(LoginDTO dto) {
        smsVerifyService.checkCode(dto.getPhone(), dto.getCode());
        User user = userMapper.selectByPhone(dto.getPhone());
        if (user == null) {
            user = createUser(dto.getPhone(), null);
        }
        return buildLoginVO(user);
    }

    @Override
    public LoginVO register(RegisterDTO dto) {
        smsVerifyService.checkCode(dto.getPhone(), dto.getCode());
        User existing = userMapper.selectByPhone(dto.getPhone());
        if (existing != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "phone already registered");
        }
        User user = createUser(dto.getPhone(), dto.getNickname());
        return buildLoginVO(user);
    }

    @Override
    public void logout(Long userId) {
        // No-op for now.
    }

    private User createUser(String phone, String nickname) {
        User user = new User();
        user.setUsername(generateUsername(phone));
        user.setPhone(phone);
        if (nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname);
        } else {
            user.setNickname(defaultNickname(phone));
        }
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setStatus(0);
        int rows = userMapper.insert(user);
        if (rows != 1 || user.getId() == null) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "create user failed");
        }
        return user;
    }

    private String generateUsername(String phone) {
        String base = "u" + phone;
        String candidate = base;
        int idx = 1;
        while (userMapper.selectByUsername(candidate) != null) {
            candidate = base + "_" + idx;
            idx++;
        }
        return candidate;
    }

    private String defaultNickname(String phone) {
        if (phone == null || phone.isBlank()) {
            return "user";
        }
        String suffix = phone.length() > 4 ? phone.substring(phone.length() - 4) : phone;
        return "user" + suffix;
    }

    private LoginVO buildLoginVO(User user) {
        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        vo.setUser(toUserVO(user));
        return vo;
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setSignature(user.getSignature());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
