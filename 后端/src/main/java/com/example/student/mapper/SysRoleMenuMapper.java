package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色菜单关联Mapper接口
 *
 * @author example
 */
@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
    
    /**
     * 批量新增角色菜单关联
     *
     * @param roleMenuList 角色菜单列表
     * @return 插入数量
     */
    int batchInsert(List<SysRoleMenu> roleMenuList);
    
    /**
     * 通过角色ID删除角色和菜单关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteByRoleId(Long roleId);
    
    /**
     * 批量删除角色菜单关联
     *
     * @param roleIds 角色ID数组
     * @return 结果
     */
    int deleteByRoleIds(@Param("roleIds") List<Long> roleIds);
    
    /**
     * 删除角色指定菜单
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID数组
     * @return 结果
     */
    int deleteByRoleIdAndMenuIds(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
    
    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int selectCountByMenuId(Long menuId);
} 