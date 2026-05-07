package cn.edu.ncu.onlinechat.module.friend.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FriendRequest implements Serializable {

    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String message;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
