package cn.edu.ncu.onlinechat.module.chat.ws;

import cn.edu.ncu.onlinechat.common.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class WebSocketSessionManager {

    private final Map<Long, List<WebSocketSession>> sessionMap = new ConcurrentHashMap<>();
    private final StringRedisTemplate redis;

    public WebSocketSessionManager(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void register(Long userId, WebSocketSession session) {
        sessionMap.compute(userId, (id, sessions) -> {
            if (sessions == null) {
                sessions = new CopyOnWriteArrayList<>();
            }
            sessions.add(session);
            return sessions;
        });
        redis.opsForValue().set(RedisKeyConstant.ONLINE_USER_PREFIX + userId, "1");
        log.info("用户 {} 上线，当前在线设备数: {}", userId, getSessionCount(userId));
    }

    public void unregister(Long userId, WebSocketSession session) {
        sessionMap.computeIfPresent(userId, (id, sessions) -> {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                redis.delete(RedisKeyConstant.ONLINE_USER_PREFIX + userId);
                log.info("用户 {} 所有设备已下线", userId);
                return null; // 从 Map 中移除该 key
            }
            log.info("用户 {} 一台设备下线，剩余在线设备数: {}", userId, sessions.size());
            return sessions;
        });
    }

    public void sendToUser(Long userId, String jsonText) {
        List<WebSocketSession> sessions = sessionMap.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        TextMessage message = new TextMessage(jsonText);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    synchronized (session) {
                        session.sendMessage(message);
                    }
                } catch (IOException e) {
                    log.warn("向用户 {} 推送消息失败，session={}", userId, session.getId(), e);
                    unregister(userId, session);
                }
            } else {
                unregister(userId, session);
            }
        }
    }

    public boolean isOnline(Long userId) {
        List<WebSocketSession> sessions = sessionMap.get(userId);
        if (sessions != null && !sessions.isEmpty()) {
            return true;
        }
        return Boolean.TRUE.equals(redis.hasKey(RedisKeyConstant.ONLINE_USER_PREFIX + userId));
    }

    public int getOnlineCount() {
        return sessionMap.size();
    }

    private int getSessionCount(Long userId) {
        List<WebSocketSession> sessions = sessionMap.get(userId);
        return sessions == null ? 0 : sessions.size();
    }

    /**
     * 从 Redis 离线队列拉取所有待推送消息
     */
    public List<String> pullOfflineMessages(Long userId) {
        String key = RedisKeyConstant.OFFLINE_MESSAGE_PREFIX + userId;
        List<String> messages = redis.opsForList().range(key, 0, -1);
        redis.delete(key);
        return messages != null ? messages : List.of();
    }

    /**
     * 将消息存入 Redis 离线队列
     */
    public void pushOfflineMessage(Long userId, String jsonText) {
        redis.opsForList().rightPush(RedisKeyConstant.OFFLINE_MESSAGE_PREFIX + userId, jsonText);
    }
}
