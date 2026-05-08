package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.common.constant.RedisKeyConstant;
import cn.edu.ncu.onlinechat.config.MailVerifyProperties;
import cn.edu.ncu.onlinechat.module.auth.vo.EmailSendVO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("integration")
@SpringBootTest
class MailVerifyIntegrationTest {

    private static final String TEST_EMAIL = env("TEST_EMAIL");

    @Autowired
    private MailVerifyService mailVerifyService;

    @Autowired
    private MailVerifyProperties properties;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static String env(String key) {
        String v = System.getenv(key);
        return v != null ? v : "";
    }

    @BeforeAll
    static void requireTestEmail() {
        if (TEST_EMAIL == null || TEST_EMAIL.isBlank() || "replaceme".equals(TEST_EMAIL)) {
            fail("缺少必要的环境变量 → TEST_EMAIL");
        }
    }

    @Test
    void sendAndCheckEmailCodeShouldWork() {
        EmailSendVO vo = mailVerifyService.sendCode(TEST_EMAIL);

        assertThat(vo).isNotNull();
        assertThat(vo.getRequestId()).isNotBlank();
        assertThat(vo.getExpiresIn()).isEqualTo(properties.getValidTime());

        String codeKey = RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + TEST_EMAIL;
        String code = stringRedisTemplate.opsForValue().get(codeKey);
        assertThat(code).isNotBlank();

        mailVerifyService.checkCode(TEST_EMAIL, code);

        String stored = stringRedisTemplate.opsForValue().get(codeKey);
        assertThat(stored).isNull();

        String intervalKey = RedisKeyConstant.MAIL_SEND_INTERVAL_PREFIX + TEST_EMAIL;
        stringRedisTemplate.delete(intervalKey);
    }
}
