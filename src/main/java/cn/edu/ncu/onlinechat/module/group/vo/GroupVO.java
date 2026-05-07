package cn.edu.ncu.onlinechat.module.group.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GroupVO implements Serializable {

    private Long id;
    private String name;
    private String avatar;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Integer memberCount;
    private String myRole;
    private LocalDateTime createTime;
}
