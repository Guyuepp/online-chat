package cn.edu.ncu.onlinechat.module.chat.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // TODO: 从 URL 参数拿 token → 解析 userId → 存在线状态到 Redis
        //       把 session 加入 ConcurrentHashMap<Long, WebSocketSession> 在线池
        log.info("WS connected: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // TODO: 解析 JSON → 根据 type 路由：chat/mark_read/ping
        //       私聊消息：对方在线则直接推送，离线则暂存 Redis 离线队列
        //       群聊消息：查询群成员，逐个推送在线成员
        log.debug("WS message: {}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // TODO: 清理在线状态 → Redis 下线 → 从在线池移除
        log.info("WS disconnected: {}", session.getId());
    }
}
