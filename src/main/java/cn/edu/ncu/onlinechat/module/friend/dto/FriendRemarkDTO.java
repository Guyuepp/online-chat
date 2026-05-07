package cn.edu.ncu.onlinechat.module.friend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FriendRemarkDTO {

    @Size(max = 32)
    private String remark;
}
