package cn.edu.ncu.onlinechat.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailSendDTO {

    @NotBlank
    @Email
    private String email;
}
