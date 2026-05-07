package cn.edu.ncu.onlinechat.module.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(max = 32)
    private String nickname;

    @Size(max = 255)
    private String avatar;

    @Email
    private String email;

    @Size(max = 32)
    private String phone;

    @Size(max = 255)
    private String signature;
}
