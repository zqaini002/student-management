package com.example.student.controller;

import com.example.student.common.PageResult;
import com.example.student.common.Result;
import com.example.student.dto.CourseDTO;
import com.example.student.dto.CourseQueryDTO;
import com.example.student.entity.Course;
import com.example.student.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 课程管理控制器
 */
@Tag(name = "课程管理接口")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CourseController {

    private final CourseService courseService;

    /**
     * 获取课程列表（分页）
     */
    @Operation(summary = "获取课程列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('course:course:list')")
    public Result<PageResult<Course>> list(CourseQueryDTO queryDTO) {
        PageResult<Course> pageResult = courseService.getCoursePage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取课程详情
     */
    @Operation(summary = "获取课程详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('course:course:query', 'course:my-courses:detail')")
    public Result<Course> getInfo(@PathVariable("id") @NotNull Long id) {
        log.info("获取课程详情, id: {}", id);
        Course course = courseService.getCourseById(id);
        return Result.success(course);
    }

    /**
     * 新增课程
     */
    @Operation(summary = "新增课程")
    @PostMapping
    @PreAuthorize("hasAuthority('course:course:add')")
    public Result<?> add(@RequestBody @Valid CourseDTO courseDTO) {
        courseService.addCourse(courseDTO);
        return Result.success();
    }

    /**
     * 修改课程
     */
    @Operation(summary = "修改课程")
    @PutMapping
    @PreAuthorize("hasAuthority('course:course:edit')")
    public Result<?> update(@RequestBody @Valid CourseDTO courseDTO) {
        courseService.updateCourse(courseDTO);
        return Result.success();
    }

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('course:course:delete')")
    public Result<?> delete(@PathVariable("id") @NotNull Long id) {
        courseService.deleteCourse(id);
        return Result.success();
    }

    /**
     * 导出课程数据
     */
    @Operation(summary = "导出课程数据")
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('course:course:export')")
    public void export(CourseQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        courseService.exportCourse(queryDTO, response);
    }

    /**
     * 导入课程数据
     */
    @Operation(summary = "导入课程数据")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('course:course:import')")
    public Result<?> importData(MultipartFile file) throws IOException {
        courseService.importCourse(file);
        return Result.success();
    }
    
    /**
     * 根据院系ID获取课程列表
     */
    @Operation(summary = "根据院系ID获取课程列表")
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAuthority('course:course:list')")
    public Result<List<Course>> getCoursesByDepartment(@PathVariable("departmentId") @NotNull Long departmentId) {
        List<Course> courses = courseService.getCoursesByDepartmentId(departmentId);
        return Result.success(courses);
    }
    
    /**
     * 获取课程统计数据
     */
    @Operation(summary = "获取课程统计数据")
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('course:course:query')")
    public Result<?> getCourseStats() {
        return Result.success(courseService.getCourseStats());
    }

    /**
     * 获取考勤记录模块可用的课程列表
     */
    @Operation(summary = "获取考勤记录模块可用的课程列表", description = "支持按课程名称模糊搜索，供考勤记录模块使用")
    @GetMapping("/listForAttendance")
    @PreAuthorize("hasAuthority('attendance:record:list')")
    public Result<PageResult<Course>> listForAttendance(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
            
        log.info("考勤记录模块查询课程列表，关键字: {}", keyword);
        
        // 使用专用的查询方法，提供更宽松的搜索条件
        PageResult<Course> pageResult = courseService.getCoursesForAttendance(keyword, pageNum, pageSize);
        
        log.info("考勤记录模块查询课程列表结果数量: {}", 
                pageResult != null && pageResult.getList() != null ? pageResult.getList().size() : 0);
        return Result.success(pageResult);
    }

    /**
     * 检查课程时间冲突
     *
     * @param params 包含两个课程时间的参数
     * @return 是否冲突
     */
    @Operation(summary = "检查课程时间冲突")
    @PostMapping("/check-conflict")
    @PreAuthorize("hasAnyAuthority('course:selection:select', 'course:selection:available', 'course:selection:selected')")
    public Result<Boolean> checkTimeConflict(@RequestBody Map<String, String> params) {
        String courseTime1 = params.get("courseTime1");
        String courseTime2 = params.get("courseTime2");
        
        log.info("检查课程时间冲突: courseTime1={}, courseTime2={}", courseTime1, courseTime2);
        
        // 如果任一时间为空，则认为不冲突
        if (courseTime1 == null || courseTime2 == null || 
            courseTime1.isEmpty() || courseTime2.isEmpty()) {
            return Result.success(false);
        }
        
        try {
            // 调用服务层方法检查冲突
            boolean hasConflict = courseService.checkTimeConflict(courseTime1, courseTime2);
            return Result.success(hasConflict);
        } catch (Exception e) {
            log.error("检查课程时间冲突出错", e);
            // 返回明确的错误信息给前端，而不是默认不冲突
            return Result.error("检查时间冲突失败: " + e.getMessage());
        }
    }
} 