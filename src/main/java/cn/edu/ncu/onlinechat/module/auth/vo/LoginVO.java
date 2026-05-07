package cn.edu.ncu.onlinechat.module.auth.vo;

import cn.edu.ncu.onlinechat.module.user.vo.UserVO;
import lombok.Data;

@Data
public class LoginVO {

    private String token;
    private UserVO user;
}
