package cn.edu.ncu.onlinechat.module.chat.ws;

import cn.edu.ncu.onlinechat.common.constant.RedisKeyConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketSessionManagerTest {

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private ListOperations<String, String> listOps;

    @Mock
    private WebSocketSession session1, session2;

    private WebSocketSessionManager manager;

    @BeforeEach
    void setUp() {
        lenient().when(redis.opsForValue()).thenReturn(valueOps);
        lenient().when(redis.opsForList()).thenReturn(listOps);
        manager = new WebSocketSessionManager(redis);
    }

    @Test
    void registerShouldSetRedisAndAddToMap() {
        manager.register(1L, session1);

        verify(valueOps).set(RedisKeyConstant.ONLINE_USER_PREFIX + "1", "1");
        assertThat(manager.isOnline(1L)).isTrue();
    }

    @Test
    void registerShouldSupportMultiDevice() {
        manager.register(1L, session1);
        manager.register(1L, session2);

        assertThat(manager.isOnline(1L)).isTrue();
        verify(valueOps, atLeastOnce()).set(RedisKeyConstant.ONLINE_USER_PREFIX + "1", "1");
    }

    @Test
    void unregisterShouldKeepRedisWhenOtherSessionsRemain() {
        lenient().when(redis.hasKey(anyString())).thenReturn(true);

        manager.register(1L, session1);
        manager.register(1L, session2);
        manager.unregister(1L, session1);

        assertThat(manager.isOnline(1L)).isTrue();
        verify(redis, never()).delete(RedisKeyConstant.ONLINE_USER_PREFIX + "1");
    }

    @Test
    void unregisterShouldDeleteRedisWhenLastSession() {
        manager.register(1L, session1);
        manager.unregister(1L, session1);

        verify(redis).delete(RedisKeyConstant.ONLINE_USER_PREFIX + "1");
    }

    @Test
    void sendToUserShouldSendToAllSessions() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);

        manager.register(1L, session1);
        manager.register(1L, session2);
        manager.sendToUser(1L, "{\"type\":\"chat\"}");

        verify(session1).sendMessage(eq(new TextMessage("{\"type\":\"chat\"}")));
        verify(session2).sendMessage(eq(new TextMessage("{\"type\":\"chat\"}")));
    }

    @Test
    void sendToUserShouldCleanDeadSessions() throws IOException {
        when(session1.isOpen()).thenReturn(false);
        when(session2.isOpen()).thenReturn(true);

        manager.register(1L, session1);
        manager.register(1L, session2);
        manager.sendToUser(1L, "hello");

        verify(session2).sendMessage(eq(new TextMessage("hello")));
        verify(session1, never()).sendMessage(any());
    }

    @Test
    void sendToUserShouldDoNothingWhenUserOffline() throws IOException {
        lenient().when(redis.hasKey(RedisKeyConstant.ONLINE_USER_PREFIX + "99")).thenReturn(false);

        manager.sendToUser(99L, "hello");

        verify(session1, never()).sendMessage(any());
        verify(session2, never()).sendMessage(any());
    }

    @Test
    void isOnlineShouldCheckRedisWhenNotInMap() {
        when(redis.hasKey(RedisKeyConstant.ONLINE_USER_PREFIX + "99")).thenReturn(true);

        assertThat(manager.isOnline(99L)).isTrue();
        verify(redis).hasKey(RedisKeyConstant.ONLINE_USER_PREFIX + "99");
    }

    @Test
    void isOnlineShouldReturnFalseWhenNotInMapAndNotInRedis() {
        when(redis.hasKey(RedisKeyConstant.ONLINE_USER_PREFIX + "99")).thenReturn(false);

        assertThat(manager.isOnline(99L)).isFalse();
    }

    @Test
    void pullOfflineMessagesShouldReturnAndDelete() {
        String key = RedisKeyConstant.OFFLINE_MESSAGE_PREFIX + "1";
        when(listOps.range(key, 0, -1)).thenReturn(List.of("msg1", "msg2"));

        List<String> messages = manager.pullOfflineMessages(1L);

        assertThat(messages).containsExactly("msg1", "msg2");
        verify(redis).delete(key);
    }

    @Test
    void pullOfflineMessagesShouldHandleEmptyQueue() {
        String key = RedisKeyConstant.OFFLINE_MESSAGE_PREFIX + "1";
        when(listOps.range(key, 0, -1)).thenReturn(List.of());

        List<String> messages = manager.pullOfflineMessages(1L);

        assertThat(messages).isEmpty();
        verify(redis).delete(key);
    }

    @Test
    void pushOfflineMessageShouldRightPush() {
        manager.pushOfflineMessage(1L, "offline-msg");

        verify(listOps).rightPush(RedisKeyConstant.OFFLINE_MESSAGE_PREFIX + "1", "offline-msg");
    }

    @Test
    void getOnlineCountShouldReflectRegisteredUsers() {
        assertThat(manager.getOnlineCount()).isZero();

        manager.register(1L, session1);
        assertThat(manager.getOnlineCount()).isEqualTo(1);

        manager.register(2L, session2);
        assertThat(manager.getOnlineCount()).isEqualTo(2);
    }
}
