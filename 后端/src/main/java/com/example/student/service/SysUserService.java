package com.example.student.service;

import com.example.student.common.PageResult;
import com.example.student.dto.SysUserDTO;
import com.example.student.dto.UpdatePasswordDTO;
import com.example.student.dto.UserQueryDTO;
import com.example.student.entity.SysUser;
import com.example.student.vo.SysUserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 系统用户服务接口
 *
 * @author example
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 用户分页数据
     */
    PageResult<SysUserVO> pageUser(UserQueryDTO queryDTO);

    /**
     * 通过ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUserVO getUserById(Long userId);

    /**
     * 新增用户
     *
     * @param userDTO 用户信息
     * @return 是否成功
     */
    boolean addUser(SysUserDTO userDTO);

    /**
     * 修改用户
     *
     * @param userDTO 用户信息
     * @return 是否成功
     */
    boolean updateUser(SysUserDTO userDTO);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean changeUserStatus(Long userId, Integer status);

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 是否成功
     */
    boolean resetUserPassword(Long userId, String password);

    /**
     * 修改用户密码
     *
     * @param userId      用户ID
     * @param passwordDTO 密码信息
     * @return 是否成功
     */
    boolean updateUserPassword(Long userId, UpdatePasswordDTO passwordDTO);

    /**
     * 更新用户个人信息
     *
     * @param userId  用户ID
     * @param userDTO 用户信息
     * @return 是否成功
     */
    boolean updateUserProfile(Long userId, SysUserDTO userDTO);

    /**
     * 上传用户头像
     *
     * @param userId 用户ID
     * @param file   头像文件
     * @return 头像URL
     */
    String uploadAvatar(Long userId, MultipartFile file);

    /**
     * 查询用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 设置用户的角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean setUserRoles(Long userId, List<Long> roleIds);

    /**
     * 导出用户数据
     *
     * @param queryDTO 查询条件
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportUser(UserQueryDTO queryDTO, HttpServletResponse response) throws IOException;

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    SysUser findByUsername(String username);

    /**
     * 判断用户是否为管理员
     *
     * @param user 用户对象
     * @return 是否为管理员
     */
    boolean isAdmin(SysUser user);
} 