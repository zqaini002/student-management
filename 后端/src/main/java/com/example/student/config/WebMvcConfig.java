package com.example.student.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 *
 * @author example
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Value("${file.access.path:/uploads}")
    private String accessPath;

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问，支持两种路径访问方式
        // 1. 直接通过/uploads访问
        registry.addResourceHandler(accessPath + "/**")
                .addResourceLocations("file:" + uploadPath + "/");
        
        // 2. 通过/api/uploads访问（前端代理方式）
        registry.addResourceHandler("/api" + accessPath + "/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
} 