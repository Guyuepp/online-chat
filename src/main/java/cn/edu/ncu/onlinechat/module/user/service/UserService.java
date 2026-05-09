package cn.edu.ncu.onlinechat.module.user.service;

import cn.edu.ncu.onlinechat.module.user.dto.PasswordResetDTO;
import cn.edu.ncu.onlinechat.module.user.dto.PasswordUpdateDTO;
import cn.edu.ncu.onlinechat.module.user.dto.UserUpdateDTO;
import cn.edu.ncu.onlinechat.module.user.entity.User;
import cn.edu.ncu.onlinechat.module.user.vo.UserVO;

import java.util.List;

public interface UserService {

    User getById(Long id);

    UserVO getProfile(Long id);

    List<UserVO> search(String keyword);

    void updateProfile(Long id, UserUpdateDTO dto);

    void updatePassword(Long id, PasswordUpdateDTO dto);

    void resetPasswordByEmail(PasswordResetDTO dto);
}
