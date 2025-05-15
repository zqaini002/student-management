package com.example.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.student.common.Result;
import com.example.student.entity.SysMenu;
import com.example.student.mapper.SysMenuMapper;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单控制器
 *
 * @author example
 */
@Tag(name = "菜单管理", description = "菜单管理相关接口")
@RestController
@RequestMapping("/system/menu")
@Slf4j
public class MenuController {

    @Resource
    private SysMenuMapper menuMapper;

    /**
     * 获取菜单列表
     *
     * @param menuName 菜单名称
     * @param status 状态
     * @return 菜单列表
     */
    @Operation(summary = "获取菜单列表", description = "获取菜单列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<SysMenu>> getMenuList(
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) Integer status) {
        log.info("查询菜单列表，参数：menuName={}, status={}", menuName, status);
        try {
            SysMenu menu = new SysMenu();
            if (menuName != null && !menuName.isEmpty()) {
                menu.setMenuName(menuName);
            }
            if (status != null) {
                menu.setStatus(status);
            }
            
            List<SysMenu> menuList = menuMapper.selectMenuList(menu);
            
            // 如果自定义方法为空，使用基础方法查询
            if (menuList == null || menuList.isEmpty()) {
                LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
                if (menuName != null && !menuName.isEmpty()) {
                    queryWrapper.like(SysMenu::getMenuName, menuName);
                }
                if (status != null) {
                    queryWrapper.eq(SysMenu::getStatus, status);
                }
                queryWrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
                menuList = menuMapper.selectList(queryWrapper);
            }
            
            log.info("查询到菜单数量：{}", menuList.size());
            return Result.success(menuList);
        } catch (Exception e) {
            log.error("查询菜单列表失败", e);
            return Result.error("获取菜单列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    @Operation(summary = "获取菜单详情", description = "根据ID获取菜单详情")
    @GetMapping("/{menuId}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<SysMenu> getMenuDetail(@PathVariable @Parameter(description = "菜单ID") Long menuId) {
        log.info("查询菜单详情，ID：{}", menuId);
        try {
            SysMenu menu = menuMapper.selectMenuById(menuId);
            if (menu == null) {
                menu = menuMapper.selectById(menuId);
                if (menu == null) {
                    log.warn("菜单不存在，ID：{}", menuId);
                    return Result.error("菜单不存在");
                }
            }
            log.info("查询菜单详情成功，ID：{}", menuId);
            return Result.success(menu);
        } catch (Exception e) {
            log.error("查询菜单详情失败", e);
            return Result.error("获取菜单详情失败：" + e.getMessage());
        }
    }

    /**
     * 添加菜单
     *
     * @param menu 菜单信息
     * @return 添加结果
     */
    @Operation(summary = "添加菜单", description = "添加菜单信息")
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<Void> addMenu(@RequestBody SysMenu menu) {
        log.info("添加菜单：{}", menu);
        try {
            validateMenu(menu, true);
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            menu.setCreateTime(now);
            menu.setUpdateTime(now);
            
            int rows = menuMapper.insert(menu);
            if (rows > 0) {
                log.info("添加菜单成功，ID：{}", menu.getId());
                return Result.success("添加菜单成功");
            } else {
                log.warn("添加菜单失败");
                return Result.error("添加菜单失败");
            }
        } catch (Exception e) {
            log.error("添加菜单失败", e);
            return Result.error("添加菜单失败：" + e.getMessage());
        }
    }

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     * @return 更新结果
     */
    @Operation(summary = "更新菜单", description = "更新菜单信息")
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> updateMenu(@RequestBody SysMenu menu) {
        log.info("更新菜单：{}", menu);
        try {
            if (menu.getId() == null) {
                log.warn("更新菜单失败：ID不能为空");
                return Result.error("菜单ID不能为空");
            }
            
            // 检查菜单是否存在
            SysMenu existingMenu = menuMapper.selectById(menu.getId());
            if (existingMenu == null) {
                log.warn("更新菜单失败：菜单不存在，ID：{}", menu.getId());
                return Result.error("菜单不存在");
            }
            
            // 不能把自己设为自己的父菜单
            if (menu.getId().equals(menu.getParentId())) {
                log.warn("更新菜单失败：上级菜单不能选择自己，ID：{}", menu.getId());
                return Result.error("上级菜单不能选择自己");
            }
            
            // 检查是否选择了自己的子菜单作为父菜单
            if (isChildOfMenu(menu.getId(), menu.getParentId())) {
                log.warn("更新菜单失败：上级菜单不能选择自己的子菜单，ID：{}", menu.getId());
                return Result.error("上级菜单不能选择自己的子菜单");
            }
            
            validateMenu(menu, false);
            
            // 设置更新时间
            menu.setUpdateTime(LocalDateTime.now());
            // 保持创建时间不变
            menu.setCreateTime(existingMenu.getCreateTime());
            
            int rows = menuMapper.updateById(menu);
            if (rows > 0) {
                log.info("更新菜单成功，ID：{}", menu.getId());
                return Result.success("更新菜单成功");
            } else {
                log.warn("更新菜单失败，ID：{}", menu.getId());
                return Result.error("更新菜单失败");
            }
        } catch (Exception e) {
            log.error("更新菜单失败", e);
            return Result.error("更新菜单失败：" + e.getMessage());
        }
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 删除结果
     */
    @Operation(summary = "删除菜单", description = "根据ID删除菜单")
    @DeleteMapping("/{menuId}")
    @PreAuthorize("hasAuthority('system:menu:remove')")
    public Result<Void> deleteMenu(@PathVariable @Parameter(description = "菜单ID") Long menuId) {
        log.info("删除菜单，ID：{}", menuId);
        try {
            // 检查菜单是否存在
            SysMenu existingMenu = menuMapper.selectById(menuId);
            if (existingMenu == null) {
                log.warn("删除菜单失败：菜单不存在，ID：{}", menuId);
                return Result.error("菜单不存在");
            }
            
            // 检查是否有子菜单
            int childCount = menuMapper.selectChildrenMenuCount(menuId);
            if (childCount > 0) {
                log.warn("删除菜单失败：存在子菜单，不允许删除，ID：{}", menuId);
                return Result.error("存在子菜单，不允许删除");
            }
            
            // TODO: 检查是否有角色使用该菜单
            
            int rows = menuMapper.deleteById(menuId);
            if (rows > 0) {
                log.info("删除菜单成功，ID：{}", menuId);
                return Result.success("删除菜单成功");
            } else {
                log.warn("删除菜单失败，ID：{}", menuId);
                return Result.error("删除菜单失败");
            }
        } catch (Exception e) {
            log.error("删除菜单失败", e);
            return Result.error("删除菜单失败：" + e.getMessage());
        }
    }

    /**
     * 获取菜单下拉树结构
     *
     * @return 菜单树
     */
    @Operation(summary = "获取菜单下拉树结构", description = "获取所有菜单的树形结构数据")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<Map<String, Object>>> getMenuTree() {
        log.info("获取菜单树结构");
        try {
            List<SysMenu> menuList = menuMapper.selectMenuTreeAll();
            if (menuList == null || menuList.isEmpty()) {
                // 使用基础方法查询所有菜单
                LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
                menuList = menuMapper.selectList(queryWrapper);
            }
            
            List<Map<String, Object>> treeList = buildMenuTree(menuList);
            log.info("查询菜单树成功，数量：{}", treeList.size());
            return Result.success(treeList);
        } catch (Exception e) {
            log.error("获取菜单树失败", e);
            return Result.error("获取菜单树失败：" + e.getMessage());
        }
    }

    /**
     * 获取角色菜单树
     *
     * @param roleId 角色ID
     * @return 角色菜单树
     */
    @Operation(summary = "获取角色菜单树", description = "获取特定角色的菜单树及选中状态")
    @GetMapping("/roleMenuTree/{roleId}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Map<String, Object>> getRoleMenuTree(@PathVariable @Parameter(description = "角色ID") Long roleId) {
        log.info("获取角色菜单树，角色ID：{}", roleId);
        try {
            // 获取所有菜单
            List<SysMenu> allMenus = menuMapper.selectMenuTreeAll();
            if (allMenus == null || allMenus.isEmpty()) {
                // 使用基础方法查询所有菜单
                LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
                allMenus = menuMapper.selectList(queryWrapper);
            }
            
            // 获取角色已有菜单ID
            List<Long> checkedKeys = menuMapper.selectMenuListByRoleId(roleId);
            if (checkedKeys == null) {
                checkedKeys = new ArrayList<>();
            }
            
            // 构建菜单树
            List<Map<String, Object>> menuTree = buildMenuTree(allMenus);
            
            Map<String, Object> result = new HashMap<>();
            result.put("menus", menuTree);
            result.put("checkedKeys", checkedKeys);
            
            log.info("获取角色菜单树成功，角色ID：{}", roleId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取角色菜单树失败", e);
            return Result.error("获取角色菜单树失败：" + e.getMessage());
        }
    }
    
    /**
     * 构建菜单树形结构
     *
     * @param menuList 菜单列表
     * @return 树形菜单
     */
    private List<Map<String, Object>> buildMenuTree(List<SysMenu> menuList) {
        // 按父菜单ID对菜单进行分组
        Map<Long, List<SysMenu>> parentMap = menuList.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));
        
        // 先找出所有的一级菜单（parentId=0）
        List<SysMenu> rootMenus = parentMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归生成树形结构
        return rootMenus.stream()
                .map(menu -> buildTreeNode(menu, parentMap))
                .collect(Collectors.toList());
    }
    
    /**
     * 递归构建树节点
     *
     * @param menu 当前菜单
     * @param parentMap 父菜单ID与子菜单的映射
     * @return 树节点
     */
    private Map<String, Object> buildTreeNode(SysMenu menu, Map<Long, List<SysMenu>> parentMap) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", menu.getId());
        node.put("menuId", menu.getId()); // 兼容前端使用menuId
        node.put("label", menu.getMenuName());
        node.put("menuName", menu.getMenuName());
        node.put("icon", menu.getIcon() != null && !menu.getIcon().equals("#") ? menu.getIcon() : "");
        node.put("menuType", menu.getMenuType());
        node.put("parentId", menu.getParentId());
        node.put("orderNum", menu.getOrderNum());
        node.put("path", menu.getPath());
        node.put("component", menu.getComponent());
        node.put("perms", menu.getPerms());
        node.put("visible", menu.getVisible());
        node.put("status", menu.getStatus());
        
        // 获取子菜单
        List<SysMenu> children = parentMap.getOrDefault(menu.getId(), new ArrayList<>());
        if (!children.isEmpty()) {
            node.put("children", children.stream()
                    .map(child -> buildTreeNode(child, parentMap))
                    .collect(Collectors.toList()));
        }
        
        return node;
    }
    
    /**
     * 判断一个菜单是否是另一个菜单的子菜单
     *
     * @param id 菜单ID
     * @param parentId 父菜单ID
     * @return 是否是子菜单
     */
    private boolean isChildOfMenu(Long id, Long parentId) {
        if (parentId == null || parentId == 0L) {
            return false;
        }
        
        if (id.equals(parentId)) {
            return true;
        }
        
        // 获取父菜单
        SysMenu parentMenu = menuMapper.selectById(parentId);
        if (parentMenu == null) {
            return false;
        }
        
        // 递归检查父菜单的父菜单
        return isChildOfMenu(id, parentMenu.getParentId());
    }
    
    /**
     * 校验菜单数据
     *
     * @param menu 菜单数据
     * @param isAdd 是否是新增操作
     */
    private void validateMenu(SysMenu menu, boolean isAdd) {
        // 菜单名称不能为空
        if (menu.getMenuName() == null || menu.getMenuName().isEmpty()) {
            throw new IllegalArgumentException("菜单名称不能为空");
        }
        
        // 菜单类型不能为空
        if (menu.getMenuType() == null || menu.getMenuType().isEmpty()) {
            throw new IllegalArgumentException("菜单类型不能为空");
        }
        
        // 按钮类型菜单必须设置权限标识
        if ("F".equals(menu.getMenuType()) && (menu.getPerms() == null || menu.getPerms().isEmpty())) {
            throw new IllegalArgumentException("按钮类型菜单必须设置权限标识");
        }
        
        // 路由地址必须以/开头（目录和菜单）
        if (("M".equals(menu.getMenuType()) || "C".equals(menu.getMenuType())) 
                && menu.getPath() != null && !menu.getPath().isEmpty() && !menu.getPath().startsWith("/")) {
            menu.setPath("/" + menu.getPath());
        }
        
        // 检查父菜单是否存在
        if (menu.getParentId() != null && menu.getParentId() != 0) {
            SysMenu parentMenu = menuMapper.selectById(menu.getParentId());
            if (parentMenu == null) {
                throw new IllegalArgumentException("父菜单不存在");
            }
            
            // 父菜单不能是按钮类型
            if ("F".equals(parentMenu.getMenuType())) {
                throw new IllegalArgumentException("不能将按钮类型菜单设置为父菜单");
            }
        }
    }
} 