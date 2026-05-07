package cn.edu.ncu.onlinechat.module.group.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GroupMember implements Serializable {

    private Long id;
    private Long groupId;
    private Long userId;
    private String role;
    private String nickname;
    private LocalDateTime joinTime;
    private LocalDateTime updateTime;
}
