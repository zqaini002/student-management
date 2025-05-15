package com.example.student.controller;

import com.example.student.common.PageResult;
import com.example.student.common.Result;
import com.example.student.util.SecurityUtils;
import com.example.student.dto.SysUserDTO;
import com.example.student.dto.UpdatePasswordDTO;
import com.example.student.dto.UserQueryDTO;
import com.example.student.service.SysUserService;
import com.example.student.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 系统用户控制器
 *
 * @author example
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 用户分页数据
     */
    @Operation(summary = "分页查询用户列表", description = "分页查询用户列表接口")
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    @GetMapping("/list")
    public Result<PageResult<SysUserVO>> list(UserQueryDTO queryDTO) {
        PageResult<SysUserVO> pageResult = sysUserService.pageUser(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Operation(summary = "获取用户详情", description = "获取用户详情接口")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @GetMapping("/{userId}")
    public Result<SysUserVO> getInfo(@Parameter(description = "用户ID") @PathVariable Long userId) {
        SysUserVO user = sysUserService.getUserById(userId);
        return Result.success(user);
    }

    /**
     * 新增用户
     *
     * @param userDTO 用户信息
     * @return 结果
     */
    @Operation(summary = "新增用户", description = "新增用户接口")
    @PreAuthorize("@ss.hasPermission('system:user:add')")
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody SysUserDTO userDTO) {
        boolean result = sysUserService.addUser(userDTO);
        return Result.success(result);
    }

    /**
     * 修改用户
     *
     * @param userDTO 用户信息
     * @return 结果
     */
    @Operation(summary = "修改用户", description = "修改用户接口")
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody SysUserDTO userDTO) {
        boolean result = sysUserService.updateUser(userDTO);
        return Result.success(result);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Operation(summary = "删除用户", description = "删除用户接口")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    @DeleteMapping("/{userId}")
    public Result<Boolean> delete(@Parameter(description = "用户ID") @PathVariable Long userId) {
        boolean result = sysUserService.deleteUser(userId);
        return Result.success(result);
    }

    /**
     * 修改用户状态
     *
     * @param userDTO 用户状态
     * @return 结果
     */
    @Operation(summary = "修改用户状态", description = "修改用户状态接口")
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @PutMapping("/status")
    public Result<Boolean> changeStatus(@RequestBody SysUserDTO userDTO) {
        boolean result = sysUserService.changeUserStatus(userDTO.getId(), userDTO.getStatus());
        return Result.success(result);
    }

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param userDTO 包含新密码的用户DTO
     * @return 结果
     */
    @Operation(summary = "重置用户密码", description = "重置用户密码接口")
    @PreAuthorize("@ss.hasPermission('system:user:resetPwd')")
    @PutMapping("/{userId}/password")
    public Result<Boolean> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestBody SysUserDTO userDTO) {
        boolean result = sysUserService.resetUserPassword(userId, userDTO.getPassword());
        return Result.success(result);
    }

    /**
     * 修改用户密码
     *
     * @param passwordDTO 密码信息
     * @return 结果
     */
    @Operation(summary = "修改用户密码", description = "修改用户密码接口")
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@Valid @RequestBody UpdatePasswordDTO passwordDTO) {
        Long userId = SecurityUtils.getUserId();
        boolean result = sysUserService.updateUserPassword(userId, passwordDTO);
        return Result.success(result);
    }

    /**
     * 更新用户个人信息
     *
     * @param userDTO 用户信息
     * @return 结果
     */
    @Operation(summary = "更新用户个人信息", description = "更新用户个人信息接口")
    @PutMapping("/profile")
    public Result<Boolean> updateProfile(@Valid @RequestBody SysUserDTO userDTO) {
        Long userId = SecurityUtils.getUserId();
        boolean result = sysUserService.updateUserProfile(userId, userDTO);
        return Result.success(result);
    }

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @Operation(summary = "上传用户头像", description = "上传用户头像接口")
    @PostMapping("/profile/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.getUserId();
        String avatarUrl = sysUserService.uploadAvatar(userId, file);
        return Result.success(avatarUrl);
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Operation(summary = "获取用户角色ID列表", description = "获取用户角色ID列表接口")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @GetMapping("/{userId}/roles")
    public Result<List<Long>> getUserRoles(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Long> roleIds = sysUserService.getUserRoleIds(userId);
        return Result.success(roleIds);
    }

    /**
     * 设置用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 结果
     */
    @Operation(summary = "设置用户角色", description = "设置用户角色接口")
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @PutMapping("/roles")
    public Result<Boolean> setUserRoles(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        @SuppressWarnings("unchecked")
        List<Long> roleIds = (List<Long>) params.get("roleIds");
        boolean result = sysUserService.setUserRoles(userId, roleIds);
        return Result.success(result);
    }

    /**
     * 导出用户数据
     *
     * @param queryDTO 查询条件
     * @param response HTTP响应
     */
    @Operation(summary = "导出用户数据", description = "导出用户数据接口")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @GetMapping("/export")
    public void export(UserQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        sysUserService.exportUser(queryDTO, response);
    }
} 