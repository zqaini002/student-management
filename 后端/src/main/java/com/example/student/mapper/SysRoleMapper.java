package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色Mapper接口
 *
 * @author example
 */
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {
    
    /**
     * 查询角色详情
     *
     * @param id 角色ID
     * @return 角色
     */
    SysRole selectRoleById(Long id);
    
    /**
     * 查询角色列表
     *
     * @param role 角色查询条件
     * @return 角色列表
     */
    List<SysRole> selectRoleList(SysRole role);
    
    /**
     * 分页查询角色列表
     *
     * @param page 分页参数
     * @param role 角色查询条件
     * @return 分页数据
     */
    IPage<SysRole> selectRolePage(Page<SysRole> page, @Param("role") SysRole role);
    
    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);
    
    /**
     * 校验角色名称是否唯一
     *
     * @param roleName 角色名称
     * @param roleId 角色ID
     * @return 数量
     */
    int checkRoleNameUnique(@Param("roleName") String roleName, @Param("roleId") Long roleId);
    
    /**
     * 校验角色权限是否唯一
     *
     * @param roleKey 角色权限
     * @param roleId 角色ID
     * @return 数量
     */
    int checkRoleKeyUnique(@Param("roleKey") String roleKey, @Param("roleId") Long roleId);
} 