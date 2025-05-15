package com.example.student.security;

import com.example.student.entity.SysUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户信息
 *
 * @author example
 */
@Data
@NoArgsConstructor
@Slf4j
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户信息
     */
    private SysUser user;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 构造方法
     *
     * @param user        用户信息
     * @param permissions 权限列表
     */
    public LoginUser(SysUser user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    /**
     * 设置用户角色
     * 
     * @param roles 角色列表
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * 获取权限集合
     *
     * @return 权限集合
     */
    public Set<String> getPermissions() {
        return permissions != null ? new HashSet<>(permissions) : new HashSet<>();
    }

    /**
     * 获取角色集合
     *
     * @return 角色集合
     */
    public Set<String> getRoles() {
        return roles != null ? new HashSet<>(roles) : new HashSet<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加权限
        if (permissions != null) {
            for (String permission : permissions) {
                if (permission != null && !permission.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(permission));
                    log.debug("添加权限: {}", permission);
                }
            }
        }
        
        // 添加角色（直接使用角色名称作为权限标识符，不添加ROLE_前缀）
        if (roles != null) {
            for (String role : roles) {
                if (role != null && !role.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(role));
                    log.debug("添加角色: {}", role);
                }
            }
        }
        
        log.info("用户 {} 的权限列表: {}", user.getUsername(), authorities);
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 0;
    }
} 