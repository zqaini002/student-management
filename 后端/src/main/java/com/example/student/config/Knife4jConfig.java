package com.example.student.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Knife4j增强配置
 *
 * @author example
 */
@Configuration
public class Knife4jConfig {
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    /**
     * 读取资源内容
     */
    private String readResourceContent(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    
    /**
     * 创建自定义OpenAPI配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 创建API基本信息
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("学生信息管理系统API文档")
                        .description("学生信息管理系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("管理员").email("admin@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
} 