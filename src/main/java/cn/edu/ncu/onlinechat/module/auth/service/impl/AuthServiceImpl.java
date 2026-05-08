package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.common.result.ResultCode;
import cn.edu.ncu.onlinechat.common.constant.Constants;
import cn.edu.ncu.onlinechat.module.auth.dto.LoginDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.RegisterDTO;
import cn.edu.ncu.onlinechat.module.auth.service.AuthService;
import cn.edu.ncu.onlinechat.module.auth.service.VerifyCodeService;
import cn.edu.ncu.onlinechat.module.auth.vo.LoginVO;
import cn.edu.ncu.onlinechat.module.friend.entity.FriendGroup;
import cn.edu.ncu.onlinechat.module.friend.mapper.FriendGroupMapper;
import cn.edu.ncu.onlinechat.module.user.entity.User;
import cn.edu.ncu.onlinechat.module.user.mapper.UserMapper;
import cn.edu.ncu.onlinechat.module.user.vo.UserVO;
import cn.edu.ncu.onlinechat.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final FriendGroupMapper friendGroupMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VerifyCodeService verifyCodeService;

    @Override
    @Transactional
    public LoginVO login(LoginDTO dto) {
        verifyCodeService.checkCode(dto.getEmail(), dto.getCode());
        User user = userMapper.selectByEmail(dto.getEmail());
        if (user == null) {
            user = createUser(dto.getEmail(), null);
        }
        return buildLoginVO(user);
    }

    @Override
    @Transactional
    public LoginVO register(RegisterDTO dto) {
        verifyCodeService.checkCode(dto.getEmail(), dto.getCode());
        User existing = userMapper.selectByEmail(dto.getEmail());
        if (existing != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "email already registered");
        }
        User user = createUser(dto.getEmail(), dto.getNickname());
        return buildLoginVO(user);
    }

    @Override
    public void logout(Long userId) {
        // No-op for now.
    }

    private User createUser(String email, String nickname) {
        User user = new User();
        user.setUsername(generateUsername(email));
        user.setEmail(email);
        if (nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname);
        } else {
            user.setNickname(defaultNickname(email));
        }
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setStatus(0);
        int rows = userMapper.insert(user);
        if (rows != 1 || user.getId() == null) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "create user failed");
        }
        initDefaultFriendGroup(user.getId());
        return user;
    }

    private String generateUsername(String email) {
        String base = normalizeBase(email);
        String candidate = base;
        int idx = 1;
        while (userMapper.selectByUsername(candidate) != null) {
            candidate = base + "_" + idx;
            idx++;
        }
        return candidate;
    }

    private String defaultNickname(String email) {
        if (email == null || email.isBlank()) {
            return "user";
        }
        String base = normalizeBase(email);
        return base.isBlank() ? "user" : base;
    }

    private String normalizeBase(String email) {
        if (email == null || email.isBlank()) {
            return "user";
        }
        String local = email;
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            local = email.substring(0, atIndex);
        }
        String normalized = local.replaceAll("[^A-Za-z0-9_]", "_").trim();
        return normalized.isBlank() ? "user" : normalized;
    }

    private LoginVO buildLoginVO(User user) {
        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        vo.setUser(toUserVO(user));
        return vo;
    }

    private void initDefaultFriendGroup(Long userId) {
        FriendGroup group = new FriendGroup();
        group.setUserId(userId);
        group.setName(Constants.DEFAULT_FRIEND_GROUP_NAME);
        group.setSortOrder(0);
        int rows = friendGroupMapper.insert(group);
        if (rows != 1) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "create default friend group failed");
        }
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
