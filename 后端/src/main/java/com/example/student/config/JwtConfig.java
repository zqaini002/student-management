package com.example.student.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类
 *
 * @author example
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT加解密使用的密钥
     */
    private String secret;

    /**
     * JWT的有效期(单位:秒)
     */
    private Long expire;

    /**
     * JWT存储在请求头中的名称
     */
    private String header;

    /**
     * JWT前缀
     */
    private String tokenPrefix;
} 