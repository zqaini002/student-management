package com.example.student.controller;

import com.example.student.common.Result;
import com.example.student.common.PageResult;
import com.example.student.dto.GradeQueryDTO;
import com.example.student.service.GradeService;
import com.example.student.vo.GradeStatisticsVO;
import com.example.student.vo.StudentGradeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 成绩管理控制器
 *
 * @author example
 */
@Tag(name = "成绩管理", description = "成绩管理相关接口")
@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
@Validated
public class GradeController {

    private final GradeService gradeService;

    /**
     * 获取成绩统计数据
     */
    @Operation(summary = "获取成绩统计数据")
    @GetMapping("/stats")
    @PreAuthorize("@auth.hasPermi('grade:stats')")
    public Result<GradeStatisticsVO> getGradeStats(GradeQueryDTO queryDTO) {
        return Result.success(gradeService.getGradeStatistics(queryDTO));
    }

    /**
     * 导出成绩统计报告
     */
    @Operation(summary = "导出成绩统计报告")
    @GetMapping("/export-report")
    @PreAuthorize("@auth.hasPermi('grade:export')")
    public void exportGradeReport(GradeQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        // 处理"all"参数，与getGradeStatistics方法保持一致
        if ("all".equals(queryDTO.getSemester())) {
            queryDTO.setSemester(null);
        }
        gradeService.exportGradeReport(queryDTO, response);
    }

    /**
     * 获取课程选项
     */
    @Operation(summary = "获取课程选项")
    @GetMapping("/course-options")
    @PreAuthorize("@auth.hasPermi('grade:query')")
    public Result<List<Map<String, Object>>> getCourseOptions() {
        return Result.success(gradeService.getCourseOptions());
    }

    /**
     * 获取班级选项
     */
    @Operation(summary = "获取班级选项")
    @GetMapping("/class-options")
    @PreAuthorize("@auth.hasPermi('grade:query')")
    public Result<List<Map<String, Object>>> getClassOptions() {
        return Result.success(gradeService.getClassOptions());
    }
    
    /**
     * 获取学期列表
     */
    @Operation(summary = "获取学期列表")
    @GetMapping("/semester-list")
    @PreAuthorize("@auth.hasPermi('grade:query')")
    public Result<List<String>> getSemesterList() {
        return Result.success(gradeService.getSemesterList());
    }
    
    /**
     * 获取学生成绩列表
     */
    @Operation(summary = "获取学生成绩列表")
    @GetMapping("/student-list")
    @PreAuthorize("@auth.hasPermi('grade:query')")
    public Result<PageResult<StudentGradeVO>> getStudentGradesList(
            @RequestParam Long courseId,
            @RequestParam String semester,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String studentName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(
            gradeService.getStudentGradesList(courseId, semester, classId, studentName, pageNum, pageSize)
        );
    }
    
    /**
     * 批量提交学生成绩
     */
    @Operation(summary = "批量提交学生成绩")
    @PostMapping("/submit-grades")
    @PreAuthorize("@auth.hasPermi('grade:input')")
    public Result<Boolean> batchSubmitGrades(@RequestBody List<Map<String, Object>> grades) {
        return Result.success(gradeService.batchSubmitGrades(grades));
    }
} 