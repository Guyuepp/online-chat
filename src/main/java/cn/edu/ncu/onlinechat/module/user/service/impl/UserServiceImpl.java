package cn.edu.ncu.onlinechat.module.user.service.impl;

import cn.edu.ncu.onlinechat.module.user.dto.PasswordUpdateDTO;
import cn.edu.ncu.onlinechat.module.user.dto.UserUpdateDTO;
import cn.edu.ncu.onlinechat.module.user.entity.User;
import cn.edu.ncu.onlinechat.module.user.mapper.UserMapper;
import cn.edu.ncu.onlinechat.module.user.service.UserService;
import cn.edu.ncu.onlinechat.module.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getById(Long id) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public UserVO getProfile(Long id) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public List<UserVO> search(String keyword) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void updateProfile(Long id, UserUpdateDTO dto) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void updatePassword(Long id, PasswordUpdateDTO dto) {
        throw new UnsupportedOperationException("TODO");
    }
}
