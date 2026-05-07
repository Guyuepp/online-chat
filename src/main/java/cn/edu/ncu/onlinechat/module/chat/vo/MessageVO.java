package cn.edu.ncu.onlinechat.module.chat.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MessageVO implements Serializable {

    private Long id;
    private String chatType;
    private String messageType;
    private Long fromUserId;
    private String fromUsername;
    private String fromNickname;
    private String fromAvatar;
    private Long toTargetId;
    private String content;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Boolean isRead;
    private LocalDateTime createTime;
}
