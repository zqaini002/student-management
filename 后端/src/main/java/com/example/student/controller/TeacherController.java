package com.example.student.controller;

import com.example.student.common.PageResult;
import com.example.student.common.Result;
import com.example.student.dto.TeacherDTO;
import com.example.student.dto.TeacherQueryDTO;
import com.example.student.service.TeacherService;
import com.example.student.vo.TeacherVO;
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
import java.util.Map;

/**
 * 教师控制器
 *
 * @author example
 */
@Tag(name = "教师管理", description = "教师管理相关接口")
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    /**
     * 分页查询教师
     *
     * @param queryDTO 查询条件
     * @return 教师分页数据
     */
    @Operation(summary = "分页查询教师", description = "分页查询教师接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:list')")
    @PostMapping("/list")
    public Result<PageResult<TeacherVO>> list(@Valid @RequestBody TeacherQueryDTO queryDTO) {
        PageResult<TeacherVO> pageResult = teacherService.pageTeacher(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取教师详情
     *
     * @param id 教师ID
     * @return 教师信息
     */
    @Operation(summary = "获取教师详情", description = "获取教师详情接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:query')")
    @GetMapping("/{id}")
    public Result<TeacherVO> getInfo(@Parameter(description = "教师ID") @PathVariable Long id) {
        TeacherVO teacher = teacherService.getTeacherById(id);
        return Result.success(teacher);
    }

    /**
     * 新增教师
     *
     * @param teacherDTO 教师信息
     * @return 结果
     */
    @Operation(summary = "新增教师", description = "新增教师接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:add')")
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody TeacherDTO teacherDTO) {
        boolean result = teacherService.addTeacher(teacherDTO);
        return Result.success(result);
    }

    /**
     * 修改教师
     *
     * @param teacherDTO 教师信息
     * @return 结果
     */
    @Operation(summary = "修改教师", description = "修改教师接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:edit')")
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody TeacherDTO teacherDTO) {
        boolean result = teacherService.updateTeacher(teacherDTO);
        return Result.success(result);
    }

    /**
     * 删除教师
     *
     * @param id 教师ID
     * @return 结果
     */
    @Operation(summary = "删除教师", description = "删除教师接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:delete')")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "教师ID") @PathVariable Long id) {
        boolean result = teacherService.deleteTeacher(id);
        return Result.success(result);
    }

    /**
     * 重置密码
     *
     * @param id 教师ID
     * @return 结果
     */
    @Operation(summary = "重置密码", description = "重置密码接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:resetPwd')")
    @PutMapping("/resetPassword/{id}")
    public Result<Boolean> resetPassword(@Parameter(description = "教师ID") @PathVariable Long id) {
        boolean result = teacherService.resetPassword(id);
        return Result.success(result);
    }

    /**
     * 更新状态
     *
     * @param id     教师ID
     * @param status 状态
     * @return 结果
     */
    @Operation(summary = "更新状态", description = "更新状态接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:edit')")
    @PutMapping("/status/{id}/{status}")
    public Result<Boolean> updateStatus(
            @Parameter(description = "教师ID") @PathVariable Long id,
            @Parameter(description = "状态") @PathVariable Integer status) {
        boolean result = teacherService.updateStatus(id, status);
        return Result.success(result);
    }
    
    /**
     * 获取教师课程列表
     *
     * @param teacherId 教师ID
     * @param queryDTO  查询条件
     * @return 课程分页数据
     */
    @Operation(summary = "获取教师课程列表", description = "获取教师课程列表接口")
    @PreAuthorize("@ss.hasAnyRole('admin', 'teacher')")
    @PostMapping("/courses/{teacherId}")
    public Result<PageResult<?>> getCourses(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Valid @RequestBody TeacherQueryDTO queryDTO) {
        PageResult<?> pageResult = teacherService.getTeacherCourses(teacherId, queryDTO);
        return Result.success(pageResult);
    }
    
    /**
     * 获取教师课程学生列表
     *
     * @param teacherId 教师ID
     * @param courseId  课程ID
     * @param queryDTO  查询条件
     * @return 学生分页数据
     */
    @Operation(summary = "获取教师课程学生列表", description = "获取教师课程学生列表接口")
    @PreAuthorize("@ss.hasAnyRole('admin', 'teacher')")
    @PostMapping("/courses/{teacherId}/students/{courseId}")
    public Result<PageResult<?>> getCourseStudents(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Valid @RequestBody TeacherQueryDTO queryDTO) {
        PageResult<?> pageResult = teacherService.getTeacherCourseStudents(teacherId, courseId, queryDTO);
        return Result.success(pageResult);
    }
    
    /**
     * 获取课程学生列表（支持管理员访问）
     *
     * @param courseId  课程ID
     * @param queryDTO  查询条件，包含可选的teacherId
     * @return 学生分页数据
     */
    @Operation(summary = "获取课程学生列表", description = "获取课程学生列表接口，支持管理员直接访问")
    @PreAuthorize("@ss.hasAnyRole('admin', 'teacher')")
    @PostMapping("/courses/students/{courseId}")
    public Result<PageResult<?>> getCourseStudentsNew(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Valid @RequestBody TeacherQueryDTO queryDTO) {
        // 从查询DTO中获取teacherId
        Long teacherId = queryDTO.getTeacherId();
        // 调用相同的服务方法，处理逻辑不变
        PageResult<?> pageResult = teacherService.getTeacherCourseStudents(teacherId, courseId, queryDTO);
        return Result.success(pageResult);
    }
    
    /**
     * 提交学生成绩
     *
     * @param teacherId 教师ID
     * @param courseId  课程ID
     * @param data      成绩数据
     * @return 结果
     */
    @Operation(summary = "提交学生成绩", description = "提交学生成绩接口")
    @PreAuthorize("@ss.hasAnyRole('admin', 'teacher')")
    @PutMapping("/courses/{teacherId}/grade/{courseId}")
    public Result<Boolean> submitGrade(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @RequestBody Map<String, Object> data) {
        boolean result = teacherService.submitStudentGrade(teacherId, courseId, data);
        return Result.success(result);
    }
    
    /**
     * 提交学生考勤
     *
     * @param teacherId 教师ID
     * @param courseId  课程ID
     * @param data      考勤数据
     * @return 结果
     */
    @Operation(summary = "提交学生考勤", description = "提交学生考勤接口")
    @PreAuthorize("@ss.hasAnyRole('admin', 'teacher')")
    @PutMapping("/courses/{teacherId}/attendance/{courseId}")
    public Result<Boolean> submitAttendance(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @RequestBody Map<String, Object> data) {
        boolean result = teacherService.submitStudentAttendance(teacherId, courseId, data);
        return Result.success(result);
    }
    
    /**
     * 管理员提交学生考勤（无教师ID）
     *
     * @param courseId 课程ID
     * @param data 考勤数据
     * @return 操作结果
     */
    @Operation(summary = "管理员提交学生考勤", description = "管理员提交学生考勤接口")
    @PreAuthorize("@ss.hasAnyRole('admin')")
    @PutMapping("/courses/attendance/{courseId}")
    public Result<Boolean> submitAttendanceByAdmin(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @RequestBody Map<String, Object> data) {
        boolean result = teacherService.submitStudentAttendance(null, courseId, data);
        return Result.success(result);
    }
    
    /**
     * 获取教师统计数据
     *
     * @param teacherId 教师ID
     * @return 统计数据
     */
    @Operation(summary = "获取教师统计数据", description = "获取教师统计数据接口")
    @PreAuthorize("@ss.hasAnyRole('admin', 'teacher')")
    @GetMapping("/statistics/{teacherId}")
    public Result<Map<String, Object>> getStatistics(
            @Parameter(description = "教师ID") @PathVariable Long teacherId) {
        Map<String, Object> statistics = teacherService.getTeacherStatistics(teacherId);
        return Result.success(statistics);
    }
    
    /**
     * 导入教师数据
     *
     * @param file 教师数据文件
     * @return 导入结果
     * @throws IOException IO异常
     */
    @Operation(summary = "导入教师数据", description = "导入教师数据接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:import')")
    @PostMapping("/import")
    public Result<Map<String, Object>> importData(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = teacherService.importTeacher(file);
        return Result.success(result);
    }
    
    /**
     * 导出教师数据
     *
     * @param queryDTO 查询条件
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    @Operation(summary = "导出教师数据", description = "导出教师数据接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:export')")
    @PostMapping("/export")
    public void exportData(@Valid @RequestBody TeacherQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        teacherService.exportTeacher(queryDTO, response);
    }
    
    /**
     * 下载教师导入模板
     *
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    @Operation(summary = "下载教师导入模板", description = "下载教师导入模板接口")
    @PreAuthorize("@ss.hasPermission('education:teacher:import')")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        teacherService.downloadTeacherTemplate(response);
    }
} 