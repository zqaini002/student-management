package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.dto.CourseSelectionQueryDTO;
import com.example.student.entity.CourseSelection;
import com.example.student.vo.CourseSelectionManageVO;
import com.example.student.vo.CourseTypeStatisticsVO;
import com.example.student.vo.SemesterStatisticsVO;
import com.example.student.vo.SemesterVO;
import com.example.student.vo.StudentGradeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 选课数据库映射接口
 */
@Mapper
@Repository
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {

    /**
     * 查询选课分页列表
     *
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return 选课列表
     */
    IPage<CourseSelectionManageVO> selectSelectionPage(Page<?> page, @Param("query") CourseSelectionQueryDTO queryDTO);

    /**
     * 查询选课列表（不分页）
     *
     * @param queryDTO 查询条件
     * @return 选课列表
     */
    List<CourseSelectionManageVO> selectSelectionList(@Param("query") CourseSelectionQueryDTO queryDTO);

    /**
     * 查询选课详情
     *
     * @param id 选课ID
     * @return 选课详情
     */
    CourseSelectionManageVO selectSelectionById(@Param("id") Long id);

    /**
     * 查询学生已选课程
     *
     * @param page 分页参数
     * @param studentId 学生ID
     * @param courseName 课程名称
     * @param courseCode 课程代码
     * @param semester 学期
     * @return 已选课程
     */
    IPage<Map<String, Object>> selectStudentCourses(
            Page<?> page,
            @Param("studentId") Long studentId,
                                                         @Param("courseName") String courseName,
            @Param("courseCode") String courseCode,
            @Param("semester") String semester);

    /**
     * 统计课程已选人数
     *
     * @param courseOfferingId 课程开设ID
     * @return 已选人数
     */
    int countSelectedStudents(@Param("courseOfferingId") Long courseOfferingId);

    /**
     * 统计选课总数
     *
     * @return 选课总数
     */
    int countTotalSelections();

    /**
     * 统计指定状态的选课数量
     *
     * @param status 状态
     * @return 选课数量
     */
    int countSelectionsByStatus(@Param("status") int status);

    /**
     * 统计选课学生数量
     *
     * @return 学生数量
     */
    int countDistinctStudents();
    
    /**
     * 查询课程类型选课比例数据
     *
     * @return 课程类型选课比例
     */
    List<CourseTypeStatisticsVO> selectCourseTypeStatistics();
    
    /**
     * 查询学期选课统计数据
     *
     * @return 学期选课统计
     */
    List<SemesterStatisticsVO> selectSemesterStatistics();

    /**
     * 查询学期列表
     *
     * @return 学期列表
     */
    List<SemesterVO> selectSemesters();

    /**
     * 统计学生已选课程数量
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 已选课程数量
     */
    int countStudentSelectedCourses(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 统计学生已选学分总数
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 已选学分总数
     */
    double sumStudentSelectedCredits(@Param("studentId") Long studentId, @Param("semester") String semester);
    
    /**
     * 查询带成绩信息的学生列表
     *
     * @param page 分页参数
     * @param courseId 课程ID
     * @param keyword 搜索关键词
     * @param classId 班级ID
     * @param semester 学期
     * @param courseOfferingId 课程开设ID
     * @return 学生成绩列表
     */
    IPage<StudentGradeVO> selectStudentsWithGrades(
            Page<StudentGradeVO> page,
            @Param("courseId") Long courseId,
            @Param("keyword") String keyword,
            @Param("classId") Long classId,
            @Param("semester") String semester,
            @Param("courseOfferingId") Long courseOfferingId);

    /**
     * 查询成绩概览统计
     *
     * @param semester 学期
     * @param courseId 课程ID
     * @param classId 班级ID
     * @return 成绩概览统计数据
     */
    Map<String, Object> selectGradeOverviewStats(
            @Param("semester") String semester,
                                              @Param("courseId") Long courseId, 
                                              @Param("classId") Long classId);

    /**
     * 查询分数分布
     *
     * @param semester 学期
     * @param courseId 课程ID
     * @param classId 班级ID
     * @return 分数分布数据
     */
    List<Map<String, Object>> selectScoreDistribution(
            @Param("semester") String semester, 
                                             @Param("courseId") Long courseId, 
                                             @Param("classId") Long classId);

    /**
     * 查询班级成绩统计
     *
     * @param semester 学期
     * @param courseId 课程ID
     * @param classId 班级ID
     * @return 班级成绩统计数据
     */
    List<Map<String, Object>> selectClassGradeStats(
            @Param("semester") String semester, 
                                                 @Param("courseId") Long courseId, 
                                                 @Param("classId") Long classId);

    /**
     * 查询当前学期选课数量
     * 
     * @param semester 学期
     * @return 当前学期选课数量
     */
    int countCurrentSemesterSelections(@Param("semester") String semester);
    
    /**
     * 统计已修完课程数量
     * 
     * @return 已修完课程数量
     */
    int countCompletedSelections();

    /**
     * 获取学生课程统计信息
     *
     * @param studentId 学生ID
     * @return 课程统计信息
     */
    @Select("SELECT " +
            "COUNT(id) AS totalCourses, " +
            "SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS completedCourses, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS inProgressCourses, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS withdrawnCourses " +
            "FROM course_selection " +
            "WHERE student_id = #{studentId}")
    Map<String, Object> getStudentCourseStats(@Param("studentId") Long studentId);
    
    /**
     * 获取学生成绩分布
     *
     * @param studentId 学生ID
     * @return 成绩分布信息
     */
    @Select("SELECT " +
            "SUM(CASE WHEN score >= 90 THEN 1 ELSE 0 END) AS excellent, " +
            "SUM(CASE WHEN score >= 80 AND score < 90 THEN 1 ELSE 0 END) AS good, " +
            "SUM(CASE WHEN score >= 70 AND score < 80 THEN 1 ELSE 0 END) AS average, " +
            "SUM(CASE WHEN score >= 60 AND score < 70 THEN 1 ELSE 0 END) AS pass, " +
            "SUM(CASE WHEN score < 60 THEN 1 ELSE 0 END) AS fail, " +
            "ROUND(AVG(score), 2) AS averageScore " +
            "FROM course_selection " +
            "WHERE student_id = #{studentId} AND score IS NOT NULL")
    Map<String, Object> getStudentGradeDistribution(@Param("studentId") Long studentId);
}