package cn.edu.ncu.onlinechat.module.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 4, max = 8)
    private String code;

    @NotBlank
    @Size(min = 6, max = 32)
    private String newPassword;
}
