package cn.edu.ncu.onlinechat.module.group.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GroupMemberVO implements Serializable {

    private Long id;
    private Long groupId;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
    private String groupNickname;
    private LocalDateTime joinTime;
}
