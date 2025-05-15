package com.example.student.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 自定义权限验证工具
 */
@Component("ss")
public class SecurityService {

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param permission 权限字符串
     * @return 是否拥有权限
     */
    public boolean hasPermission(String permission) {
        if (!StringUtils.hasText(permission)) {
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
            // 获取用户权限集合
            Set<String> permissions = loginUser.getPermissions();
            // 判断权限集合中是否包含指定权限
            return permissions != null && permissions.contains(permission);
        }
        
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