package cn.edu.ncu.onlinechat.module.chat.ws;

import cn.edu.ncu.onlinechat.module.chat.ws.protocol.WsInboundMessage;
import cn.edu.ncu.onlinechat.module.chat.ws.protocol.WsOutboundMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class MessageDispatcher {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final WebSocketSessionManager sessionManager;

    public MessageDispatcher(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void dispatch(Long fromUserId, WebSocketSession session, String payload) {
        WsInboundMessage inbound;
        try {
            inbound = objectMapper.readValue(payload, WsInboundMessage.class);
        } catch (JsonProcessingException e) {
            log.warn("消息格式错误，userId={}, payload={}", fromUserId, payload);
            sendDirect(session, WsOutboundMessage.error("消息格式错误"));
            return;
        }

        String type = inbound.getType();
        if (type == null) {
            sendDirect(session, WsOutboundMessage.error("缺少 type 字段"));
            return;
        }

        switch (type) {
            case "ping" -> handlePing(session);
            case "chat" -> handleChat(fromUserId, session, inbound);
            case "mark_read" -> handleMarkRead(fromUserId, session, inbound);
            default -> {
                log.warn("未知消息类型: {}", type);
                sendDirect(session, WsOutboundMessage.error("未知消息类型: " + type));
            }
        }
    }

    private void handlePing(WebSocketSession session) {
        sendDirect(session, WsOutboundMessage.pong());
    }

    private void handleChat(Long fromUserId, WebSocketSession session, WsInboundMessage inbound) {
        Long toTargetId = inbound.getToTargetId();
        if (toTargetId == null) {
            sendDirect(session, WsOutboundMessage.error("缺少 toTargetId"));
            return;
        }

        WsOutboundMessage outbound = WsOutboundMessage.builder()
                .type("chat")
                .chatType(inbound.getChatType())
                .messageType(inbound.getMessageType())
                .fromUserId(fromUserId)
                .toTargetId(toTargetId)
                .content(inbound.getContent())
                .createTime(LocalDateTime.now())
                .build();

        String json;
        try {
            json = objectMapper.writeValueAsString(outbound);
        } catch (JsonProcessingException e) {
            log.error("序列化消息失败", e);
            sendDirect(session, WsOutboundMessage.error("服务器内部错误"));
            return;
        }

        if (sessionManager.isOnline(toTargetId)) {
            sessionManager.sendToUser(toTargetId, json);
            log.debug("私聊消息已推送: {} -> {}", fromUserId, toTargetId);
        } else {
            sessionManager.pushOfflineMessage(toTargetId, json);
            log.debug("目标离线，消息入离线队列: {} -> {}", fromUserId, toTargetId);
        }

        sendDirect(session, WsOutboundMessage.ack("消息已发送"));
    }

    private void handleMarkRead(Long fromUserId, WebSocketSession session, WsInboundMessage inbound) {
        Long toTargetId = inbound.getToTargetId();
        if (toTargetId == null) {
            sendDirect(session, WsOutboundMessage.error("缺少 toTargetId"));
            return;
        }

        WsOutboundMessage outbound = WsOutboundMessage.builder()
                .type("mark_read")
                .fromUserId(fromUserId)
                .toTargetId(toTargetId)
                .chatType(inbound.getChatType())
                .build();

        String json;
        try {
            json = objectMapper.writeValueAsString(outbound);
        } catch (JsonProcessingException e) {
            log.error("序列化消息失败", e);
            return;
        }

        if (sessionManager.isOnline(toTargetId)) {
            sessionManager.sendToUser(toTargetId, json);
        }
    }

    private void sendDirect(WebSocketSession session, WsOutboundMessage msg) {
        try {
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
                }
            }
        } catch (IOException e) {
            log.warn("向 session {} 发送消息失败", session.getId(), e);
        }
    }
}
