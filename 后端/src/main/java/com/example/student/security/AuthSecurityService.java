package com.example.student.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义权限验证工具
 * 与SecurityService类似，但Bean名称为auth
 */
@Component("auth")
@Slf4j
public class AuthSecurityService {

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param permission 权限字符串
     * @return 是否拥有权限
     */
    public boolean hasPermi(String permission) {
        log.info("验证权限: {}", permission);
        if (!StringUtils.hasText(permission)) {
            log.warn("权限字符串为空");
            return false;
        }
        
        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("无法获取认证信息");
            return false;
        }
        
        // 获取用户详情
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) principal;
            // 获取用户权限集合
            Set<String> permissions = loginUser.getPermissions();
            // 判断权限集合中是否包含指定权限
            boolean hasPermission = permissions != null && permissions.contains(permission);
            log.info("用户 {} 请求权限 {}, 结果: {}", loginUser.getUsername(), permission, hasPermission);
            return hasPermission;
        }
        
        log.warn("用户认证信息不是LoginUser类型");
        return false;
    }
    
    /**
     * 判断当前用户是否拥有指定角色
     *
     * @param role 角色字符串
     * @return 是否拥有角色
     */
    public boolean hasRole(String role) {
        if (!StringUtils.hasText(role)) {
            return false;
        }
        
        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        // 获取用户详情
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) principal;
            // 获取用户角色集合
            Set<String> roles = loginUser.getRoles();
            // 判断角色集合中是否包含指定角色
            return roles != null && roles.contains(role);
        }
        
        return false;
    }
    
    /**
     * 判断当前用户是否拥有指定角色中的任意一个
     *
     * @param roles 角色字符串数组
     * @return 是否拥有任意一个角色
     */
    public boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        
        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        // 获取用户详情
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) principal;
            // 获取用户角色集合
            Set<String> userRoles = loginUser.getRoles();
            if (userRoles != null) {
                for (String role : roles) {
                    if (userRoles.contains(role)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
} 
 