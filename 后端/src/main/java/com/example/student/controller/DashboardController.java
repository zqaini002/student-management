package com.example.student.controller;

import com.example.student.common.Result;
import com.example.student.entity.ChartData;
import com.example.student.entity.Notice;
import com.example.student.entity.StatisticsData;
import com.example.student.entity.TodoItem;
import com.example.student.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 仪表盘控制器
 */
@Tag(name = "仪表盘管理", description = "仪表盘管理相关接口")
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取统计数据
     */
    @Operation(summary = "获取统计数据", description = "获取统计数据")
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher', 'student')")
    public Result<StatisticsData> getStatistics() {
        logCurrentUserAuthorities("getStatistics");
        return Result.success(dashboardService.getStatisticsData());
    }

    /**
     * 获取待办事项
     */
    @Operation(summary = "获取待办事项", description = "获取待办事项")
    @GetMapping("/todo")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher', 'student')")
    public Result<List<TodoItem>> getTodoList() {
        logCurrentUserAuthorities("getTodoList");
        return Result.success(dashboardService.getTodoList());
    }

    /**
     * 获取系统公告
     */
    @Operation(summary = "获取系统公告", description = "获取系统公告")
    @GetMapping("/notice")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher', 'student')")
    public Result<List<Notice>> getNoticeList() {
        logCurrentUserAuthorities("getNoticeList");
        return Result.success(dashboardService.getNoticeList());
    }

    /**
     * 获取学生按学期分布数据
     * @param timeRange 时间范围，可选值：近半年, 近一年, 全部
     */
    @Operation(summary = "获取学生按学期分布数据", description = "获取学生按学期分布数据")
    @GetMapping("/student/semester")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher', 'student')")
    public Result<ChartData> getStudentSemesterData(@RequestParam(required = false, defaultValue = "近半年") String timeRange) {
        logCurrentUserAuthorities("getStudentSemesterData");
        log.info("获取学生按学期分布数据，时间范围: {}", timeRange);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("student"))) {
            // 学生用户请求时，返回学生个人的成绩分布数据
            log.info("学生用户访问学期分布数据，返回成绩分布数据");
            return Result.success(dashboardService.getStudentGradeDistribution());
        }
        
        return Result.success(dashboardService.getStudentSemesterData(timeRange));
    }

    /**
     * 获取院系分布数据
     * @param year 年份，可选值：今年, 去年, 全部
     */
    @Operation(summary = "获取院系分布数据", description = "获取院系分布数据")
    @GetMapping("/department/distribution")
    @PreAuthorize("hasAnyAuthority('admin', 'teacher', 'student')")
    public Result<ChartData> getDepartmentDistribution(@RequestParam(required = false, defaultValue = "今年") String year) {
        logCurrentUserAuthorities("getDepartmentDistribution");
        log.info("获取院系分布数据，年份: {}", year);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("student"))) {
            // 学生用户请求时，返回学生个人的学习统计数据
            log.info("学生用户访问院系分布数据，返回个人学习统计数据");
            return Result.success(dashboardService.getStudentPersonalStats());
        }
        
        return Result.success(dashboardService.getDepartmentDistribution(year));
    }
    
    /**
     * 获取学生个人学习统计数据
     */
    @Operation(summary = "获取学生个人学习统计数据", description = "获取学生个人学习统计数据")
    @GetMapping("/student/personal/stats")
    @PreAuthorize("hasAuthority('student')")
    public Result<ChartData> getStudentPersonalStats() {
        logCurrentUserAuthorities("getStudentPersonalStats");
        log.info("获取学生个人学习统计数据");
        return Result.success(dashboardService.getStudentPersonalStats());
    }
    
    /**
     * 获取学生成绩分布数据
     */
    @Operation(summary = "获取学生成绩分布数据", description = "获取学生成绩分布数据")
    @GetMapping("/student/grade/distribution")
    @PreAuthorize("hasAuthority('student')")
    public Result<ChartData> getStudentGradeDistribution() {
        logCurrentUserAuthorities("getStudentGradeDistribution");
        log.info("获取学生成绩分布数据");
        return Result.success(dashboardService.getStudentGradeDistribution());
    }

    /**
     * 获取学生专用的院系分布数据
     */
    @Operation(summary = "获取学生专用的院系分布数据", description = "获取学生专用的院系分布数据")
    @GetMapping("/student/department/distribution")
    @PreAuthorize("hasAuthority('student')")
    public Result<ChartData> getStudentDepartmentDistribution() {
        logCurrentUserAuthorities("getStudentDepartmentDistribution");
        log.info("获取学生专用的院系分布数据");
        return Result.success(dashboardService.getStudentPersonalStats());
    }
    
    /**
     * 获取学生专用的学期数据
     */
    @Operation(summary = "获取学生专用的学期数据", description = "获取学生专用的学期数据")
    @GetMapping("/student/semester/data")
    @PreAuthorize("hasAuthority('student')")
    public Result<ChartData> getStudentSemesterData() {
        logCurrentUserAuthorities("getStudentSemesterData");
        log.info("获取学生专用的学期数据");
        return Result.success(dashboardService.getStudentGradeDistribution());
    }
    
    /**
     * 记录当前用户权限信息
     */
    private void logCurrentUserAuthorities(String methodName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("用户 {} 访问 {} 方法，拥有权限: {}", 
                    authentication.getName(), 
                    methodName,
                    authentication.getAuthorities());
        } else {
            log.warn("无认证信息访问 {} 方法", methodName);
        }
    }
} 