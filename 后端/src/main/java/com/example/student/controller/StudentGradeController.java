package com.example.student.controller;

import com.example.student.common.PageResult;
import com.example.student.common.Result;
import com.example.student.service.StudentGradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学生成绩控制器
 * 处理学生查询自己成绩的相关操作
 */
@Tag(name = "学生成绩查询接口")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Validated
@Slf4j
public class StudentGradeController {

    private final StudentGradeService studentGradeService;

    /**
     * 获取学生成绩列表
     *
     * @param studentId 学生ID
     * @param params 查询参数（包含分页信息、学期、课程名称等筛选条件）
     * @return 成绩列表
     */
    @Operation(summary = "获取学生成绩列表")
    @GetMapping("/{studentId}/grade-list")
    @PreAuthorize("hasAuthority('grade:my:list')")
    public Result<PageResult<?>> getStudentGrades(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestParam Map<String, Object> params) {
        log.info("获取学生成绩列表, 学生ID: {}, 参数: {}", studentId, params);
        PageResult<?> pageResult = studentGradeService.getStudentGrades(studentId, params);
        return Result.success(pageResult);
    }

    /**
     * 获取学生成绩统计数据
     *
     * @param studentId 学生ID
     * @return 成绩统计数据
     */
    @Operation(summary = "获取学生成绩统计数据")
    @GetMapping("/{studentId}/grade-stats")
    @PreAuthorize("hasAuthority('grade:my:stats')")
    public Result<?> getStudentGradeStats(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        log.info("获取学生成绩统计数据, 学生ID: {}", studentId);
        Map<String, Object> stats = studentGradeService.getStudentGradeStats(studentId);
        return Result.success(stats);
    }

    /**
     * 获取学生信息
     *
     * @param studentId 学生ID
     * @return 学生信息
     */
    @Operation(summary = "获取学生信息")
    @GetMapping("/{studentId}/info")
    @PreAuthorize("hasAnyAuthority('grade:my:list', 'grade:my:stats', 'grade:my:print')")
    public Result<?> getStudentInfo(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        log.info("获取学生信息, 学生ID: {}", studentId);
        Map<String, Object> studentInfo = studentGradeService.getStudentInfo(studentId);
        return Result.success(studentInfo);
    }

    /**
     * 获取打印成绩单所需数据
     *
     * @param studentId 学生ID
     * @return 成绩单数据
     */
    @Operation(summary = "获取打印成绩单所需数据")
    @GetMapping("/{studentId}/grade-report")
    @PreAuthorize("hasAuthority('grade:my:print')")
    public Result<?> getGradeReport(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        log.info("获取打印成绩单所需数据, 学生ID: {}", studentId);
        Map<String, Object> reportData = studentGradeService.getGradeReport(studentId);
        return Result.success(reportData);
    }

    /**
     * 获取学生选课记录(包括未公布成绩的)
     *
     * @param studentId 学生ID
     * @param params 查询参数（包含分页信息、学期、课程名称等筛选条件）
     * @return 选课记录列表
     */
    @Operation(summary = "获取学生选课记录")
    @GetMapping("/{studentId}/course-selections")
    @PreAuthorize("hasAuthority('grade:my:list')")
    public Result<PageResult<?>> getStudentCourseSelections(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestParam Map<String, Object> params) {
        log.info("获取学生选课记录, 学生ID: {}, 参数: {}", studentId, params);
        PageResult<?> pageResult = studentGradeService.getStudentCourseSelections(studentId, params);
        return Result.success(pageResult);
    }
} 