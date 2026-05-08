package cn.edu.ncu.onlinechat.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SmsSendDTO {

    @NotBlank
    @Pattern(regexp = "^1\\d{10}$")
    private String phone;
}
