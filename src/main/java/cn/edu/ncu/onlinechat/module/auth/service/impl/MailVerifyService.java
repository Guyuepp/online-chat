package cn.edu.ncu.onlinechat.module.auth.service.impl;

import cn.edu.ncu.onlinechat.common.constant.RedisKeyConstant;
import cn.edu.ncu.onlinechat.common.exception.BusinessException;
import cn.edu.ncu.onlinechat.common.result.ResultCode;
import cn.edu.ncu.onlinechat.config.MailVerifyProperties;
import cn.edu.ncu.onlinechat.module.auth.service.VerifyCodeService;
import cn.edu.ncu.onlinechat.module.auth.vo.EmailSendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailVerifyService implements VerifyCodeService {

    private final JavaMailSender mailSender;
    private final MailVerifyProperties properties;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public EmailSendVO sendCode(String email) {
        String intervalKey = RedisKeyConstant.MAIL_SEND_INTERVAL_PREFIX + email;
        Long interval = properties.getInterval();
        if (interval != null && interval > 0) {
            Boolean locked = stringRedisTemplate.opsForValue()
                    .setIfAbsent(intervalKey, "1", interval, TimeUnit.SECONDS);
            if (!Boolean.TRUE.equals(locked)) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "mail send too frequent");
            }
        }

        String code = generateCode(properties.getCodeLength());
        String codeKey = RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + email;
        Long validTime = properties.getValidTime();

        try {
            if (validTime != null && validTime > 0) {
                stringRedisTemplate.opsForValue().set(codeKey, code, validTime, TimeUnit.SECONDS);
            } else {
                stringRedisTemplate.opsForValue().set(codeKey, code);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            String subject = properties.getSubject();
            message.setSubject((subject == null || subject.isBlank()) ? "Your verification code" : subject);
            String from = properties.getFrom();
            if (from != null && !from.isBlank()) {
                message.setFrom(from);
            }
            message.setText(buildContent(code, validTime));

            mailSender.send(message);
        } catch (Exception e) {
            stringRedisTemplate.delete(codeKey);
            if (interval != null && interval > 0) {
                stringRedisTemplate.delete(intervalKey);
            }
            throw new BusinessException(ResultCode.SERVER_ERROR, "mail send failed: " + e.getMessage());
        }

        EmailSendVO vo = new EmailSendVO();
        vo.setRequestId(UUID.randomUUID().toString());
        vo.setExpiresIn(validTime);
        return vo;
    }

    @Override
    public void checkCode(String email, String code) {
        String codeKey = RedisKeyConstant.MAIL_VERIFY_CODE_PREFIX + email;
        String stored = stringRedisTemplate.opsForValue().get(codeKey);
        if (stored == null || !stored.equals(code)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "mail code invalid or expired");
        }
        stringRedisTemplate.delete(codeKey);
    }

    private String generateCode(Long length) {
        int size = length != null && length > 0 ? length.intValue() : 6;
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        return builder.toString();
    }

    private String buildContent(String code, Long validTime) {
        String template = properties.getTemplate();
        if (template == null || template.isBlank()) {
            template = "Your verification code is {code}. It expires in {minutes} minutes.";
        }
        long minutes = resolveMinutes(validTime);
        return template.replace("{code}", code)
                .replace("{minutes}", String.valueOf(minutes));
    }

    private long resolveMinutes(Long validTime) {
        if (validTime == null || validTime <= 0) {
            return 5L;
        }
        return Math.max(1L, (long) Math.ceil(validTime / 60.0));
    }
}
