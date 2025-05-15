package com.example.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.entity.Attendance;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.student.vo.AttendanceStatisticsVO;
import com.example.student.vo.AttendanceVO;

import java.util.List;
import java.util.Map;

/**
 * 考勤服务接口
 *
 * @author example
 */
public interface AttendanceService extends IService<Attendance> {

    /**
     * 分页查询考勤记录
     *
     * @param page 分页参数
     * @param params 查询条件
     * @return 考勤记录
     */
    Page<AttendanceVO> listAttendanceRecords(Page<Attendance> page, Map<String, Object> params);

    /**
     * 获取学生考勤记录
     *
     * @param studentId 学生ID
     * @param page 分页参数
     * @param params 查询条件
     * @return 考勤记录
     */
    Page<AttendanceVO> getStudentAttendance(Long studentId, Page<Attendance> page, Map<String, Object> params);

    /**
     * 批量提交考勤记录
     *
     * @param attendances 考勤记录列表
     * @return 是否成功
     */
    boolean batchSubmitAttendance(List<Attendance> attendances);

    /**
     * 获取考勤统计数据
     *
     * @param page 分页参数
     * @param params 查询条件
     * @return 考勤统计数据
     */
    Page<AttendanceStatisticsVO> getAttendanceStatistics(Page<AttendanceStatisticsVO> page, Map<String, Object> params);

    /**
     * 获取课程考勤统计
     *
     * @param courseId 课程ID
     * @return 考勤统计数据
     */
    AttendanceStatisticsVO getCourseAttendanceStatistics(Long courseId);

    /**
     * 获取班级考勤统计
     *
     * @param classId 班级ID
     * @return 考勤统计数据
     */
    AttendanceStatisticsVO getClassAttendanceStatistics(Long classId);

    /**
     * 导出考勤数据
     *
     * @param params 查询条件
     * @return 考勤数据列表
     */
    List<AttendanceVO> exportAttendance(Map<String, Object> params);

    /**
     * 删除考勤记录
     *
     * @param ids ID列表
     * @return 是否成功
     */
    boolean removeAttendanceByIds(List<Long> ids);
} 