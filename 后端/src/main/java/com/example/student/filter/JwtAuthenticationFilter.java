package com.example.student.filter;

import com.example.student.config.JwtConfig;
import com.example.student.security.LoginUser;
import com.example.student.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 *
 * @author example
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 获取请求路径和方法
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.info("处理请求: {} {}", method, requestURI);
        
        // 从请求头中获取JWT
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            log.info("找到令牌: {}", token.substring(0, Math.min(10, token.length())) + "...");
            try {
                // 从JWT中获取用户名
                String username = jwtUtil.getUsernameFromToken(token);
                log.info("从令牌中提取用户名: {}", username);

                // 如果用户名不为空，且安全上下文中不存在认证信息，则进行认证
                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 从数据库中加载用户信息
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 显示详细的权限信息用于调试
                    String authorities = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", "));
                    log.info("加载用户详情: {}, 权限: {}", username, authorities);

                    // 验证JWT是否有效
                    if (jwtUtil.validateToken(token, userDetails)) {
                        // 创建认证令牌
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 将认证令牌设置到安全上下文中
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        log.info("用户 {} 认证成功，权限: {}", username, authorities);
                        
                        // 特别检查是否有访问仪表盘所需的权限
                        if (requestURI.startsWith("/dashboard")) {
                            boolean hasDashboardAccess = userDetails.getAuthorities().stream()
                                .anyMatch(auth -> {
                                    String authority = auth.getAuthority();
                                    return authority.equals("admin") || 
                                           authority.equals("teacher") || 
                                           authority.equals("student");
                                });
                            log.info("用户 {} 访问仪表盘: {}, 是否有权限: {}", username, requestURI, hasDashboardAccess);
                        }
                    } else {
                        log.warn("令牌验证失败: {}", token.substring(0, Math.min(10, token.length())) + "...");
                    }
                }
            } catch (JwtException e) {
                log.error("JWT验证失败: {}", e.getMessage());
            }
        } else {
            log.warn("请求未包含令牌: {} {}", method, requestURI);
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
        
        // 记录响应状态
        log.info("请求完成: {} {} 状态码: {}", method, requestURI, response.getStatus());
    }

    /**
     * 从请求头中获取JWT
     *
     * @param request HTTP请求
     * @return JWT
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getTokenPrefix() + " ")) {
            return bearerToken.substring(jwtConfig.getTokenPrefix().length() + 1);
        }
        return null;
    }
} 