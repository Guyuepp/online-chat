package cn.edu.ncu.onlinechat.module.friend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FriendMoveDTO {

    @NotNull
    private Long groupId;
}
