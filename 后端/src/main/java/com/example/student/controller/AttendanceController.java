package com.example.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.CommonPage;
import com.example.student.common.CommonResult;
import com.example.student.entity.Attendance;
import com.example.student.service.AttendanceService;
import com.example.student.vo.AttendanceStatisticsVO;
import com.example.student.vo.AttendanceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 考勤管理控制器
 *
 * @author example
 */
@RestController
@RequestMapping("/attendance")
@Slf4j
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 查询考勤记录列表
     */
    @Operation(summary = "查询考勤记录列表")
    @PostMapping("/list")
    @PreAuthorize("@ss.hasPermission('attendance:record:list')")
    public CommonResult<CommonPage<AttendanceVO>> list(@RequestBody Map<String, Object> params) {
        log.info("查询考勤记录列表参数: {}", params);
        
        Integer pageNum = (Integer) params.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) params.getOrDefault("pageSize", 10);
        
        // 处理课程ID参数
        if (params.containsKey("courseId") && params.get("courseId") != null) {
            log.info("检测到课程ID参数: {}", params.get("courseId"));
        }
        
        Page<Attendance> page = new Page<>(pageNum, pageSize);
        Page<AttendanceVO> resultPage = attendanceService.listAttendanceRecords(page, params);
        
        return CommonResult.success(CommonPage.restPage(resultPage));
    }

    /**
     * 获取考勤统计数据
     */
    @PostMapping("/statistics")
    @PreAuthorize("@ss.hasPermission('attendance:stats:list')")
    public CommonResult<CommonPage<AttendanceStatisticsVO>> statistics(@RequestBody Map<String, Object> params) {
        Integer pageNum = (Integer) params.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) params.getOrDefault("pageSize", 10);
        
        Page<AttendanceStatisticsVO> page = new Page<>(pageNum, pageSize);
        Page<AttendanceStatisticsVO> resultPage = attendanceService.getAttendanceStatistics(page, params);
        
        return CommonResult.success(CommonPage.restPage(resultPage));
    }

    /**
     * 获取课程考勤统计
     */
    @GetMapping("/course/{courseId}/statistics")
    @PreAuthorize("@ss.hasPermission('attendance:stats:list')")
    public CommonResult<AttendanceStatisticsVO> getCourseStatistics(@PathVariable("courseId") Long courseId) {
        AttendanceStatisticsVO statistics = attendanceService.getCourseAttendanceStatistics(courseId);
        return CommonResult.success(statistics);
    }

    /**
     * 获取班级考勤统计
     */
    @GetMapping("/class/{classId}/statistics")
    @PreAuthorize("@ss.hasPermission('attendance:stats:list')")
    public CommonResult<AttendanceStatisticsVO> getClassStatistics(@PathVariable("classId") Long classId) {
        AttendanceStatisticsVO statistics = attendanceService.getClassAttendanceStatistics(classId);
        return CommonResult.success(statistics);
    }

    /**
     * 批量提交考勤记录
     */
    @PostMapping("/batch")
    @PreAuthorize("@ss.hasPermission('attendance:record:add') or @ss.hasPermission('attendance:record:edit') or @ss.hasPermission('attendance:record:remove')")
    public CommonResult<Boolean> batch(@RequestBody Map<String, Object> params) {
        // 处理删除操作
        if ("delete".equals(params.get("action"))) {
            Object ids = params.get("ids");
            if (ids instanceof List) {
                List<Long> idList = new ArrayList<>();
                for (Object id : (List<?>) ids) {
                    if (id instanceof Integer) {
                        idList.add(((Integer) id).longValue());
                    } else if (id instanceof Long) {
                        idList.add((Long) id);
                    }
                }
                boolean success = attendanceService.removeAttendanceByIds(idList);
                return CommonResult.success(success);
            }
            return CommonResult.failed("无效的ID列表");
        }
        
        // 处理单条记录添加/修改
        Attendance attendance = new Attendance();
        Object id = params.get("id");
        attendance.setId(id != null ? Long.valueOf(id.toString()) : null);
        attendance.setCourseOfferingId(Long.valueOf(params.get("courseOfferingId").toString()));
        attendance.setStudentId(Long.valueOf(params.get("studentId").toString()));
        attendance.setAttendanceDate(java.time.LocalDate.parse(params.get("attendanceDate").toString()));
        attendance.setStatus(Integer.valueOf(params.get("status").toString()));
        attendance.setRemark((String) params.get("remark"));
        
        boolean success = true;
        if (attendance.getId() != null) {
            // 更新
            success = attendanceService.updateById(attendance);
        } else {
            // 新增
            success = attendanceService.save(attendance);
        }
        return CommonResult.success(success);
    }

    /**
     * 导出考勤记录
     */
    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermission('attendance:record:export')")
    public void export(HttpServletResponse response, @RequestBody Map<String, Object> params) {
        List<AttendanceVO> list = attendanceService.exportAttendance(params);
        // 省略导出Excel的实现，实际项目中可以使用EasyExcel或POI
        // 这里只返回成功信息，实际应该将Excel文件输出到response
    }
} 