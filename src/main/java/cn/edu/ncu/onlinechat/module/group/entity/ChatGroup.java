package cn.edu.ncu.onlinechat.module.group.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatGroup implements Serializable {

    private Long id;
    private String name;
    private String avatar;
    private String description;
    private Long ownerId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
