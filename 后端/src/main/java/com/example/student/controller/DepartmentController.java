package com.example.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.Result;
import com.example.student.entity.Department;
import com.example.student.mapper.DepartmentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 院系控制器
 *
 * @author example
 */
@Tag(name = "院系管理", description = "院系管理相关接口")
@RestController
@RequestMapping("/education/department")
@Slf4j
public class DepartmentController {

    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 获取院系列表（分页）
     * 
     * @param current 当前页
     * @param size 每页数量
     * @param name 院系名称（可选）
     * @param code 院系编码（可选）
     * @return 分页院系列表
     */
    @Operation(summary = "获取院系列表", description = "分页获取院系列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('education:department:list')")
    public Result<Page<Department>> getDepartmentList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code) {
        log.info("查询院系列表，参数：current={}, size={}, name={}, code={}", current, size, name, code);
        try {
            Page<Department> page = new Page<>(current, size);
            LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
            
            // 添加查询条件（如果有）
            if (name != null && !name.isEmpty()) {
                queryWrapper.like(Department::getName, name);
            }
            if (code != null && !code.isEmpty()) {
                queryWrapper.like(Department::getCode, code);
            }
            
            // 排序
            queryWrapper.orderByDesc(Department::getUpdateTime);
            
            Page<Department> departmentPage = departmentMapper.selectPage(page, queryWrapper);
            log.info("查询到院系数量：{}", departmentPage.getTotal());
            return Result.success(departmentPage);
        } catch (Exception e) {
            log.error("查询院系列表失败", e);
            return Result.error("获取院系列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取院系详情
     *
     * @param id 院系ID
     * @return 院系详情
     */
    @Operation(summary = "获取院系详情", description = "根据ID获取院系详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('education:department:query') or hasAuthority('education:department:edit')")
    public Result<Department> getDepartmentById(@PathVariable @Parameter(description = "院系ID") Long id) {
        log.info("获取院系详情，ID：{}", id);
        try {
            Department department = departmentMapper.selectById(id);
            if (department == null) {
                log.warn("获取院系详情失败：院系不存在，ID：{}", id);
                    return Result.error("院系不存在");
            }
            return Result.success(department);
        } catch (Exception e) {
            log.error("获取院系详情失败", e);
            return Result.error("获取院系详情失败：" + e.getMessage());
        }
    }

    /**
     * 添加院系
     *
     * @param department 院系信息
     * @return 添加结果
     */
    @Operation(summary = "添加院系", description = "添加院系信息")
    @PostMapping
    @PreAuthorize("hasAuthority('education:department:add')")
    public Result<Void> addDepartment(@RequestBody Department department) {
        log.info("添加院系：{}", department);
        try {
            // 校验院系编码唯一性
            LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Department::getCode, department.getCode());
            if (departmentMapper.selectCount(queryWrapper) > 0) {
                log.warn("添加院系失败：院系编码已存在，code={}", department.getCode());
                return Result.error("院系编码已存在");
            }
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            department.setCreateTime(now);
            department.setUpdateTime(now);
            
            int rows = departmentMapper.insert(department);
            if (rows > 0) {
                log.info("添加院系成功，ID：{}", department.getId());
                return Result.success("添加院系成功");
            } else {
                log.warn("添加院系失败");
                return Result.error("添加院系失败");
            }
        } catch (Exception e) {
            log.error("添加院系失败", e);
            return Result.error("添加院系失败：" + e.getMessage());
        }
    }

    /**
     * 更新院系
     *
     * @param department 院系信息
     * @return 更新结果
     */
    @Operation(summary = "更新院系", description = "更新院系信息")
    @PutMapping
    @PreAuthorize("hasAuthority('education:department:edit')")
    public Result<Void> updateDepartment(@RequestBody Department department) {
        log.info("更新院系：{}", department);
        try {
            if (department.getId() == null) {
                log.warn("更新院系失败：ID不能为空");
                return Result.error("院系ID不能为空");
            }
            
            // 检查院系是否存在
            Department existingDepartment = departmentMapper.selectById(department.getId());
            if (existingDepartment == null) {
                log.warn("更新院系失败：院系不存在，ID：{}", department.getId());
                return Result.error("院系不存在");
            }
            
            // 校验院系编码唯一性（排除自身）
            if (!existingDepartment.getCode().equals(department.getCode())) {
                LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Department::getCode, department.getCode());
                if (departmentMapper.selectCount(queryWrapper) > 0) {
                    log.warn("更新院系失败：院系编码已存在，code={}", department.getCode());
                    return Result.error("院系编码已存在");
                }
            }
            
            // 设置更新时间
            department.setUpdateTime(LocalDateTime.now());
            // 保持创建时间不变
            department.setCreateTime(existingDepartment.getCreateTime());
            
            int rows = departmentMapper.updateById(department);
            if (rows > 0) {
                log.info("更新院系成功，ID：{}", department.getId());
                return Result.success("更新院系成功");
            } else {
                log.warn("更新院系失败，ID：{}", department.getId());
                return Result.error("更新院系失败");
            }
        } catch (Exception e) {
            log.error("更新院系失败", e);
            return Result.error("更新院系失败：" + e.getMessage());
        }
    }

    /**
     * 删除院系
     *
     * @param id 院系ID
     * @return 删除结果
     */
    @Operation(summary = "删除院系", description = "根据ID删除院系")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('education:department:delete') or hasAuthority('education:department:remove')")
    public Result<Void> deleteDepartment(@PathVariable @Parameter(description = "院系ID") Long id) {
        log.info("删除院系，ID：{}", id);
        try {
            // 检查院系是否存在
            Department existingDepartment = departmentMapper.selectById(id);
            if (existingDepartment == null) {
                log.warn("删除院系失败：院系不存在，ID：{}", id);
                return Result.error("院系不存在");
            }
            
            // TODO: 检查是否有关联的专业或者教师，如果有，则不能删除
            
            int rows = departmentMapper.deleteById(id);
            if (rows > 0) {
                log.info("删除院系成功，ID：{}", id);
                return Result.success("删除院系成功");
            } else {
                log.warn("删除院系失败，ID：{}", id);
                return Result.error("删除院系失败");
            }
        } catch (Exception e) {
            log.error("删除院系失败", e);
            return Result.error("删除院系失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有院系（不分页）
     *
     * @return 所有院系列表
     */
    @Operation(summary = "获取所有院系", description = "获取所有院系列表（不分页）")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('education:department:list')")
    public Result<List<Department>> getAllDepartments() {
        log.info("获取所有院系");
        try {
            LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(Department::getCode);
            List<Department> departmentList = departmentMapper.selectList(queryWrapper);
            log.info("获取所有院系成功，数量：{}", departmentList.size());
            return Result.success(departmentList);
        } catch (Exception e) {
            log.error("获取所有院系失败", e);
            return Result.error("获取所有院系失败：" + e.getMessage());
        }
    }
} 