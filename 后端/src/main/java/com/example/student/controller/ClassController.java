package com.example.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.Result;
import com.example.student.entity.Class;
import com.example.student.entity.Teacher;
import com.example.student.mapper.ClassMapper;
import com.example.student.mapper.TeacherMapper;
import com.example.student.vo.ClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 班级控制器
 *
 * @author example
 */
@Tag(name = "班级管理", description = "班级管理相关接口")
@RestController
@RequestMapping("/student/class")
@Slf4j
public class ClassController {

    @Resource
    private ClassMapper classMapper;

    @Resource
    private TeacherMapper teacherMapper;

    /**
     * 获取班级列表（分页）
     * 
     * @param current 当前页
     * @param size 每页数量
     * @param name 班级名称（可选）
     * @param code 班级编码（可选）
     * @param majorId 专业ID（可选）
     * @param grade 年级（可选）
     * @return 分页班级列表
     */
    @Operation(summary = "获取班级列表", description = "分页获取班级列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('education:class:list')")
    public Result<Page<ClassVO>> getClassList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) String grade) {
        log.info("查询班级列表，参数：current={}, size={}, name={}, code={}, majorId={}, grade={}", 
                current, size, name, code, majorId, grade);
        try {
            Page<Class> page = new Page<>(current, size);
            LambdaQueryWrapper<Class> queryWrapper = new LambdaQueryWrapper<>();
            
            // 添加查询条件（如果有）
            if (name != null && !name.isEmpty()) {
                queryWrapper.like(Class::getName, name);
            }
            if (code != null && !code.isEmpty()) {
                queryWrapper.like(Class::getCode, code);
            }
            if (majorId != null) {
                queryWrapper.eq(Class::getMajorId, majorId);
            }
            if (grade != null && !grade.isEmpty()) {
                queryWrapper.eq(Class::getGrade, grade);
            }
            
            // 排序
            queryWrapper.orderByDesc(Class::getUpdateTime);
            
            Page<Class> classPage = classMapper.selectPage(page, queryWrapper);
            Page<ClassVO> voPage = convertToVoPage(classPage);
            
            log.info("查询到班级数量：{}", voPage.getTotal());
            return Result.success(voPage);
        } catch (Exception e) {
            log.error("查询班级列表失败", e);
            return Result.error("获取班级列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取班级详情
     *
     * @param id 班级ID
     * @return 班级详情
     */
    @Operation(summary = "获取班级详情", description = "根据ID获取班级详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('education:class:list')")
    public Result<ClassVO> getClassDetail(@PathVariable @Parameter(description = "班级ID") Long id) {
        log.info("查询班级详情，ID：{}", id);
        try {
            ClassVO classVO = classMapper.selectClassVOById(id);
            if (classVO == null) {
                log.warn("班级不存在，ID：{}", id);
                return Result.error("班级不存在");
            }
            log.info("查询班级详情成功，ID：{}", id);
            return Result.success(classVO);
        } catch (Exception e) {
            log.error("查询班级详情失败", e);
            return Result.error("获取班级详情失败：" + e.getMessage());
        }
    }

    /**
     * 添加班级
     *
     * @param classInfo 班级信息
     * @return 添加结果
     */
    @Operation(summary = "添加班级", description = "添加班级信息")
    @PostMapping
    @PreAuthorize("hasAuthority('education:class:add')")
    public Result<Void> addClass(@RequestBody Class classInfo) {
        log.info("添加班级：{}", classInfo);
        try {
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            classInfo.setCreateTime(now);
            classInfo.setUpdateTime(now);
            
            int rows = classMapper.insert(classInfo);
            if (rows > 0) {
                log.info("添加班级成功，ID：{}", classInfo.getId());
                return Result.success("添加班级成功");
            } else {
                log.warn("添加班级失败");
                return Result.error("添加班级失败");
            }
        } catch (Exception e) {
            log.error("添加班级失败", e);
            return Result.error("添加班级失败：" + e.getMessage());
        }
    }

    /**
     * 更新班级
     *
     * @param classInfo 班级信息
     * @return 更新结果
     */
    @Operation(summary = "更新班级", description = "更新班级信息")
    @PutMapping
    @PreAuthorize("hasAuthority('education:class:edit')")
    public Result<Void> updateClass(@RequestBody Class classInfo) {
        log.info("更新班级：{}", classInfo);
        try {
            if (classInfo.getId() == null) {
                log.warn("更新班级失败：ID不能为空");
                return Result.error("班级ID不能为空");
            }
            
            // 检查班级是否存在
            Class existingClass = classMapper.selectById(classInfo.getId());
            if (existingClass == null) {
                log.warn("更新班级失败：班级不存在，ID：{}", classInfo.getId());
                return Result.error("班级不存在");
            }
            
            // 设置更新时间
            classInfo.setUpdateTime(LocalDateTime.now());
            // 保持创建时间不变
            classInfo.setCreateTime(existingClass.getCreateTime());
            
            int rows = classMapper.updateById(classInfo);
            if (rows > 0) {
                log.info("更新班级成功，ID：{}", classInfo.getId());
                return Result.success("更新班级成功");
            } else {
                log.warn("更新班级失败，ID：{}", classInfo.getId());
                return Result.error("更新班级失败");
            }
        } catch (Exception e) {
            log.error("更新班级失败", e);
            return Result.error("更新班级失败：" + e.getMessage());
        }
    }

    /**
     * 删除班级
     *
     * @param id 班级ID
     * @return 删除结果
     */
    @Operation(summary = "删除班级", description = "根据ID删除班级")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('education:class:remove')")
    public Result<Void> deleteClass(@PathVariable @Parameter(description = "班级ID") Long id) {
        log.info("删除班级，ID：{}", id);
        try {
            // 检查班级是否存在
            Class existingClass = classMapper.selectById(id);
            if (existingClass == null) {
                log.warn("删除班级失败：班级不存在，ID：{}", id);
                return Result.error("班级不存在");
            }
            
            int rows = classMapper.deleteById(id);
            if (rows > 0) {
                log.info("删除班级成功，ID：{}", id);
                return Result.success("删除班级成功");
            } else {
                log.warn("删除班级失败，ID：{}", id);
                return Result.error("删除班级失败");
            }
        } catch (Exception e) {
            log.error("删除班级失败", e);
            return Result.error("删除班级失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有班级（不分页）
     *
     * @return 班级列表
     */
    @Operation(summary = "获取所有班级", description = "获取所有班级，不分页")
    @GetMapping("/all")
    public Result<List<ClassVO>> getAllClasses() {
        log.info("开始查询所有班级信息");
        try {
            List<ClassVO> classList = classMapper.selectClassVOList();
            log.info("查询到{}个班级信息", classList.size());
            return Result.success(classList);
        } catch (Exception e) {
            log.error("查询班级列表失败", e);
            return Result.error("获取班级列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 将Class实体分页结果转换为ClassVO分页结果
     */
    private Page<ClassVO> convertToVoPage(Page<Class> classPage) {
        Page<ClassVO> voPage = new Page<>(classPage.getCurrent(), classPage.getSize(), classPage.getTotal());
        List<ClassVO> voList = classMapper.selectClassVOListByIds(classPage.getRecords());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取所有教师（简化版，仅id和name）
     *
     * @return 教师列表
     */
    @Operation(summary = "获取所有教师", description = "获取所有教师信息，简化版")
    @GetMapping("/all/teachers")
    public Result<List<Map<String, Object>>> getAllTeachers() {
        log.info("开始查询所有教师基本信息");
        try {
            // 使用TeacherMapper获取教师信息
            List<Map<String, Object>> teachers = teacherMapper.selectTeacherOptions();
            
            log.info("查询到{}个教师信息", teachers.size());
            return Result.success(teachers);
        } catch (Exception e) {
            log.error("查询教师列表失败", e);
            return Result.error("获取教师列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据姓名搜索教师
     *
     * @param name 教师姓名
     * @return 教师列表
     */
    @Operation(summary = "根据姓名搜索教师", description = "根据姓名搜索教师信息")
    @GetMapping("/list/teachers")
    public Result<List<Map<String, Object>>> searchTeachers(@RequestParam(required = false) String name) {
        log.info("开始根据姓名搜索教师，name: {}", name);
        try {
            // 使用TeacherMapper搜索教师信息
            List<Map<String, Object>> teachers = teacherMapper.searchTeachersByName(name);
            
            log.info("搜索到{}个教师信息", teachers.size());
            return Result.success(teachers);
        } catch (Exception e) {
            log.error("搜索教师列表失败", e);
            return Result.error("搜索教师列表失败：" + e.getMessage());
        }
    }
} 