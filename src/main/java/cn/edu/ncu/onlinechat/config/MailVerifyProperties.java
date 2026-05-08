package cn.edu.ncu.onlinechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mail.verify")
public class MailVerifyProperties {

    private Long codeLength = 6L;
    private Long validTime = 300L;
    private Long interval = 60L;
    private String subject = "你的验证码来啦 | Online Chat";
    private String from;
    private String template = "嗨~ 你的验证码是 {code}，{minutes} 分钟内有效。非本人操作请忽略。";
}
