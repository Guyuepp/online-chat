package cn.edu.ncu.onlinechat.module.friend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FriendRequestSendDTO {

    @NotNull
    private Long toUserId;

    @Size(max = 255)
    private String message;
}
