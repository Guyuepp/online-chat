package cn.edu.ncu.onlinechat.module.chat.ws;

import cn.edu.ncu.onlinechat.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final String ATTR_USER_ID = "userId";

    private final JwtUtil jwtUtil;
    private final WebSocketSessionManager sessionManager;
    private final MessageDispatcher dispatcher;

    public ChatWebSocketHandler(JwtUtil jwtUtil,
                                WebSocketSessionManager sessionManager,
                                MessageDispatcher dispatcher) {
        this.jwtUtil = jwtUtil;
        this.sessionManager = sessionManager;
        this.dispatcher = dispatcher;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String token = extractToken(session);
        if (token == null || !jwtUtil.isValid(token)) {
            log.warn("WebSocket 连接 Token 无效: {}", session.getId());
            closeSession(session, CloseStatus.POLICY_VIOLATION);
            return;
        }

        Long userId = jwtUtil.parseUserId(token);
        session.getAttributes().put(ATTR_USER_ID, userId);
        sessionManager.register(userId, session);
        log.info("WS 连接成功: userId={}, sessionId={}", userId, session.getId());

        // 推送离线消息
        List<String> offlineMessages = sessionManager.pullOfflineMessages(userId);
        if (offlineMessages != null && !offlineMessages.isEmpty()) {
            log.info("推送 {} 条离线消息给用户 {}", offlineMessages.size(), userId);
            for (String msg : offlineMessages) {
                try {
                    synchronized (session) {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(msg));
                        }
                    }
                } catch (IOException e) {
                    log.warn("推送离线消息失败: {}", session.getId(), e);
                }
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        if (userId == null) {
            closeSession(session, CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        dispatcher.dispatch(userId, session, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        if (userId != null) {
            sessionManager.unregister(userId, session);
        }
        log.info("WS 断开: userId={}, sessionId={}, status={}", userId, session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        log.error("WS 传输错误: userId={}, sessionId={}", userId, session.getId(), exception);
        if (userId != null) {
            sessionManager.unregister(userId, session);
        }
        closeSession(session, CloseStatus.SERVER_ERROR);
    }

    private String extractToken(WebSocketSession session) {
        if (session.getUri() == null) return null;
        String query = session.getUri().getQuery();
        if (query == null) return null;
        Map<String, String> params = parseQueryParams(query);
        return params.get("token");
    }

    private Map<String, String> parseQueryParams(String query) {
        return java.util.Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .filter(kv -> kv.length == 2)
                .collect(java.util.stream.Collectors.toMap(
                        kv -> java.net.URLDecoder.decode(kv[0], java.nio.charset.StandardCharsets.UTF_8),
                        kv -> java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8)
                ));
    }

    private void closeSession(WebSocketSession session, CloseStatus status) {
        try {
            if (session.isOpen()) {
                session.close(status);
            }
        } catch (IOException e) {
            log.warn("关闭 session 失败: {}", session.getId(), e);
        }
    }
}
