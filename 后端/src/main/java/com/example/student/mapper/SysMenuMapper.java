package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单Mapper接口
 *
 * @author example
 */
@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    
    /**
     * 查询菜单详情
     *
     * @param id 菜单ID
     * @return 菜单
     */
    SysMenu selectMenuById(Long id);
    
    /**
     * 查询菜单列表
     *
     * @param menu 菜单查询条件
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);
    
    /**
     * 查询菜单树结构
     *
     * @return 菜单树列表
     */
    List<SysMenu> selectMenuTreeAll();
    
    /**
     * 根据用户ID查询菜单树结构
     *
     * @param userId 用户ID
     * @return 菜单树列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);
    
    /**
     * 根据角色ID查询菜单树结构
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 查询父级菜单信息
     *
     * @param parentId 父级菜单ID
     * @return 父级菜单信息
     */
    SysMenu selectParentMenuById(Long parentId);
    
    /**
     * 查询子菜单数量
     *
     * @param menuId 菜单ID
     * @return 子菜单数量
     */
    int selectChildrenMenuCount(Long menuId);
} 