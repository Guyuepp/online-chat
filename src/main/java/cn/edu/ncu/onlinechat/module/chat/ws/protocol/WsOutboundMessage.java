package cn.edu.ncu.onlinechat.module.chat.ws.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsOutboundMessage {
    private String type;
    private String chatType;
    private String messageType;
    private Long fromUserId;
    private Long toTargetId;
    private String content;
    private LocalDateTime createTime;

    /** 错误码或提示信息 */
    private String msg;

    public static WsOutboundMessage pong() {
        return WsOutboundMessage.builder().type("pong").build();
    }

    public static WsOutboundMessage error(String msg) {
        return WsOutboundMessage.builder().type("error").msg(msg).build();
    }

    public static WsOutboundMessage ack(String msg) {
        return WsOutboundMessage.builder().type("ack").msg(msg).build();
    }
}
