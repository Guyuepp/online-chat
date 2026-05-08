package cn.edu.ncu.onlinechat.module.auth.service;

import cn.edu.ncu.onlinechat.module.auth.vo.EmailSendVO;

public interface VerifyCodeService {

    EmailSendVO sendCode(String email);

    void checkCode(String email, String code);
}
