package cn.edu.ncu.onlinechat.module.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateGroupDTO {

    @NotBlank
    @Size(max = 32)
    private String name;

    @Size(max = 255)
    private String description;

    @Size(max = 255)
    private String avatar;
}
