package cn.edu.ncu.onlinechat.module.auth.service;

import cn.edu.ncu.onlinechat.module.auth.vo.SmsSendVO;

public interface SmsVerifyService {

    SmsSendVO sendCode(String phone);

    void checkCode(String phone, String code);
}
