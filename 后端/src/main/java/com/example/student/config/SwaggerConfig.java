package com.example.student.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置
 *
 * @author example
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI配置
     */
    @Bean
    public OpenAPI openAPI() {
        // 添加JWT认证配置
        final String securitySchemeName = "Bearer认证";
        
        return new OpenAPI()
                .info(new Info()
                        .title("学生信息管理系统API文档")
                        .description("学生信息管理系统API文档，包含系统所有接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("example")
                                .email("example@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                // 添加JWT安全配置
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT认证，请在下方输入框输入Bearer {token}"))
                );
    }
} 