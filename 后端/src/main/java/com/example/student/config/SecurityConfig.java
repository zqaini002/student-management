package com.example.student.config;

import com.example.student.filter.JwtAuthenticationFilter;
import com.example.student.handler.AccessDeniedHandlerImpl;
import com.example.student.handler.AuthenticationEntryPointImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置
 *
 * @author example
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    private UserDetailsService userDetailsService;
    
    @Resource
    private AuthenticationEntryPointImpl authenticationEntryPoint;
    
    @Resource
    private AccessDeniedHandlerImpl accessDeniedHandler;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        log.info("创建认证管理器，使用UserDetailsService: {}", userDetailsService.getClass().getName());
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 关闭CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 不通过Session获取SecurityContext
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 请求授权
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // 允许OPTIONS请求通过，解决跨域问题
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        // 允许静态资源访问
                        .requestMatchers("/", "/index.html").permitAll()
                        // 开放favicon.ico的访问路径
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/api/favicon.ico").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        // 允许上传文件路径访问
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/uploads/**").permitAll()
                        // 允许Swagger/Knife4j相关资源访问
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers("/doc.html", "/api/doc.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/swagger-config").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/configuration/ui").permitAll()
                        .requestMatchers("/configuration/security").permitAll()
                        // 允许匿名访问的接口
                        .requestMatchers("/login", "/api/login").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/user/info").permitAll()
                        .requestMatchers("/auth/logout").permitAll()
                        // 除上面外的所有请求全部需要鉴权认证
                        .anyRequest().authenticated()
                )
                // 添加JWT过滤器，在UsernamePasswordAuthenticationFilter之前执行
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 异常处理
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }
} 