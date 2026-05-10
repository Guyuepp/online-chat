package cn.edu.ncu.onlinechat.module.chat.ws;

import cn.edu.ncu.onlinechat.module.chat.ws.protocol.WsOutboundMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageDispatcherTest {

    @Mock
    private WebSocketSessionManager sessionManager;

    @Mock
    private WebSocketSession session;

    private MessageDispatcher dispatcher;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        dispatcher = new MessageDispatcher(sessionManager);
        lenient().when(session.isOpen()).thenReturn(true);
    }

    @Test
    void dispatchShouldReturnErrorForInvalidJson() throws IOException {
        dispatcher.dispatch(1L, session, "not json");

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        WsOutboundMessage response = objectMapper.readValue(captor.getValue().getPayload(), WsOutboundMessage.class);
        assertThat(response.getType()).isEqualTo("error");
        assertThat(response.getMsg()).contains("消息格式错误");
    }

    @Test
    void dispatchShouldReturnErrorForMissingType() throws IOException {
        dispatcher.dispatch(1L, session, "{}");

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        WsOutboundMessage response = objectMapper.readValue(captor.getValue().getPayload(), WsOutboundMessage.class);
        assertThat(response.getType()).isEqualTo("error");
        assertThat(response.getMsg()).contains("缺少 type 字段");
    }

    @Test
    void dispatchShouldReturnErrorForUnknownType() throws IOException {
        dispatcher.dispatch(1L, session, "{\"type\":\"unknown\"}");

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        WsOutboundMessage response = objectMapper.readValue(captor.getValue().getPayload(), WsOutboundMessage.class);
        assertThat(response.getType()).isEqualTo("error");
        assertThat(response.getMsg()).contains("未知消息类型");
    }

    @Test
    void pingShouldReplyPong() throws IOException {
        dispatcher.dispatch(1L, session, "{\"type\":\"ping\"}");

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        WsOutboundMessage response = objectMapper.readValue(captor.getValue().getPayload(), WsOutboundMessage.class);
        assertThat(response.getType()).isEqualTo("pong");
    }

    @Test
    void chatShouldPushToOnlineTargetAndAck() throws IOException {
        when(sessionManager.isOnline(3L)).thenReturn(true);

        dispatcher.dispatch(2L, session, "{\"type\":\"chat\",\"chatType\":\"PRIVATE\",\"toTargetId\":3,\"messageType\":\"TEXT\",\"content\":\"hello\"}");

        // should push to target
        verify(sessionManager).sendToUser(eq(3L), anyString());
        // should ack sender
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        WsOutboundMessage ack = objectMapper.readValue(captor.getValue().getPayload(), WsOutboundMessage.class);
        assertThat(ack.getType()).isEqualTo("ack");
        assertThat(ack.getMsg()).isEqualTo("消息已发送");
    }

    @Test
    void chatShouldQueueOfflineWhenTargetOffline() throws IOException {
        when(sessionManager.isOnline(3L)).thenReturn(false);

        dispatcher.dispatch(2L, session, "{\"type\":\"chat\",\"chatType\":\"PRIVATE\",\"toTargetId\":3,\"messageType\":\"TEXT\",\"content\":\"hello\"}");

        verify(sessionManager).pushOfflineMessage(eq(3L), anyString());
    }

    @Test
    void chatShouldReturnErrorForMissingTargetId() throws IOException {
        dispatcher.dispatch(2L, session, "{\"type\":\"chat\",\"content\":\"hello\"}");

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        WsOutboundMessage response = objectMapper.readValue(captor.getValue().getPayload(), WsOutboundMessage.class);
        assertThat(response.getType()).isEqualTo("error");
        assertThat(response.getMsg()).contains("缺少 toTargetId");
    }

    @Test
    void chatOutboundMessageShouldContainFromUserIdAndCreateTime() throws IOException {
        when(sessionManager.isOnline(3L)).thenReturn(true);

        dispatcher.dispatch(2L, session, "{\"type\":\"chat\",\"chatType\":\"PRIVATE\",\"toTargetId\":3,\"messageType\":\"TEXT\",\"content\":\"test\"}");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(sessionManager).sendToUser(eq(3L), captor.capture());
        WsOutboundMessage outbound = objectMapper.readValue(captor.getValue(), WsOutboundMessage.class);
        assertThat(outbound.getType()).isEqualTo("chat");
        assertThat(outbound.getFromUserId()).isEqualTo(2L);
        assertThat(outbound.getToTargetId()).isEqualTo(3L);
        assertThat(outbound.getContent()).isEqualTo("test");
        assertThat(outbound.getCreateTime()).isNotNull();
    }

    @Test
    void markReadShouldForwardToOnlineTarget() throws IOException {
        when(sessionManager.isOnline(3L)).thenReturn(true);

        dispatcher.dispatch(2L, session, "{\"type\":\"mark_read\",\"chatType\":\"PRIVATE\",\"toTargetId\":3}");

        verify(sessionManager).sendToUser(eq(3L), anyString());
    }

    @Test
    void markReadShouldNotSendWhenTargetOffline() {
        when(sessionManager.isOnline(3L)).thenReturn(false);

        dispatcher.dispatch(2L, session, "{\"type\":\"mark_read\",\"chatType\":\"PRIVATE\",\"toTargetId\":3}");

        verify(sessionManager, never()).sendToUser(anyLong(), anyString());
    }

    @Test
    void markReadShouldReturnErrorForMissingTargetId() throws IOException {
        dispatcher.dispatch(2L, session, "{\"type\":\"mark_read\"}");

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        WsOutboundMessage response = objectMapper.readValue(captor.getValue().getPayload(), WsOutboundMessage.class);
        assertThat(response.getType()).isEqualTo("error");
        assertThat(response.getMsg()).contains("缺少 toTargetId");
    }
}
