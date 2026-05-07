package cn.edu.ncu.onlinechat.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank
    @Size(min = 3, max = 32)
    private String username;

    @NotBlank
    @Size(min = 6, max = 32)
    private String password;

    @Size(max = 32)
    private String nickname;
}
