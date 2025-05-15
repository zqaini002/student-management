package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 成绩数据库映射接口
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    /**
     * 查询学生成绩列表
     *
     * @param page 分页参数
     * @param studentId 学生ID
     * @param courseName 课程名称
     * @param semester 学期
     * @param status 状态
     * @return 成绩列表
     */
    IPage<Map<String, Object>> selectStudentGrades(
            Page<?> page,
            @Param("studentId") Long studentId,
            @Param("courseName") String courseName,
            @Param("semester") String semester,
            @Param("status") Integer status);

    /**
     * 查询学生所有成绩（不分页）
     *
     * @param studentId 学生ID
     * @return 所有成绩
     */
    List<Map<String, Object>> selectAllStudentGrades(@Param("studentId") Long studentId);

    /**
     * 查询学生成绩统计数据
     *
     * @param studentId 学生ID
     * @return 统计数据
     */
    Map<String, Object> selectStudentGradeStats(@Param("studentId") Long studentId);

    /**
     * 查询学生各学期成绩统计
     *
     * @param studentId 学生ID
     * @return 各学期统计
     */
    List<Map<String, Object>> selectStudentSemesterStats(@Param("studentId") Long studentId);

    /**
     * 查询学生成绩分布
     *
     * @param studentId 学生ID
     * @return 成绩分布
     */
    List<Map<String, Object>> selectStudentGradeDistribution(@Param("studentId") Long studentId);

    /**
     * 查询学生选课记录（包括未公布成绩的）
     *
     * @param page 分页参数
     * @param studentId 学生ID
     * @param courseName 课程名称（可选）
     * @param semester 学期（可选）
     * @param status 状态（可选）
     * @return 选课记录列表
     */
    IPage<Map<String, Object>> selectStudentCourseSelections(
            Page<Map<String, Object>> page,
            @Param("studentId") Long studentId,
            @Param("courseName") String courseName,
            @Param("semester") String semester,
            @Param("status") Integer status);
} 