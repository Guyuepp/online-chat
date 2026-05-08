package cn.edu.ncu.onlinechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.dypnsapi")
public class AliyunDypnsapiProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String schemeName;
    private String signName;
    private String templateCode;
    private String countryCode = "86";
    private Long codeLength = 6L;
    private Long validTime = 300L;
    private Long interval = 60L;
    private Boolean returnVerifyCode = false;
}
