package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student.entity.Course;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 课程Mapper接口
 */
@Repository
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 根据开课院系ID查询课程列表
     *
     * @param departmentId 开课院系ID
     * @return 课程列表
     */
    List<Course> selectCoursesByDepartmentId(Long departmentId);
    
    /**
     * 查询各学期的课程数量统计（包括新开课程和结课课程）
     *
     * @return 学期课程数量统计数据
     */
    @Select("SELECT semester, " +
            "COUNT(id) AS totalCourses, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS finishedCourses " +
            "FROM course_offering " +
            "GROUP BY semester " +
            "ORDER BY semester")
    List<Map<String, Object>> selectCourseBySemester();

    /**
     * 按院系统计课程数量
     *
     * @return 各院系课程数量统计
     */
    @Select("SELECT d.id as departmentId, d.name as departmentName, " +
            "COUNT(c.id) as courseCount, " +
            "SUM(CASE WHEN c.type = 0 THEN 1 ELSE 0 END) as requiredCourseCount, " +
            "SUM(CASE WHEN c.type = 1 THEN 1 ELSE 0 END) as electiveCourseCount " +
            "FROM course c " +
            "LEFT JOIN department d ON c.department_id = d.id " +
            "GROUP BY d.id, d.name " +
            "ORDER BY courseCount DESC")
    List<Map<String, Object>> selectCourseCountByDepartment();

    /**
     * 查询课程选项列表
     *
     * @return 课程选项列表
     */
    List<Map<String, Object>> selectCourseOptions();

    /**
     * 查询考勤记录模块可用的课程列表
     * 
     * @param keyword 搜索关键词
     * @return 课程列表
     */
    List<Course> selectCoursesForAttendance(@Param("keyword") String keyword);

    /**
     * 按学分分布统计课程
     *
     * @return 学分分布统计数据
     */
    List<Map<String, Object>> selectCreditDistribution();

    /**
     * 查询各学期的课程数量统计（按时间范围筛选）
     *
     * @param params 查询参数，包含 startDate (开始日期)
     * @return 学期课程数量统计数据
     */
    @Select({"<script>",
            "SELECT semester, ",
            "COUNT(id) AS totalCourses, ",
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS finishedCourses ",
            "FROM course_offering ",
            "<where>",
            "  <if test='startDate != null'>",
            "    create_time >= #{startDate}",
            "  </if>",
            "</where>",
            "GROUP BY semester ",
            "ORDER BY semester",
            "</script>"})
    List<Map<String, Object>> selectCourseBySemesterWithTimeRange(Map<String, Object> params);

    /**
     * 查询课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    Map<String, Object> selectCourseDetailById(@Param("courseId") Long courseId);

    /**
     * 查询课程教材信息
     *
     * @param courseId 课程ID
     * @return 教材列表
     */
    List<Map<String, Object>> selectCourseMaterialsById(@Param("courseId") Long courseId);
} 
