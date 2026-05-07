package cn.edu.ncu.onlinechat.module.friend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FriendGroupDTO {

    @NotBlank
    @Size(max = 32)
    private String name;

    private Integer sortOrder;
}
