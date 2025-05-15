package com.example.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.Result;
import com.example.student.entity.SysRole;
import com.example.student.entity.SysRoleMenu;
import com.example.student.entity.SysMenu;
import com.example.student.mapper.SysMenuMapper;
import com.example.student.mapper.SysRoleMapper;
import com.example.student.mapper.SysRoleMenuMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色控制器
 *
 * @author example
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/system/role")
@Slf4j
public class RoleController {

    @Resource
    private SysRoleMapper roleMapper;
    
    @Resource
    private SysRoleMenuMapper roleMenuMapper;
    
    @Resource
    private SysMenuMapper menuMapper;

    /**
     * 获取角色列表
     *
     * @param current 当前页
     * @param size 每页数量
     * @param roleName 角色名称
     * @param roleKey 角色权限字符串
     * @param status 状态
     * @return 角色列表
     */
    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<IPage<SysRole>> getRoleList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleKey,
            @RequestParam(required = false) Integer status) {
        log.info("查询角色列表，参数：current={}, size={}, roleName={}, roleKey={}, status={}", 
                current, size, roleName, roleKey, status);
        try {
            Page<SysRole> page = new Page<>(current, size);
            SysRole role = new SysRole();
            if (roleName != null && !roleName.isEmpty()) {
                role.setRoleName(roleName);
            }
            if (roleKey != null && !roleKey.isEmpty()) {
                role.setRoleKey(roleKey);
            }
            if (status != null) {
                role.setStatus(status);
            }
            
            IPage<SysRole> rolePage = roleMapper.selectRolePage(page, role);
            
            // 如果自定义方法返回为空，使用基础方法查询
            if (rolePage == null || rolePage.getRecords().isEmpty()) {
                LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
                if (roleName != null && !roleName.isEmpty()) {
                    queryWrapper.like(SysRole::getRoleName, roleName);
                }
                if (roleKey != null && !roleKey.isEmpty()) {
                    queryWrapper.like(SysRole::getRoleKey, roleKey);
                }
                if (status != null) {
                    queryWrapper.eq(SysRole::getStatus, status);
                }
                queryWrapper.orderByAsc(SysRole::getRoleSort);
                
                rolePage = roleMapper.selectPage(page, queryWrapper);
            }
            
            log.info("查询到角色数量：{}", rolePage.getTotal());
            return Result.success(rolePage);
        } catch (Exception e) {
            log.error("查询角色列表失败", e);
            return Result.error("获取角色列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<SysRole> getRoleDetail(@PathVariable @Parameter(description = "角色ID") Long roleId) {
        log.info("查询角色详情，ID：{}", roleId);
        try {
            SysRole role = roleMapper.selectRoleById(roleId);
            if (role == null) {
                role = roleMapper.selectById(roleId);
                if (role == null) {
                    log.warn("角色不存在，ID：{}", roleId);
                    return Result.error("角色不存在");
                }
            }
            log.info("查询角色详情成功，ID：{}", roleId);
            return Result.success(role);
        } catch (Exception e) {
            log.error("查询角色详情失败", e);
            return Result.error("获取角色详情失败：" + e.getMessage());
        }
    }

    /**
     * 添加角色
     *
     * @param role 角色信息
     * @return 添加结果
     */
    @Operation(summary = "添加角色", description = "添加角色信息")
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public Result<Void> addRole(@RequestBody SysRole role) {
        log.info("添加角色：{}", role);
        try {
            // 校验角色名称是否唯一
            int count = roleMapper.checkRoleNameUnique(role.getRoleName(), null);
            if (count > 0) {
                log.warn("添加角色失败：角色名称已存在，roleName={}", role.getRoleName());
                return Result.error("角色名称已存在");
            }
            
            // 校验角色权限字符串是否唯一
            count = roleMapper.checkRoleKeyUnique(role.getRoleKey(), null);
            if (count > 0) {
                log.warn("添加角色失败：角色权限已存在，roleKey={}", role.getRoleKey());
                return Result.error("角色权限已存在");
            }
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            role.setCreateTime(now);
            role.setUpdateTime(now);
            
            int rows = roleMapper.insert(role);
            if (rows > 0) {
                log.info("添加角色成功，ID：{}", role.getId());
                return Result.success("添加角色成功");
            } else {
                log.warn("添加角色失败");
                return Result.error("添加角色失败");
            }
        } catch (Exception e) {
            log.error("添加角色失败", e);
            return Result.error("添加角色失败：" + e.getMessage());
        }
    }

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 更新结果
     */
    @Operation(summary = "更新角色", description = "更新角色信息")
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateRole(@RequestBody SysRole role) {
        log.info("更新角色：{}", role);
        try {
            if (role.getId() == null) {
                log.warn("更新角色失败：ID不能为空");
                return Result.error("角色ID不能为空");
            }
            
            // 检查角色是否存在
            SysRole existingRole = roleMapper.selectById(role.getId());
            if (existingRole == null) {
                log.warn("更新角色失败：角色不存在，ID：{}", role.getId());
                return Result.error("角色不存在");
            }
            
            // 校验角色名称是否唯一
            int count = roleMapper.checkRoleNameUnique(role.getRoleName(), role.getId());
            if (count > 0) {
                log.warn("更新角色失败：角色名称已存在，roleName={}", role.getRoleName());
                return Result.error("角色名称已存在");
            }
            
            // 校验角色权限字符串是否唯一
            count = roleMapper.checkRoleKeyUnique(role.getRoleKey(), role.getId());
            if (count > 0) {
                log.warn("更新角色失败：角色权限已存在，roleKey={}", role.getRoleKey());
                return Result.error("角色权限已存在");
            }
            
            // 设置更新时间
            role.setUpdateTime(LocalDateTime.now());
            // 保持创建时间不变
            role.setCreateTime(existingRole.getCreateTime());
            
            int rows = roleMapper.updateById(role);
            if (rows > 0) {
                log.info("更新角色成功，ID：{}", role.getId());
                return Result.success("更新角色成功");
            } else {
                log.warn("更新角色失败，ID：{}", role.getId());
                return Result.error("更新角色失败");
            }
        } catch (Exception e) {
            log.error("更新角色失败", e);
            return Result.error("更新角色失败：" + e.getMessage());
        }
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 删除结果
     */
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Result<Void> deleteRole(@PathVariable @Parameter(description = "角色ID") Long roleId) {
        log.info("删除角色，ID：{}", roleId);
        try {
            // 检查角色是否存在
            SysRole existingRole = roleMapper.selectById(roleId);
            if (existingRole == null) {
                log.warn("删除角色失败：角色不存在，ID：{}", roleId);
                return Result.error("角色不存在");
            }
            
            // TODO: 检查角色是否有用户在使用
            
            // 删除角色菜单关联
            roleMenuMapper.deleteByRoleId(roleId);
            
            int rows = roleMapper.deleteById(roleId);
            if (rows > 0) {
                log.info("删除角色成功，ID：{}", roleId);
                return Result.success("删除角色成功");
            } else {
                log.warn("删除角色失败，ID：{}", roleId);
                return Result.error("删除角色失败");
            }
        } catch (Exception e) {
            log.error("删除角色失败", e);
            return Result.error("删除角色失败：" + e.getMessage());
        }
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 修改结果
     */
    @Operation(summary = "修改角色状态", description = "修改角色状态")
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> changeRoleStatus(@RequestBody SysRole role) {
        log.info("修改角色状态，ID：{}，状态：{}", role.getId(), role.getStatus());
        try {
            if (role.getId() == null) {
                log.warn("修改角色状态失败：ID不能为空");
                return Result.error("角色ID不能为空");
            }
            
            // 检查角色是否存在
            SysRole existingRole = roleMapper.selectById(role.getId());
            if (existingRole == null) {
                log.warn("修改角色状态失败：角色不存在，ID：{}", role.getId());
                return Result.error("角色不存在");
            }
            
            // 只更新状态和更新时间字段
            SysRole updateRole = new SysRole();
            updateRole.setId(role.getId());
            updateRole.setStatus(role.getStatus());
            updateRole.setUpdateTime(LocalDateTime.now());
            
            int rows = roleMapper.updateById(updateRole);
            if (rows > 0) {
                log.info("修改角色状态成功，ID：{}", role.getId());
                return Result.success("修改角色状态成功");
            } else {
                log.warn("修改角色状态失败，ID：{}", role.getId());
                return Result.error("修改角色状态失败");
            }
        } catch (Exception e) {
            log.error("修改角色状态失败", e);
            return Result.error("修改角色状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取角色权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Operation(summary = "获取角色权限列表", description = "获取角色已分配的菜单权限")
    @GetMapping("/permissions/{roleId}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<List<String>> getRolePermissions(@PathVariable @Parameter(description = "角色ID") Long roleId) {
        log.info("获取角色权限列表，角色ID：{}", roleId);
        try {
            // 检查角色是否存在
            SysRole existingRole = roleMapper.selectById(roleId);
            if (existingRole == null) {
                log.warn("获取角色权限失败：角色不存在，ID：{}", roleId);
                return Result.error("角色不存在");
            }
            
            // 查询角色已分配的菜单ID
            List<Long> menuIds = menuMapper.selectMenuListByRoleId(roleId);
            
            // 查询这些菜单ID对应的权限标识
            List<String> permissions = new ArrayList<>();
            if (!menuIds.isEmpty()) {
                LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.select(SysMenu::getPerms);
                queryWrapper.in(SysMenu::getId, menuIds);
                queryWrapper.isNotNull(SysMenu::getPerms);
                queryWrapper.ne(SysMenu::getPerms, "");
                
                List<SysMenu> menuList = menuMapper.selectList(queryWrapper);
                permissions = menuList.stream()
                        .map(SysMenu::getPerms)
                        .filter(perms -> perms != null && !perms.isEmpty())
                        .collect(Collectors.toList());
            }
            
            log.info("获取角色权限列表成功，角色ID：{}，权限数量：{}", roleId, permissions.size());
            return Result.success(permissions);
        } catch (Exception e) {
            log.error("获取角色权限列表失败", e);
            return Result.error("获取角色权限列表失败：" + e.getMessage());
        }
    }

    /**
     * 更新角色权限
     *
     * @param params 包含角色ID和菜单ID列表的参数对象
     * @return 更新结果
     */
    @Operation(summary = "更新角色权限", description = "更新角色菜单权限")
    @PutMapping("/permissions")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateRolePermissions(@RequestBody Map<String, Object> params) {
        Long roleId;
        List<Long> menuIds = new ArrayList<>();
        Set<String> permStrings = new HashSet<>(); // 用于记录所有权限字符串
        
        try {
            roleId = Long.valueOf(params.get("roleId").toString());
            
            if (params.get("menuIds") != null) {
                List<?> menuIdsRaw = (List<?>) params.get("menuIds");
                
                for (Object item : menuIdsRaw) {
                    if (item instanceof Integer) {
                        menuIds.add(Long.valueOf((Integer) item));
                    } else if (item instanceof Long) {
                        menuIds.add((Long) item);
                    } else if (item instanceof String) {
                        // 处理字符串类型的权限标识
                        String permString = (String) item;
                        permStrings.add(permString); // 记录权限字符串
                        log.info("收到权限标识: {}", permString);
                        
                        // 查找匹配的菜单ID
                        try {
                            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
                            queryWrapper.eq(SysMenu::getPerms, permString);
                            SysMenu menu = menuMapper.selectOne(queryWrapper);
                            
                            if (menu != null) {
                                menuIds.add(menu.getId());
                                log.info("找到匹配的菜单ID: {}, 权限: {}", menu.getId(), permString);
                            } else {
                                log.warn("未找到匹配的菜单ID，权限: {}", permString);
                            }
                        } catch (Exception e) {
                            log.error("查询权限菜单失败: {}", permString, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析参数失败", e);
            return Result.error("参数格式错误：" + e.getMessage());
        }
        
        log.info("更新角色权限，角色ID：{}，菜单数量：{}", roleId, menuIds.size());
        
        try {
            // 检查角色是否存在
            SysRole existingRole = roleMapper.selectById(roleId);
            if (existingRole == null) {
                log.warn("更新角色权限失败：角色不存在，ID：{}", roleId);
                return Result.error("角色不存在");
            }
            
            // 删除原有的角色菜单关联
            roleMenuMapper.deleteByRoleId(roleId);
            
            // 批量插入新的角色菜单关联
            if (!menuIds.isEmpty()) {
                List<SysRoleMenu> roleMenuList = menuIds.stream()
                        .distinct() // 去重
                        .map(menuId -> {
                            SysRoleMenu roleMenu = new SysRoleMenu();
                            roleMenu.setRoleId(roleId);
                            roleMenu.setMenuId(menuId);
                            return roleMenu;
                        })
                        .collect(Collectors.toList());
                
                int rows = roleMenuMapper.batchInsert(roleMenuList);
                log.info("批量插入角色菜单关联，影响行数：{}", rows);
                if (rows <= 0) {
                    log.warn("批量插入角色菜单关联失败");
                }
            }
            
            // 更新角色更新时间
            SysRole updateRole = new SysRole();
            updateRole.setId(roleId);
            updateRole.setUpdateTime(LocalDateTime.now());
            roleMapper.updateById(updateRole);
            
            log.info("更新角色权限成功，角色ID：{}", roleId);
            return Result.success("更新角色权限成功");
        } catch (Exception e) {
            log.error("更新角色权限失败", e);
            return Result.error("更新角色权限失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有可分配的权限列表
     *
     * @return 权限列表
     */
    @Operation(summary = "获取所有可分配的权限列表", description = "获取系统中所有可分配的权限标识")
    @GetMapping("/permissions/all")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<List<String>> getAllPermissions() {
        log.info("获取所有权限列表");
        try {
            // 此处从菜单表中查询所有权限标识
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