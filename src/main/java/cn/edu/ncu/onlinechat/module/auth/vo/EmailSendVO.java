package cn.edu.ncu.onlinechat.module.auth.vo;

import lombok.Data;

@Data
public class EmailSendVO {

    private String requestId;
    private Long expiresIn;
}
