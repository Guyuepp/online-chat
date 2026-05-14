package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.common.constant.RedisKeyConstant;
import cn.edu.ncu.onlinechat.common.util.RequestUtil;
import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.config.MailVerifyProperties;
import cn.edu.ncu.onlinechat.module.auth.vo.EmailSendVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MailVerifyServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailVerifyProperties properties;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private MailVerifyService mailVerifyService;

    private static final String EMAIL = "user@example.com";
    private static final Long CODE_LENGTH = 6L;
    private static final Long VALID_TIME = 300L;
    private static final Long INTERVAL = 60L;
    private static final Long IP_INTERVAL = 60L;
    private static final Long MAX_FAIL_TIMES = 5L;
    private static final Long LOCK_TIME = 600L;
    private static final String SUBJECT = "Login code";
    private static final String TEMPLATE = "Your code is {code}, valid {minutes} minutes.";
    private static final String FROM = "no-reply@example.com";

    @BeforeEach
    void setUp() {
        when(properties.getCodeLength()).thenReturn(CODE_LENGTH);
        when(properties.getValidTime()).thenReturn(VALID_TIME);
        when(properties.getInterval()).thenReturn(INTERVAL);
        when(properties.getIpInterval()).thenReturn(IP_INTERVAL);
        when(properties.getMaxFailTimes()).thenReturn(MAX_FAIL_TIMES);
        when(properties.getLockTime()).thenReturn(LOCK_TIME);
        when(properties.getSubject()).thenReturn(SUBJECT);
        when(properties.getTemplate()).thenReturn(TEMPLATE);
        when(properties.getFrom()).thenReturn(FROM);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void sendCodeShouldSendMailAndStoreCode() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(INTERVAL), eq(TimeUnit.SECONDS)))
                .thenReturn(true);

        EmailSendVO vo = mailVerifyService.sendCode(EMAIL);

        assertThat(vo.getExpiresIn()).isEqualTo(VALID_TIME);
        assertThat(vo.getRequestId()).isNotBlank();

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(eq(RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + EMAIL), codeCaptor.capture(),
                eq(VALID_TIME), eq(TimeUnit.SECONDS));
        String code = codeCaptor.getValue();
        assertThat(code).matches("\\d{6}");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo()).contains(EMAIL);
        assertThat(message.getFrom()).isEqualTo(FROM);
        assertThat(message.getSubject()).isEqualTo(SUBJECT);
        assertThat(message.getText()).contains(code);
    }

    @Test
    void sendCodeShouldThrowWhenRateLimited() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(INTERVAL), eq(TimeUnit.SECONDS)))
                .thenReturn(false);

        assertThatThrownBy(() -> mailVerifyService.sendCode(EMAIL))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("too frequent");
    }

    @Test
    void sendCodeShouldCleanKeysWhenMailFails() {
        when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(INTERVAL), eq(TimeUnit.SECONDS)))
                .thenReturn(true);
        doThrow(new RuntimeException("smtp down")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> mailVerifyService.sendCode(EMAIL))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mail send failed");

        verify(stringRedisTemplate).delete(RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + EMAIL);
        verify(stringRedisTemplate).delete(RedisKeyConstant.MAIL_SEND_INTERVAL_PREFIX + EMAIL);
    }

    @Test
    void sendCodeShouldSkipRateLimitWhenIntervalIsNull() {
        when(properties.getInterval()).thenReturn(null);

        EmailSendVO vo = mailVerifyService.sendCode(EMAIL);

        assertThat(vo).isNotNull();
        verify(valueOperations, never()).setIfAbsent(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void checkCodeShouldPassWhenMatched() {
        when(valueOperations.get(RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + EMAIL)).thenReturn("123456");

        mailVerifyService.checkCode(EMAIL, "123456");

        verify(stringRedisTemplate).delete(RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + EMAIL);
    }

    @Test
    void checkCodeShouldThrowWhenMismatch() {
        when(valueOperations.get(RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + EMAIL)).thenReturn("654321");

        assertThatThrownBy(() -> mailVerifyService.checkCode(EMAIL, "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("invalid or expired");
    }

    @Test
    void checkCodeShouldThrowWhenMissing() {
        when(valueOperations.get(RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + EMAIL)).thenReturn(null);

        assertThatThrownBy(() -> mailVerifyService.checkCode(EMAIL, "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("invalid or expired");
    }

    @Test
    void checkCodeShouldLockWhenFailTooMany() {
        when(properties.getMaxFailTimes()).thenReturn(2L);
        when(valueOperations.get(RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + EMAIL)).thenReturn("123456");
        when(valueOperations.increment(RedisKeyConstant.MAIL_VERIFY_FAIL_PREFIX + EMAIL)).thenReturn(2L);

        assertThatThrownBy(() -> mailVerifyService.checkCode(EMAIL, "000000"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("locked");

        verify(valueOperations).set(eq(RedisKeyConstant.MAIL_VERIFY_LOCK_PREFIX + EMAIL), eq("1"),
                eq(LOCK_TIME), eq(TimeUnit.SECONDS));
        verify(stringRedisTemplate).delete(RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + EMAIL);
    }

    @Test
    void sendCodeShouldThrowWhenLocked() {
        when(stringRedisTemplate.hasKey(RedisKeyConstant.MAIL_VERIFY_LOCK_PREFIX + EMAIL))
                .thenReturn(true);

        assertThatThrownBy(() -> mailVerifyService.sendCode(EMAIL))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("locked");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendCodeShouldThrowWhenIpRateLimited() {
        try (MockedStatic<RequestUtil> mocked = mockStatic(RequestUtil.class)) {
            mocked.when(RequestUtil::clientIp).thenReturn("10.0.0.1");
            when(valueOperations.setIfAbsent(
                    eq(RedisKeyConstant.MAIL_SEND_IP_INTERVAL_PREFIX + "10.0.0.1"),
                    eq("1"), eq(IP_INTERVAL), eq(TimeUnit.SECONDS)))
                    .thenReturn(false);

            assertThatThrownBy(() -> mailVerifyService.sendCode(EMAIL))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("too frequent");

            verify(mailSender, never()).send(any(SimpleMailMessage.class));
        }
    }
}
