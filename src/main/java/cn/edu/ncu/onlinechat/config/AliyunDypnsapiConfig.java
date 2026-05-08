package cn.edu.ncu.onlinechat.config;

import com.aliyun.credentials.models.Config;
import com.aliyun.dypnsapi20170525.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AliyunDypnsapiConfig {

    private final AliyunDypnsapiProperties properties;

    @Bean
    public Client dypnsapiClient() throws Exception {
        Config credentialConfig = new Config()
            .setType("access_key")
                .setAccessKeyId(properties.getAccessKeyId())
                .setAccessKeySecret(properties.getAccessKeySecret());
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client(credentialConfig);

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credential);
        config.endpoint = properties.getEndpoint();
        return new Client(config);
    }
}
