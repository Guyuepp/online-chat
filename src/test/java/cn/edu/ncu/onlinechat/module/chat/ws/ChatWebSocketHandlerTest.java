package cn.edu.ncu.onlinechat.module.chat.ws;

import cn.edu.ncu.onlinechat.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatWebSocketHandlerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private WebSocketSessionManager sessionManager;

    @Mock
    private MessageDispatcher dispatcher;

    @Mock
    private WebSocketSession session;

    private ChatWebSocketHandler handler;
    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        handler = new ChatWebSocketHandler(jwtUtil, sessionManager, dispatcher);
        attributes = new HashMap<>();
        lenient().when(session.getId()).thenReturn("test-session-id");
        lenient().when(session.getAttributes()).thenReturn(attributes);
    }

    @Test
    void afterConnectionEstablishedShouldRegisterWithValidToken() throws Exception {
        when(session.getUri()).thenReturn(URI.create("ws://localhost/ws/chat?token=valid-token"));
        when(jwtUtil.isValid("valid-token")).thenReturn(true);
        when(jwtUtil.parseUserId("valid-token")).thenReturn(1L);
        when(sessionManager.pullOfflineMessages(1L)).thenReturn(List.of());

        handler.afterConnectionEstablished(session);

        verify(sessionManager).register(1L, session);
        assertThat(attributes.get("userId")).isEqualTo(1L);
        verify(session, never()).close(any());
    }

    @Test
    void afterConnectionEstablishedShouldCloseOnInvalidToken() throws Exception {
        when(session.getUri()).thenReturn(URI.create("ws://localhost/ws/chat?token=bad-token"));
        when(jwtUtil.isValid("bad-token")).thenReturn(false);
        when(session.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session);

        verify(session).close(CloseStatus.POLICY_VIOLATION);
        verify(sessionManager, never()).register(anyLong(), any());
    }

    @Test
    void afterConnectionEstablishedShouldCloseOnMissingToken() throws Exception {
        when(session.getUri()).thenReturn(URI.create("ws://localhost/ws/chat"));
        when(session.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session);

        verify(session).close(CloseStatus.POLICY_VIOLATION);
    }

    @Test
    void afterConnectionEstablishedShouldPushOfflineMessages() throws Exception {
        when(session.getUri()).thenReturn(URI.create("ws://localhost/ws/chat?token=valid-token"));
        when(jwtUtil.isValid("valid-token")).thenReturn(true);
        when(jwtUtil.parseUserId("valid-token")).thenReturn(1L);
        when(sessionManager.pullOfflineMessages(1L)).thenReturn(List.of("msg1", "msg2"));
        when(session.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session);

        verify(sessionManager).register(1L, session);
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());
        List<TextMessage> sent = captor.getAllValues();
        assertThat(sent.stream().map(TextMessage::getPayload))
                .contains("msg1", "msg2");
    }

    @Test
    void handleTextMessageShouldDispatch() {
        attributes.put("userId", 1L);
        TextMessage message = new TextMessage("{\"type\":\"ping\"}");

        handler.handleTextMessage(session, message);

        verify(dispatcher).dispatch(eq(1L), eq(session), eq("{\"type\":\"ping\"}"));
    }

    @Test
    void handleTextMessageShouldCloseWhenNoUserId() throws Exception {
        TextMessage message = new TextMessage("test");
        when(session.isOpen()).thenReturn(true);

        handler.handleTextMessage(session, message);

        verify(session).close(CloseStatus.NOT_ACCEPTABLE);
        verify(dispatcher, never()).dispatch(anyLong(), any(), anyString());
    }

    @Test
    void afterConnectionClosedShouldUnregister() {
        attributes.put("userId", 1L);

        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(sessionManager).unregister(1L, session);
    }

    @Test
    void afterConnectionClosedShouldSkipWhenNoUserId() {
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(sessionManager, never()).unregister(anyLong(), any());
    }

    @Test
    void handleTransportErrorShouldUnregister() throws Exception {
        attributes.put("userId", 1L);
        when(session.isOpen()).thenReturn(true);

        handler.handleTransportError(session, new RuntimeException("boom"));

        verify(sessionManager).unregister(1L, session);
        verify(session).close(CloseStatus.SERVER_ERROR);
    }

    @Test
    void tokenShouldBeExtractedFromQueryString() throws Exception {
        when(session.getUri()).thenReturn(URI.create("ws://localhost/ws/chat?token=my.jwt.token.abc"));
        when(jwtUtil.isValid("my.jwt.token.abc")).thenReturn(true);
        when(jwtUtil.parseUserId("my.jwt.token.abc")).thenReturn(2L);
        when(sessionManager.pullOfflineMessages(2L)).thenReturn(List.of());

        handler.afterConnectionEstablished(session);

        verify(jwtUtil).isValid("my.jwt.token.abc");
        verify(sessionManager).register(2L, session);
    }
}
