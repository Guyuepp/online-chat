package cn.edu.ncu.onlinechat.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 4, max = 8)
    private String code;

    @Size(max = 32)
    private String nickname;

    @Size(min = 6, max = 32)
    private String password;
}
