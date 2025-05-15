package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student.entity.Attendance;
import com.example.student.entity.Class;
import com.example.student.entity.Course;
import com.example.student.vo.AttendanceStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 考勤数据访问接口
 *
 * @author example
 */
@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {

    /**
     * 查询课程开设的考勤记录
     *
     * @param courseOfferingId 课程开设ID
     * @param attendanceDate 考勤日期
     * @return 考勤记录列表
     */
    List<Attendance> selectAttendanceByCourseOffering(@Param("courseOfferingId") Long courseOfferingId,
                                                     @Param("attendanceDate") LocalDate attendanceDate);

    /**
     * 查询学生的考勤统计
     *
     * @param studentId 学生ID
     * @param courseOfferingId 课程开设ID
     * @return 考勤统计
     */
    Map<String, Object> selectAttendanceStats(@Param("studentId") Long studentId,
                                            @Param("courseOfferingId") Long courseOfferingId);

    /**
     * 根据课程ID列表批量查询课程信息
     *
     * @param courseIds 课程ID列表
     * @return 课程列表
     */
    List<Course> selectCoursesByIds(@Param("courseIds") Set<Long> courseIds);
    
    /**
     * 按课程统计考勤数据
     *
     * @param classId 班级ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 考勤统计列表
     */
    List<AttendanceStatisticsVO> getAttendanceStatisticsByCourse(
            @Param("classId") Long classId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 按班级统计考勤数据
     *
     * @param courseId 课程ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 考勤统计列表
     */
    List<AttendanceStatisticsVO> getAttendanceStatisticsByClass(
            @Param("courseId") Long courseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 按日期统计考勤数据
     *
     * @param courseId 课程ID（可选）
     * @param classId 班级ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 考勤统计列表
     */
    List<AttendanceStatisticsVO> getAttendanceStatisticsByDate(
            @Param("courseId") Long courseId,
            @Param("classId") Long classId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 按状态统计考勤数据
     *
     * @param courseId 课程ID（可选）
     * @param classId 班级ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 考勤统计列表
     */
    List<AttendanceStatisticsVO> getAttendanceStatisticsByStatus(
            @Param("courseId") Long courseId,
            @Param("classId") Long classId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 获取指定课程的考勤统计信息
     *
     * @param courseId 课程ID
     * @return 考勤统计
     */
    AttendanceStatisticsVO getCourseAttendanceStatisticsById(@Param("courseId") Long courseId);
    
    /**
     * 获取指定班级的考勤统计信息
     *
     * @param classId 班级ID
     * @return 考勤统计
     */
    @Select("SELECT " +
            "  'class' as type, " +
            "  c.id as class_id, " +
            "  c.name as class_name, " +
            "  COUNT(DISTINCT a.student_id) as student_count, " +
            "  SUM(CASE WHEN a.status = 0 THEN 1 ELSE 0 END) as present_count, " +
            "  SUM(CASE WHEN a.status = 1 THEN 1 ELSE 0 END) as absent_count, " +
            "  SUM(CASE WHEN a.status = 2 THEN 1 ELSE 0 END) as late_count, " +
            "  SUM(CASE WHEN a.status = 3 THEN 1 ELSE 0 END) as early_leave_count, " +
            "  SUM(CASE WHEN a.status = 4 THEN 1 ELSE 0 END) as leave_count " +
            "FROM attendance a " +
            "JOIN student s ON a.student_id = s.id " +
            "JOIN class c ON s.class_id = c.id " +
            "WHERE c.id = #{classId} " +
            "GROUP BY c.id, c.name")
    AttendanceStatisticsVO getClassAttendanceStatisticsById(@Param("classId") Long classId);
    
    /**
     * 根据班级ID列表批量查询班级信息
     *
     * @param classIds 班级ID列表
     * @return 班级列表
     */
    @Select("<script>" +
            "SELECT c.* FROM class c " +
            "WHERE c.id IN " +
            "<foreach collection='classIds' item='id' open='(' separator=',' close=')'>" +
            "  #{id}" +
            "</foreach>" +
            "</script>")
    List<Class> getClassesByIds(@Param("classIds") Set<Long> classIds);

    /**
     * 获取本周考勤统计数据
     *
     * @param params 包含startDate和endDate的参数Map
     * @return 包含总记录数和正常出勤数的统计数据
     */
    Map<String, Object> getWeeklyAttendanceStats(Map<String, Object> params);
} 