package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.entity.CourseOffering;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 课程开设Mapper接口
 *
 * @author example
 */
@Repository
public interface CourseOfferingMapper extends BaseMapper<CourseOffering> {

    /**
     * 减少课程已选人数
     *
     * @param courseOfferingId 课程开设ID
     * @return 影响行数
     */
    @Update("UPDATE course_offering SET selected_count = selected_count - 1 WHERE id = #{courseOfferingId} AND selected_count > 0")
    int decrementSelectedCount(@Param("courseOfferingId") Long courseOfferingId);
    
    /**
     * 增加课程已选人数
     *
     * @param courseOfferingId 课程开设ID
     * @return 影响行数
     */
    @Update("UPDATE course_offering SET selected_count = selected_count + 1 WHERE id = #{courseOfferingId}")
    int incrementSelectedCount(@Param("courseOfferingId") Long courseOfferingId);
    
    /**
     * 批量减少课程已选人数
     *
     * @param courseOfferingId 课程开设ID
     * @param amount 减少数量
     * @return 影响行数
     */
    @Update("UPDATE course_offering SET selected_count = selected_count - #{amount} WHERE id = #{courseOfferingId} AND selected_count >= #{amount}")
    int decrementSelectedCountByAmount(@Param("courseOfferingId") Long courseOfferingId, @Param("amount") Integer amount);
    
    /**
     * 批量增加课程已选人数
     *
     * @param courseOfferingId 课程开设ID
     * @param amount 增加数量
     * @return 影响行数
     */
    @Update("UPDATE course_offering SET selected_count = selected_count + #{amount} WHERE id = #{courseOfferingId}")
    int incrementSelectedCountByAmount(@Param("courseOfferingId") Long courseOfferingId, @Param("amount") Integer amount);
    
    /**
     * 查询所有学期
     *
     * @return 学期列表
     */
    @Select("SELECT DISTINCT semester FROM course_offering ORDER BY semester DESC")
    List<String> selectAllSemesters();

    /**
     * 根据课程名称查询课程开设ID列表
     *
     * @param courseName 课程名称
     * @return 课程开设ID列表
     */
    @Select("SELECT co.id FROM course_offering co JOIN course c ON co.course_id = c.id WHERE c.name LIKE CONCAT('%', #{courseName}, '%')")
    List<Long> selectCourseOfferingsByCourseName(@Param("courseName") String courseName);
    
    /**
     * 根据课程开设ID查询班级ID
     *
     * @param courseOfferingId 课程开设ID
     * @return 班级ID
     */
    @Select("SELECT DISTINCT s.class_id FROM course_selection cs " +
            "JOIN student s ON cs.student_id = s.id " +
            "WHERE cs.course_offering_id = #{courseOfferingId} AND s.class_id IS NOT NULL LIMIT 1")
    Long getClassIdByCourseOfferingId(@Param("courseOfferingId") Long courseOfferingId);
    
    /**
     * 根据课程开设ID获取课程信息和班级信息
     *
     * @param courseOfferingId 课程开设ID
     * @return 包含课程和班级信息的Map
     */
    Map<String, Object> getCourseAndClassInfo(@Param("courseOfferingId") Long courseOfferingId);

    /**
     * 查询学生可选课程列表
     *
     * @param page 分页参数
     * @param studentId 学生ID
     * @param courseName 课程名称
     * @param courseType 课程类型
     * @param creditsRange 学分范围
     * @return 可选课程列表
     */
    IPage<Map<String, Object>> selectAvailableCourses(
            Page<?> page,
            @Param("studentId") Long studentId,
            @Param("courseName") String courseName,
            @Param("courseType") String courseType,
            @Param("creditsRange") String creditsRange);
} 