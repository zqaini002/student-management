package com.example.student.util;

import com.example.student.entity.SysUser;
import com.example.student.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

/**
 * 安全工具类
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户
     */
    public static SysUser getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return ((LoginUser) principal).getUser();
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        SysUser user = getCurrentUser();
        return user == null ? null : user.getId();
    }
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        return getCurrentUserId();
    }

    /**
     * 获取当前登录用户的用户名
     */
    public static String getCurrentUsername() {
        SysUser user = getCurrentUser();
        return user == null ? null : user.getUsername();
    }

    /**
     * 获取认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 判断当前用户是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
    
    /**
     * 判断当前用户是否拥有指定角色
     * 
     * @param role 角色标识
     * @return 是否拥有指定角色
     */
    public static boolean hasRole(String role) {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.contains(new SimpleGrantedAuthority(role));
    }
    
    /**
     * 创建权限对象
     * 
     * @param authority 权限标识
     * @return 权限对象
     */
    public static GrantedAuthority createAuthority(String authority) {
        return new SimpleGrantedAuthority(authority);
    }
} 
