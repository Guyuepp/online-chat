package cn.edu.ncu.onlinechat.module.friend.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FriendRequestVO implements Serializable {

    private Long id;
    private Long fromUserId;
    private String fromUsername;
    private String fromNickname;
    private String fromAvatar;
    private Long toUserId;
    private String message;
    private String status;
    private LocalDateTime createTime;
}
