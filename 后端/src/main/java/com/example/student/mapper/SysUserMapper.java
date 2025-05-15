package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.dto.UserQueryDTO;
import com.example.student.entity.SysUser;
import com.example.student.vo.SysUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author example
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户ID查询权限集合
     *
     * @param userId 用户ID
     * @return 权限集合
     */
    List<String> selectPermsByUserId(Long userId);

    /**
     * 根据用户ID查询角色集合
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    List<String> selectRoleKeysByUserId(Long userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    SysUser selectUserByUsername(String username);
    
    /**
     * 分页查询用户列表
     *
     * @param page      分页参数
     * @param username  用户名
     * @param name      姓名
     * @param phone     手机号
     * @param status    状态
     * @param userType  用户类型
     * @return 用户分页数据
     */
    IPage<SysUserVO> selectUserList(Page<SysUserVO> page, 
                                    @Param("username") String username,
                                    @Param("name") String name,
                                    @Param("phone") String phone,
                                    @Param("status") Integer status,
                                    @Param("userType") Integer userType);
    
    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUserVO selectUserById(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);
    
    /**
     * 根据用户ID查询角色名称列表
     *
     * @param userId 用户ID
     * @return 角色名称列表
     */
    List<String> selectRoleNamesByUserId(Long userId);
} 