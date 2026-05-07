package cn.edu.ncu.onlinechat.module.auth.service;

import cn.edu.ncu.onlinechat.module.auth.dto.LoginDTO;
import cn.edu.ncu.onlinechat.module.auth.dto.RegisterDTO;
import cn.edu.ncu.onlinechat.module.auth.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    LoginVO register(RegisterDTO dto);

    void logout(Long userId);
}
