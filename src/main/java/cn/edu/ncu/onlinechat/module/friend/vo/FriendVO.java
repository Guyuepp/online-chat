package cn.edu.ncu.onlinechat.module.friend.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FriendVO implements Serializable {

    private Long id;
    private Long userId;
    private Long friendId;
    private Long groupId;
    private String remark;
    private String username;
    private String nickname;
    private String avatar;
    private String signature;
    private Boolean online;
}
