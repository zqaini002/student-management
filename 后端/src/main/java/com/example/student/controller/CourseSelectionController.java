package com.example.student.controller;

import com.example.student.common.PageResult;
import com.example.student.common.Result;
import com.example.student.dto.CourseSelectionDTO;
import com.example.student.dto.CourseSelectionQueryDTO;
import com.example.student.service.CourseSelectionService;
import com.example.student.service.StudentService;
import com.example.student.util.SecurityUtils;
import com.example.student.vo.CourseSelectionStatisticsVO;
import com.example.student.vo.CourseTypeStatisticsVO;
import com.example.student.vo.SemesterStatisticsVO;
import com.example.student.vo.SemesterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 选课管理控制器
 * 处理学生选课相关操作
 */
@Tag(name = "选课管理接口")
@RestController
@RequestMapping("/selection/manage")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CourseSelectionController {

    private final CourseSelectionService courseSelectionService;
    private final StudentService studentService;

    /**
     * 获取学生已选课程
     *
     * @param studentId 学生ID
     * @param params 查询参数（包含分页信息、课程名称等筛选条件）
     * @return 已选课程列表
     */
    @Operation(summary = "获取学生已选课程")
    @GetMapping("/{studentId}/selected-courses")
    @PreAuthorize("hasAuthority('course:my-courses:list')")
    public Result<PageResult<?>> getStudentSelectedCourses(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestParam Map<String, Object> params) {
        log.info("获取学生已选课程, 学生ID: {}, 参数: {}", studentId, params);
        PageResult<?> pageResult = courseSelectionService.getStudentSelectedCourses(studentId, params);
        return Result.success(pageResult);
    }

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    @Operation(summary = "获取课程详情")
    @GetMapping("/detail/{courseId}")
    @PreAuthorize("hasAuthority('selection:detail')")
    public Result<?> getCourseDetail(@PathVariable Long courseId) {
        return Result.success(courseSelectionService.getCourseDetail(courseId));
    }

    /**
     * 获取课程教材信息
     *
     * @param courseId 课程ID
     * @return 教材信息列表
     */
    @Operation(summary = "获取课程教材信息")
    @GetMapping("/materials/{courseId}")
    @PreAuthorize("hasAuthority('selection:detail')")
    public Result<?> getCourseMaterials(@PathVariable Long courseId) {
        return Result.success(courseSelectionService.getCourseMaterials(courseId));
    }

    /**
     * 获取学生可选课程列表
     *
     * @param studentId 学生ID
     * @param params 查询参数
     * @return 可选课程列表
     */
    @Operation(summary = "获取学生可选课程列表")
    @GetMapping("/{studentId}/available-courses")
    @PreAuthorize("hasAuthority('course:selection:available')")
    public Result<PageResult<?>> getAvailableCourses(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestParam Map<String, Object> params) {
        log.info("获取学生可选课程, 学生ID: {}, 参数: {}", studentId, params);
        PageResult<?> pageResult = courseSelectionService.getAvailableCourses(studentId, params);
        return Result.success(pageResult);
    }

    /**
     * 学生选课
     *
     * @param courseOfferingId 课程开设ID
     * @param studentId 学生ID
     * @return 选课结果
     */
    @Operation(summary = "学生选课")
    @PostMapping("/select/{courseOfferingId}")
    @PreAuthorize("hasAuthority('selection:select')")
    public Result<?> selectCourse(
            @Parameter(description = "课程开设ID", required = true) @PathVariable Long courseOfferingId,
            @Parameter(description = "学生ID", required = false) @RequestParam(required = false) Long studentId) {
        
        if (studentId == null) {
            // 如果没有传递学生ID，则获取当前登录用户对应的学生ID
            Long userId = SecurityUtils.getUserId();
            studentId = studentService.getStudentIdByUserId(userId);
            if (studentId == null) {
                return Result.error("当前用户没有关联的学生账号");
            }
        }
        
        boolean result = courseSelectionService.selectCourse(studentId, courseOfferingId);
        return Result.success(result);
    }

    /**
     * 学生退选课程
     *
     * @param courseOfferingId 课程开设ID
     * @param studentId 学生ID
     * @return 退选结果
     */
    @Operation(summary = "学生退选课程")
    @PostMapping("/withdraw/{courseOfferingId}")
    @PreAuthorize("hasAuthority('selection:withdraw')")
    public Result<?> withdrawCourse(
            @Parameter(description = "课程开设ID", required = true) @PathVariable Long courseOfferingId,
            @Parameter(description = "学生ID", required = false) @RequestParam(required = false) Long studentId) {
        
        if (studentId == null) {
            // 如果没有传递学生ID，则获取当前登录用户对应的学生ID
            Long userId = SecurityUtils.getUserId();
            studentId = studentService.getStudentIdByUserId(userId);
            if (studentId == null) {
                return Result.error("当前用户没有关联的学生账号");
            }
        }
        
        boolean result = courseSelectionService.withdrawCourse(studentId, courseOfferingId);
        return Result.success(result);
    }

    /**
     * 获取选课设置
     *
     * @return 选课设置信息
     */
    @Operation(summary = "获取选课设置")
    @GetMapping("/settings")
    @PreAuthorize("hasAnyAuthority('course:selection:available', 'course:selection:selected')")
    public Result<?> getSelectionSettings() {
        return Result.success(courseSelectionService.getSelectionSettings());
    }

    /**
     * 检查课程时间冲突
     *
     * @param params 包含两个课程时间的参数
     * @return 是否冲突
     */
    @Operation(summary = "检查课程时间冲突")
    @PostMapping("/check-conflict")
    @PreAuthorize("hasAuthority('course:selection:select')")
    public Result<Boolean> checkTimeConflict(@RequestBody Map<String, String> params) {
        String courseTime1 = params.get("courseTime1");
        String courseTime2 = params.get("courseTime2");
        boolean hasConflict = courseSelectionService.checkTimeConflict(courseTime1, courseTime2);
        return Result.success(hasConflict);
    }

    /**
     * 查询选课列表
     */
    @Operation(summary = "查询选课列表")
    @GetMapping("/list")
    @PreAuthorize("@auth.hasPermi('course:selection:list')")
    public Result<PageResult<?>> list(CourseSelectionQueryDTO queryDTO) {
        return Result.success(courseSelectionService.getSelectionList(queryDTO));
    }

    /**
     * 获取选课详细信息
     */
    @Operation(summary = "获取选课详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("@auth.hasPermi('course:selection:query')")
    public Result<?> getInfo(@PathVariable("id") Long id) {
        return Result.success(courseSelectionService.getSelectionById(id));
    }

    /**
     * 修改选课信息
     */
    @Operation(summary = "修改选课信息")
    @PutMapping("")
    @PreAuthorize("@auth.hasPermi('course:selection:operate')")
    public Result<?> update(@RequestBody @Valid CourseSelectionDTO courseSelectionDTO) {
        return Result.success(courseSelectionService.updateSelection(courseSelectionDTO));
    }

    /**
     * 批量退课操作
     */
    @Operation(summary = "批量退课操作")
    @PutMapping("/batch-withdraw")
    @PreAuthorize("@auth.hasPermi('course:selection:operate')")
    public Result<?> batchWithdraw(@RequestBody List<Long> ids) {
        return Result.success(courseSelectionService.batchWithdrawCourses(ids));
    }

    /**
     * 退课操作（按选课ID）
     */
    @Operation(summary = "退课操作")
    @PutMapping("/withdraw/{id}")
    @PreAuthorize("@auth.hasPermi('course:selection:operate')")
    public Result<?> withdrawById(@PathVariable("id") Long id) {
        return Result.success(courseSelectionService.withdrawCourse(id));
    }

    /**
     * 导出选课数据
     */
    @Operation(summary = "导出选课数据")
    @GetMapping("/export")
    @PreAuthorize("@auth.hasPermi('course:selection:export')")
    public void export(CourseSelectionQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        courseSelectionService.exportSelection(queryDTO, response);
    }

    /**
     * 获取选课统计数据
     */
    @Operation(summary = "获取选课统计数据")
    @GetMapping("/statistics")
    @PreAuthorize("@auth.hasPermi('course:selection:list')")
    public Result<CourseSelectionStatisticsVO> getStatistics() {
        return Result.success(courseSelectionService.getSelectionStatistics());
    }

    /**
     * 获取课程类型选课比例数据
     */
    @Operation(summary = "获取课程类型选课比例数据")
    @GetMapping("/statistics/course-type")
    @PreAuthorize("@auth.hasPermi('course:selection:list')")
    public Result<List<CourseTypeStatisticsVO>> getCourseTypeStatistics() {
        return Result.success(courseSelectionService.getCourseTypeStatistics());
    }

    /**
     * 获取学期选课统计数据
     */
    @Operation(summary = "获取学期选课统计数据")
    @GetMapping("/statistics/semester")
    @PreAuthorize("@auth.hasPermi('course:selection:list')")
    public Result<List<SemesterStatisticsVO>> getSemesterStatistics() {
        return Result.success(courseSelectionService.getSemesterStatistics());
    }

    /**
     * 获取学期列表
     */
    @Operation(summary = "获取学期列表")
    @GetMapping("/semesters")
    @PreAuthorize("@auth.hasPermi('course:selection:list')")
    public Result<List<SemesterVO>> getSemesters() {
        return Result.success(courseSelectionService.getSemesters());
    }

    /**
     * 生成选课报表
     */
    @Operation(summary = "生成选课报表")
    @PostMapping("/report")
    @PreAuthorize("@auth.hasPermi('course:selection:export')")
    public Result<?> generateReport(@RequestBody Map<String, Object> params) {
        return Result.success(courseSelectionService.generateSelectionReport(params));
    }
} 