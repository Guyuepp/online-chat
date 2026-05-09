package cn.edu.ncu.onlinechat.module.user.service.impl;

import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.common.result.ResultCode;
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
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public UserVO getProfile(Long id) {
        return toUserVO(getById(id));
    }

    @Override
    public List<UserVO> search(String keyword) {
        List<User> users = userMapper.searchByKeyword(keyword);
        return users.stream()
                .limit(20)
                .map(this::toUserVO)
                .toList();
    }

    @Override
    public void updateProfile(Long id, UserUpdateDTO dto) {
        User user = getById(id);
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getSignature() != null) {
            user.setSignature(dto.getSignature());
        }
        userMapper.update(user);
    }

    @Override
    public void updatePassword(Long id, PasswordUpdateDTO dto) {
        User user = getById(id);
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_INCORRECT);
        }
        userMapper.updatePassword(id, passwordEncoder.encode(dto.getNewPassword()));
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
