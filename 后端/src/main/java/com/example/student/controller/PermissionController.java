package com.example.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.student.common.Result;
import com.example.student.entity.SysMenu;
import com.example.student.mapper.SysMenuMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限控制器
 *
 * @author example
 */
@Tag(name = "权限管理", description = "权限管理相关接口")
@RestController
@RequestMapping("/system/permission")
@Slf4j
public class PermissionController {

    @Resource
    private SysMenuMapper menuMapper;

    /**
     * 获取所有可分配的权限列表
     *
     * @return 权限列表
     */
    @Operation(summary = "获取所有可分配的权限列表", description = "获取系统中所有可分配的权限标识")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<List<String>> getAllPermissions() {
        log.info("获取所有权限列表");
        try {
            // 从菜单表中查询所有权限标识
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(SysMenu::getPerms);
            queryWrapper.isNotNull(SysMenu::getPerms);
            queryWrapper.ne(SysMenu::getPerms, "");
            
            List<SysMenu> menuList = menuMapper.selectList(queryWrapper);
            List<String> permissions = menuList.stream()
                    .map(SysMenu::getPerms)
                    .filter(perms -> perms != null && !perms.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
            
            log.info("获取权限列表成功，权限数量：{}", permissions.size());
            return Result.success(permissions);
        } catch (Exception e) {
            log.error("获取权限列表失败", e);
            return Result.error("获取权限列表失败：" + e.getMessage());
        }
    }
} 