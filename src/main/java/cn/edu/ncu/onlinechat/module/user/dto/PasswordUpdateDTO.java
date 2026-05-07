package cn.edu.ncu.onlinechat.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateDTO {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 32)
    private String newPassword;
}
