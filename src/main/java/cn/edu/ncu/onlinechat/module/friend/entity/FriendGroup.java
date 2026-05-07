package cn.edu.ncu.onlinechat.module.friend.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FriendGroup implements Serializable {

    private Long id;
    private Long userId;
    private String name;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
