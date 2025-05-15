package com.example.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.PageResult;
import com.example.student.common.Result;
import com.example.student.dto.StudentDTO;
import com.example.student.dto.StudentQueryDTO;
import com.example.student.entity.Attendance;
import com.example.student.service.AttendanceService;
import com.example.student.service.StudentService;
import com.example.student.service.CourseSelectionService;
import com.example.student.vo.AttendanceVO;
import com.example.student.vo.StudentVO;
import com.example.student.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.CommonPage;
import com.example.student.common.CommonResult;

/**
 * 学生控制器
 *
 * @author example
 */
@Tag(name = "学生管理", description = "学生管理相关接口")
@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Resource
    private StudentService studentService;

    @Resource
    private AttendanceService attendanceService;
    
    @Resource
    private CourseSelectionService courseSelectionService;

    /**
     * 分页查询学生信息
     *
     * @param queryDTO 查询参数
     * @return 学生信息
     */
    @Operation(summary = "分页查询学生信息", description = "分页查询学生信息")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('education:student:list')")
    public Result<PageResult<StudentVO>> pageStudent(@Valid StudentQueryDTO queryDTO) {
        PageResult<StudentVO> pageResult = studentService.pageStudent(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询学生列表（用于考勤记录选择学生）
     * 返回简化的学生信息，适用于下拉选择
     *
     * @param keyword 搜索关键字（学号或姓名）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 学生信息列表
     */
    @Operation(summary = "查询学生列表（用于考勤记录选择）", description = "获取学生基础信息，适用于考勤记录模块")
    @GetMapping("/listForAttendance")
    @PreAuthorize("hasAuthority('attendance:record:list')")
    public Result<PageResult<StudentVO>> listForAttendance(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        log.info("考勤记录模块查询学生列表，关键字: {}", keyword);
        
        // 构建专用查询对象
        StudentQueryDTO queryDTO = new StudentQueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(Math.min(pageSize, 20)); // 限制每页最多20条记录
        queryDTO.setStatus(0); // 只查询在读学生
        
        if (keyword != null && !keyword.isEmpty()) {
            // 判断关键字是否像学号（包含数字）
            if (keyword.matches(".*\\d+.*")) {
                queryDTO.setStudentNo(keyword);
                log.info("按学号查询: {}", keyword);
            } else {
                queryDTO.setName(keyword);
                log.info("按姓名查询: {}", keyword);
            }
        }
        
        // 记录完整查询参数
        log.info("考勤模块学生查询参数: {}", queryDTO);
        
        PageResult<StudentVO> pageResult = studentService.pageStudent(queryDTO);
        
        log.info("考勤记录模块查询学生列表结果数量: {}", pageResult != null && pageResult.getList() != null ? pageResult.getList().size() : 0);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询学生信息
     *
     * @param id 学生ID
     * @return 学生信息
     */
    @Operation(summary = "根据ID查询学生信息", description = "根据ID查询学生信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('education:student:query')")
    public Result<StudentVO> getStudentById(@Parameter(description = "学生ID") @PathVariable Long id) {
        StudentVO student = studentService.getStudentById(id);
        return Result.success(student);
    }

    /**
     * 添加学生
     *
     * @param studentDTO 学生信息
     * @return 结果
     */
    @Operation(summary = "添加学生", description = "添加学生")
    @PostMapping
    @PreAuthorize("hasAuthority('education:student:add')")
    public Result<Void> addStudent(@Valid @RequestBody StudentDTO studentDTO) {
        boolean result = studentService.addStudent(studentDTO);
        return result ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 修改学生
     *
     * @param studentDTO 学生信息
     * @return 结果
     */
    @Operation(summary = "修改学生", description = "修改学生")
    @PutMapping
    @PreAuthorize("hasAuthority('education:student:edit')")
    public Result<Void> updateStudent(@Valid @RequestBody StudentDTO studentDTO) {
        boolean result = studentService.updateStudent(studentDTO);
        return result ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除学生
     *
     * @param id 学生ID
     * @return 结果
     */
    @Operation(summary = "删除学生", description = "删除学生")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('education:student:remove')")
    public Result<Void> deleteStudent(@Parameter(description = "学生ID") @PathVariable Long id) {
        boolean result = studentService.deleteStudent(id);
        return result ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    /**
     * 批量更新学生状态
     *
     * @param params 参数，包含学生ID列表和状态
     * @return 结果
     */
    @Operation(summary = "批量更新学生状态", description = "批量更新学生状态")
    @PutMapping("/batch/status")
    @PreAuthorize("hasAuthority('education:student:edit')")
    public Result<Void> updateBatchStudentStatus(@RequestBody Map<String, Object> params) {
        List<Long> ids = (List<Long>) params.get("ids");
        Integer status = (Integer) params.get("status");
        boolean result = studentService.updateBatchStudentStatus(ids, status);
        return result ? Result.success("批量更新状态成功") : Result.error("批量更新状态失败");
    }
    
    /**
     * 获取学生课程列表
     *
     * @param studentId 学生ID
     * @param current 当前页
     * @param size 每页数量
     * @param status 状态
     * @return 课程列表
     */
    @Operation(summary = "获取学生课程列表", description = "获取指定学生的课程列表")
    @GetMapping("/{studentId}/courses")
    @PreAuthorize("hasAnyAuthority('education:student:query', 'course:my-courses:list')")
    public Result<PageResult<?>> getStudentCourses(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        // 检查权限：如果是学生用户，只能查看自己的信息
        if (SecurityUtils.hasRole("student") && !studentService.isCurrentStudent(studentId)) {
            return Result.error("只能查询自己的课程信息");
        }
        
        PageResult<?> pageResult = studentService.getStudentCourses(studentId, current, size, status);
        return Result.success(pageResult);
    }
    
    /**
     * 学生查询自己的课程列表（学生专用接口）
     *
     * @param studentId 学生ID
     * @param pageNum 当前页
     * @param pageSize 每页数量
     * @param courseName 课程名称
     * @param courseCode 课程代码
     * @param semester 学期
     * @return 课程列表
     */
    @Operation(summary = "学生查询自己的课程列表", description = "学生查询自己的课程列表（学生专用接口）")
    @GetMapping("/{studentId}/my-courses")
    @PreAuthorize("hasAuthority('course:my-courses:list')")
    public Result<PageResult<?>> getStudentOwnCourses(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String semester) {
        
        // 直接使用当前登录用户查询课程，忽略URL中的studentId参数
        PageResult<?> pageResult = studentService.getCurrentUserCourses(pageNum, pageSize, courseName, courseCode, semester);
        return Result.success(pageResult);
    }
    
    /**
     * 导入学生信息
     *
     * @param file Excel文件
     * @return 导入结果
     * @throws Exception IO异常
     */
    @Operation(summary = "导入学生信息", description = "通过Excel导入学生信息")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('education:student:import')")
    public Result<Map<String, Object>> importStudent(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, Object> result = studentService.importStudent(file);
        return Result.success(result);
    }
    
    /**
     * 导出学生信息
     *
     * @param queryDTO 查询条件
     * @param response 响应对象
     * @throws Exception IO异常
     */
    @Operation(summary = "导出学生信息", description = "导出学生信息到Excel")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('education:student:export')")
    public void exportStudent(StudentQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        studentService.exportStudent(queryDTO, response);
    }
    
    /**
     * 获取学生成绩列表
     *
     * @param studentId 学生ID
     * @param current 当前页
     * @param size 每页数量
     * @param semester 学期
     * @return 成绩列表
     */
    @Operation(summary = "获取学生成绩列表", description = "获取指定学生的成绩列表")
    @GetMapping("/{studentId}/grades")
    @PreAuthorize("hasAnyAuthority('education:student:query', 'grade:my:list')")
    public Result<PageResult<?>> getStudentGrades(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String semester) {
        PageResult<?> pageResult = studentService.getStudentGrades(studentId, current, size, semester);
        return Result.success(pageResult);
    }
    
    /**
     * 获取学生考勤记录
     */
    @GetMapping("/{studentId}/attendance")
    @PreAuthorize("@ss.hasPermission('attendance:record:list')")
    public CommonResult<CommonPage<AttendanceVO>> getAttendance(
            @PathVariable("studentId") Long studentId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer status) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("courseName", courseName);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("status", status);
        
        // 检查当前登录用户是否就是查询的学生
        if (SecurityUtils.hasRole("student") && !studentService.isCurrentStudent(studentId)) {
            // 如果不是当前登录学生，则获取当前登录用户对应的学生ID
            Long currentUserId = SecurityUtils.getUserId();
            Long currentStudentId = studentService.getStudentIdByUserId(currentUserId);
            log.info("用户请求访问其他学生考勤，已自动调整为查询自己的考勤记录。请求ID: {}, 当前用户对应学生ID: {}", studentId, currentStudentId);
            studentId = currentStudentId;
        }
        
        log.info("获取学生考勤记录，学生ID: {}, 查询参数: {}", studentId, params);
        
        Page<Attendance> page = new Page<>(pageNum, pageSize);
        Page<AttendanceVO> resultPage = attendanceService.getStudentAttendance(studentId, page, params);
        
        return CommonResult.success(CommonPage.restPage(resultPage));
    }
    
    /**
     * 获取学生统计数据
     *
     * @return 统计数据
     */
    @Operation(summary = "获取学生统计数据", description = "获取学生各项统计数据")
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('education:student:list')")
    public Result<Map<String, Object>> getStudentStatistics() {
        Map<String, Object> statistics = studentService.getStudentStatistics();
        log.info("返回统计数据: {}", statistics);
        return Result.success(statistics);
    }
    
    /**
     * 获取统计数据详情（调试用）
     *
     * @return 统计数据详情
     */
    @Operation(summary = "获取统计数据详情", description = "用于调试，获取统计数据详情")
    @GetMapping("/statistics/debug")
    @PreAuthorize("hasAuthority('education:student:list')")
    public Result<Map<String, Object>> getStatisticsDebug() {
        Map<String, Object> statistics = studentService.getStudentStatistics();
        Map<String, Object> debug = new HashMap<>();
        debug.put("statistics", statistics);
        debug.put("timestamp", new Date());
        debug.put("statusDistribution", statistics.get("statusDistribution"));
        debug.put("departmentDistribution", statistics.get("departmentDistribution"));
        log.info("返回调试统计数据: {}", debug);
        return Result.success(debug);
    }
    
    /**
     * 导入学生信息模板下载
     *
     * @param response 响应对象
     * @throws Exception IO异常
     */
    @Operation(summary = "导入学生信息模板下载", description = "下载学生信息导入的Excel模板")
    @GetMapping("/template")
    @PreAuthorize("hasAuthority('education:student:import')")
    public void downloadStudentTemplate(HttpServletResponse response) throws Exception {
        studentService.downloadStudentTemplate(response);
    }

    /**
     * 获取学生可选课程列表
     *
     * @param studentId 学生ID
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param courseName 课程名称（可选）
     * @param courseType 课程类型（可选）
     * @param creditsRange 学分范围（可选）
     * @return 可选课程列表
     */
    @Operation(summary = "获取学生可选课程列表", description = "获取学生可选课程列表")
    @GetMapping("/{studentId}/available-courses")
    @PreAuthorize("hasAuthority('course:selection:available')")
    public Result<PageResult<?>> getAvailableCourses(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Integer courseType,
            @RequestParam(required = false) String creditsRange) {
        
        log.info("获取学生可选课程列表，学生ID: {}, 页码: {}, 每页大小: {}, 课程名称: {}, 课程类型: {}, 学分范围: {}", 
                studentId, pageNum, pageSize, courseName, courseType, creditsRange);
        
        // 检查当前用户是否有权限访问该学生的信息
        if (SecurityUtils.hasRole("student") && !studentService.isCurrentStudent(studentId)) {
            return Result.error("只能查询自己可选的课程");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        if (courseName != null) {
            params.put("courseName", courseName);
        }
        if (courseType != null) {
            params.put("courseType", courseType);
        }
        if (creditsRange != null) {
            params.put("creditsRange", creditsRange);
        }
        
        PageResult<?> pageResult = courseSelectionService.getAvailableCourses(studentId, params);
        return Result.success(pageResult);
    }

    /**
     * 获取当前登录学生的可选课程列表（学生专用接口）
     *
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param courseName 课程名称（可选）
     * @param courseType 课程类型（可选）
     * @param creditsRange 学分范围（可选）
     * @return 可选课程列表
     */
    @Operation(summary = "获取当前学生可选课程列表", description = "获取当前登录学生的可选课程列表（学生专用接口）")
    @GetMapping("/current/available-courses")
    @PreAuthorize("hasAuthority('course:selection:available')")
    public Result<PageResult<?>> getCurrentStudentAvailableCourses(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Integer courseType,
            @RequestParam(required = false) String creditsRange) {
        
        log.info("获取当前学生可选课程列表，页码: {}, 每页大小: {}, 课程名称: {}, 课程类型: {}, 学分范围: {}", 
                pageNum, pageSize, courseName, courseType, creditsRange);
        
        // 获取当前用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 获取学生ID
        Long studentId = studentService.getStudentIdByUserId(userId);
        if (studentId == null) {
            return Result.error("当前用户不是学生");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        if (courseName != null) {
            params.put("courseName", courseName);
        }
        if (courseType != null) {
            params.put("courseType", courseType);
        }
        if (creditsRange != null) {
            params.put("creditsRange", creditsRange);
        }
        
        PageResult<?> pageResult = courseSelectionService.getAvailableCourses(studentId, params);
        return Result.success(pageResult);
    }

    /**
     * 获取当前登录学生的已选课程列表（学生专用接口）
     *
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return 已选课程列表
     */
    @Operation(summary = "获取当前学生已选课程列表", description = "获取当前登录学生的已选课程列表（学生专用接口）")
    @GetMapping("/current/selected-courses")
    @PreAuthorize("hasAuthority('course:selection:selected')")
    public Result<PageResult<?>> getCurrentStudentSelectedCourses(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        log.info("获取当前学生已选课程列表，页码: {}, 每页大小: {}", pageNum, pageSize);
        
        // 获取当前用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 获取学生ID
        Long studentId = studentService.getStudentIdByUserId(userId);
        if (studentId == null) {
            return Result.error("当前用户不是学生");
        }
        
        PageResult<?> pageResult = studentService.getStudentCourses(studentId, pageNum, pageSize, null);
        return Result.success(pageResult);
    }

    /**
     * 获取选课设置
     *
     * @return 选课设置信息
     */
    @Operation(summary = "获取选课设置", description = "获取选课设置信息")
    @GetMapping("/course/selection/settings")
    @PreAuthorize("hasAnyAuthority('course:selection:available', 'course:selection:selected')")
    public Result<?> getSelectionSettings() {
        log.info("获取选课设置");
        return Result.success(courseSelectionService.getSelectionSettings());
    }

    /**
     * 学生选课
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 选课结果
     */
    @Operation(summary = "学生选课", description = "学生选课操作")
    @PostMapping("/{studentId}/select-course/{courseId}")
    @PreAuthorize("hasAuthority('course:selection:select')")
    public Result<?> selectCourse(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        log.info("学生选课操作，学生ID: {}, 课程ID: {}", studentId, courseId);
        
        // 检查当前用户是否有权限访问该学生的信息
        if (SecurityUtils.hasRole("student") && !studentService.isCurrentStudent(studentId)) {
            return Result.error("只能为自己选课");
        }
        
        boolean result = courseSelectionService.selectCourse(studentId, courseId);
        return Result.success(result);
    }

    /**
     * 当前学生选课（学生专用接口）
     * 
     * @param courseId 课程ID
     * @return 选课结果
     */
    @Operation(summary = "当前学生选课", description = "当前学生选课操作（学生专用接口）")
    @PostMapping("/current/select-course/{courseId}")
    @PreAuthorize("hasAuthority('course:selection:select')")
    public Result<?> currentStudentSelectCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        // 获取当前用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 获取学生ID
        Long studentId = studentService.getStudentIdByUserId(userId);
        if (studentId == null) {
            return Result.error("当前用户不是学生");
        }
        
        log.info("当前学生选课操作，学生ID: {}, 课程ID: {}", studentId, courseId);
        
        boolean result = courseSelectionService.selectCourse(studentId, courseId);
        return Result.success(result);
    }

    /**
     * 学生退选课程
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 退选结果
     */
    @Operation(summary = "学生退选课程", description = "学生退选课程操作")
    @DeleteMapping("/{studentId}/withdraw-course/{courseId}")
    @PreAuthorize("hasAuthority('course:selection:withdraw')")
    public Result<?> withdrawCourse(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        log.info("学生退选课程操作，学生ID: {}, 课程ID: {}", studentId, courseId);
        
        // 检查当前用户是否有权限访问该学生的信息
        if (SecurityUtils.hasRole("student") && !studentService.isCurrentStudent(studentId)) {
            return Result.error("只能为自己退选课程");
        }
        
        boolean result = courseSelectionService.withdrawCourse(studentId, courseId);
        return Result.success(result);
    }
    
    /**
     * 当前学生退选课程（学生专用接口）
     * 
     * @param courseId 课程ID
     * @return 退选结果
     */
    @Operation(summary = "当前学生退选课程", description = "当前学生退选课程操作（学生专用接口）")
    @DeleteMapping("/current/withdraw-course/{courseId}")
    @PreAuthorize("hasAuthority('course:selection:withdraw')")
    public Result<?> currentStudentWithdrawCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        // 获取当前用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 获取学生ID
        Long studentId = studentService.getStudentIdByUserId(userId);
        if (studentId == null) {
            return Result.error("当前用户不是学生");
        }
        
        log.info("当前学生退选课程操作，学生ID: {}, 课程ID: {}", studentId, courseId);
        
        boolean result = courseSelectionService.withdrawCourse(studentId, courseId);
        return Result.success(result);
    }
}