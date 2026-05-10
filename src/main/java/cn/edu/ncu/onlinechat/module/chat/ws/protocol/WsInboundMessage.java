package cn.edu.ncu.onlinechat.module.chat.ws.protocol;

import lombok.Data;

@Data
public class WsInboundMessage {
    private String type;
    private String chatType;
    private String messageType;
    private Long toTargetId;
    private String content;
}
