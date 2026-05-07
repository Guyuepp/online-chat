package cn.edu.ncu.onlinechat.module.friend.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Friend implements Serializable {

    private Long id;
    private Long userId;
    private Long friendId;
    private Long groupId;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
