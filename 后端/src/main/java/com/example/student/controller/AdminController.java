package com.example.student.controller;

import com.example.student.common.PageResult;
import com.example.student.common.Result;
import com.example.student.dto.AdminDTO;
import com.example.student.dto.AdminQueryDTO;
import com.example.student.service.AdminService;
import com.example.student.vo.AdminVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 *
 * @author example
 */
@Tag(name = "管理员管理", description = "管理员管理相关接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 分页查询管理员
     *
     * @param queryDTO 查询条件
     * @return 管理员分页数据
     */
    @Operation(summary = "分页查询管理员", description = "分页查询管理员接口")
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    @PostMapping("/list")
    public Result<PageResult<AdminVO>> list(@Valid @RequestBody AdminQueryDTO queryDTO) {
        PageResult<AdminVO> pageResult = adminService.pageAdmin(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取管理员详情
     *
     * @param id 管理员ID
     * @return 管理员信息
     */
    @Operation(summary = "获取管理员详情", description = "获取管理员详情接口")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    @GetMapping("/{id}")
    public Result<AdminVO> getInfo(@Parameter(description = "管理员ID") @PathVariable Long id) {
        AdminVO admin = adminService.getAdminById(id);
        return Result.success(admin);
    }

    /**
     * 新增管理员
     *
     * @param adminDTO 管理员信息
     * @return 结果
     */
    @Operation(summary = "新增管理员", description = "新增管理员接口")
    @PreAuthorize("@ss.hasPermission('system:user:add')")
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody AdminDTO adminDTO) {
        boolean result = adminService.addAdmin(adminDTO);
        return Result.success(result);
    }

    /**
     * 修改管理员
     *
     * @param adminDTO 管理员信息
     * @return 结果
     */
    @Operation(summary = "修改管理员", description = "修改管理员接口")
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody AdminDTO adminDTO) {
        boolean result = adminService.updateAdmin(adminDTO);
        return Result.success(result);
    }

    /**
     * 删除管理员
     *
     * @param id 管理员ID
     * @return 结果
     */
    @Operation(summary = "删除管理员", description = "删除管理员接口")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "管理员ID") @PathVariable Long id) {
        boolean result = adminService.deleteAdmin(id);
        return Result.success(result);
    }

    /**
     * 重置密码
     *
     * @param id 管理员ID
     * @return 结果
     */
    @Operation(summary = "重置密码", description = "重置密码接口")
    @PreAuthorize("@ss.hasPermission('system:user:resetPwd')")
    @PutMapping("/resetPassword/{id}")
    public Result<Boolean> resetPassword(@Parameter(description = "管理员ID") @PathVariable Long id) {
        boolean result = adminService.resetPassword(id);
        return Result.success(result);
    }

    /**
     * 更新状态
     *
     * @param id     管理员ID
     * @param status 状态
     * @return 结果
     */
    @Operation(summary = "更新状态", description = "更新状态接口")
    @PreAuthorize("@ss.hasPermission('system:user:edit')")
    @PutMapping("/status/{id}/{status}")
    public Result<Boolean> updateStatus(
            @Parameter(description = "管理员ID") @PathVariable Long id,
            @Parameter(description = "状态") @PathVariable Integer status) {
        boolean result = adminService.updateStatus(id, status);
        return Result.success(result);
    }
}