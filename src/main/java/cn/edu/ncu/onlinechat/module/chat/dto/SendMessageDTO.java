package cn.edu.ncu.onlinechat.module.chat.dto;

import cn.edu.ncu.onlinechat.common.enums.ChatType;
import cn.edu.ncu.onlinechat.common.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageDTO {

    @NotNull
    private ChatType chatType;

    @NotNull
    private MessageType messageType;

    @NotNull
    private Long toTargetId;

    @Size(max = 5000)
    private String content;

    private String fileUrl;
    private String fileName;
    private Long fileSize;
}
