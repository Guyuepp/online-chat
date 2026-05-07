package cn.edu.ncu.onlinechat.module.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private String signature;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
