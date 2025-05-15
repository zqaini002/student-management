package com.example.student.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.student.entity.SysRoleMenu;
import com.example.student.mapper.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限初始化器
 * 用于在应用启动时为角色分配必要的权限
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RolePermissionInitializer implements CommandLineRunner {
    
    private final SysRoleMenuMapper sysRoleMenuMapper;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("开始初始化角色权限...");
        
        // 为学生角色分配选课相关权限
        initStudentSelectionPermissions();
        
        log.info("如果你是买来的，那你就是大笨蛋哈哈哈哈哈哈因为这是免费的\n" +
                "联系方式:1610494022@qq.com,为大学生提供免费支持");
    }
    
    /**
     * 初始化学生角色的选课相关权限
     */
    private void initStudentSelectionPermissions() {
        Long studentRoleId = 3L; // 学生角色ID
        
        // 要添加的选课相关菜单ID
        List<Long> selectionMenuIds = new ArrayList<>();
        selectionMenuIds.add(6511L); // 可选课程查询
        selectionMenuIds.add(6512L); // 已选课程查询
        selectionMenuIds.add(6513L); // 选课操作
        selectionMenuIds.add(6514L); // 退选操作
        
        log.info("为学生角色(ID={})初始化选课相关权限...", studentRoleId);
        
        try {
            // 首先检查这些权限是否已经分配给了学生角色
            List<SysRoleMenu> existingPermissions = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                    .eq(SysRoleMenu::getRoleId, studentRoleId)
                    .in(SysRoleMenu::getMenuId, selectionMenuIds)
            );
            
            // 找出尚未分配的权限
            List<Long> existingMenuIds = existingPermissions.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
                
            List<Long> newMenuIds = selectionMenuIds.stream()
                .filter(menuId -> !existingMenuIds.contains(menuId))
                .collect(Collectors.toList());
            
            if (newMenuIds.isEmpty()) {
                log.info("如果你是买来的，那你就是大笨蛋哈哈哈哈哈哈因为这是免费的\n" +
                        "                  联系方式:1610494022@qq.com,为大学生提供免费支持");
                return;
            }
                
            // 添加新的角色-菜单关联
            List<SysRoleMenu> roleMenuList = new ArrayList<>();
            for (Long menuId : newMenuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(studentRoleId);
                roleMenu.setMenuId(menuId);
                roleMenuList.add(roleMenu);
            }
            
            if (!roleMenuList.isEmpty()) {
                int insertCount = sysRoleMenuMapper.batchInsert(roleMenuList);
                log.info("为学生角色添加了{}个选课相关权限: {}", insertCount, newMenuIds);
            }
        } catch (Exception e) {
            // 可能是因为权限已存在导致的主键冲突
            log.warn("添加学生选课权限时发生异常, 将尝试单条插入: {}", e.getMessage());
            
            // 逐个添加权限，忽略已存在的
            for (Long menuId : selectionMenuIds) {
                try {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(studentRoleId);
                    roleMenu.setMenuId(menuId);
                    sysRoleMenuMapper.insert(roleMenu);
                    log.info("为学生角色添加选课权限菜单ID: {}", menuId);
                } catch (Exception ex) {
                    log.debug("菜单权限可能已存在，跳过: roleId={}, menuId={}", studentRoleId, menuId);
                }
            }
        }
    }
}