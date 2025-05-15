package com.example.student.security;

import com.example.student.entity.SysUser;
import com.example.student.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户详情服务实现
 *
 * @author example
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("加载用户信息: {}", username);
        
        // 根据用户名查询用户
        SysUser user = sysUserMapper.selectUserByUsername(username);
        if (user == null) {
            log.warn("用户名 {} 不存在", username);
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 查询用户权限信息
        List<String> permissions = sysUserMapper.selectPermsByUserId(user.getId());
        log.info("用户 {} 的权限列表: {}", username, permissions);

        // 查询用户角色
        List<String> roles = sysUserMapper.selectRoleKeysByUserId(user.getId());
        log.info("用户 {} 的角色列表: {}", username, roles);

        // 构建LoginUser对象
        LoginUser loginUser = new LoginUser(user, permissions);
        loginUser.setRoles(roles);

        return loginUser;
    }
} 