package cn.edu.ncu.onlinechat.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("cn.edu.ncu.onlinechat.module.**.mapper")
public class MybatisConfig {
}
