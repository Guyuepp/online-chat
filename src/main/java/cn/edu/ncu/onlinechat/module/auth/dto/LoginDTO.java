package cn.edu.ncu.onlinechat.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank
    private String phone;

    @NotBlank
    private String code;
}
