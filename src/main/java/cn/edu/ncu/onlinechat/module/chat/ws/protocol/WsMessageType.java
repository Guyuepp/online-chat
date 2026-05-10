package cn.edu.ncu.onlinechat.module.chat.ws.protocol;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WsMessageType {
    CHAT("chat"),
    MARK_READ("mark_read"),
    PING("ping"),
    PONG("pong"),
    ACK("ack"),
    ERROR("error");

    private final String value;

    WsMessageType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
