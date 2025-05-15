package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.entity.Student;
import com.example.student.vo.StudentAttendanceVO;
import com.example.student.vo.StudentCourseVO;
import com.example.student.vo.StudentGradeVO;
import com.example.student.vo.StudentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 学生Mapper接口
 *
 * @author example
 */
@Repository
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生信息
     *
     * @param page          分页参数
     * @param studentNo     学号
     * @param name          姓名
     * @param classId       班级ID
     * @param gender        性别
     * @param majorId       专业ID
     * @param departmentId  院系ID
     * @param phone         手机号码
     * @param status        状态
     * @param startDate     入学开始日期
     * @param endDate       入学结束日期
     * @return 学生信息
     */
    IPage<StudentVO> selectStudentPage(Page<StudentVO> page,
                                      @Param("studentNo") String studentNo,
                                      @Param("name") String name,
                                      @Param("classId") Long classId,
                                      @Param("gender") Integer gender,
                                      @Param("majorId") Long majorId,
                                      @Param("departmentId") Long departmentId,
                                      @Param("phone") String phone,
                                      @Param("status") Integer status,
                                      @Param("startDate") String startDate,
                                      @Param("endDate") String endDate);

    /**
     * 根据ID查询学生信息
     *
     * @param id 学生ID
     * @return 学生信息
     */
    StudentVO selectStudentById(@Param("id") Long id);

    /**
     * 根据班级ID查询学生列表
     *
     * @param classId 班级ID
     * @return 学生列表
     */
    List<Student> selectStudentsByClassId(Long classId);
    
    /**
     * 查询院系学生分布统计
     *
     * @return 院系分布统计数据
     */
    @Select("SELECT d.name AS departmentName, COUNT(s.id) AS studentCount " +
            "FROM student s " +
            "JOIN class c ON s.class_id = c.id " +
            "JOIN major m ON c.major_id = m.id " +
            "JOIN department d ON m.department_id = d.id " +
            "GROUP BY d.id, d.name " +
            "ORDER BY studentCount DESC")
    List<Map<String, Object>> selectDepartmentDistribution();
    
    /**
     * 查询学生课程列表
     *
     * @param page      分页参数
     * @param studentId 学生ID
     * @param status    状态
     * @return 课程列表
     */
    IPage<StudentCourseVO> selectStudentCoursePage(Page<StudentCourseVO> page,
                                                  @Param("studentId") Long studentId,
                                                  @Param("status") Integer status);
    
    /**
     * 查询学生成绩列表
     *
     * @param page      分页参数
     * @param studentId 学生ID
     * @param semester  学期
     * @return 成绩列表
     */
    IPage<StudentGradeVO> selectStudentGradePage(Page<StudentGradeVO> page,
                                               @Param("studentId") Long studentId,
                                               @Param("semester") String semester);
    
    /**
     * 查询学生考勤记录
     *
     * @param page      分页参数
     * @param studentId 学生ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param status    考勤状态
     * @return 考勤记录
     */
    IPage<StudentAttendanceVO> selectStudentAttendancePage(Page<StudentAttendanceVO> page,
                                                         @Param("studentId") Long studentId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate,
                                                         @Param("status") Integer status);
    
    /**
     * 查询各班级学生人数统计
     *
     * @return 班级学生人数统计
     */
    @Select("SELECT c.name AS className, COUNT(s.id) AS studentCount " +
            "FROM student s " +
            "JOIN class c ON s.class_id = c.id " +
            "WHERE s.status = 0 " +
            "GROUP BY c.id, c.name " +
            "ORDER BY studentCount DESC")
    List<Map<String, Object>> selectClassDistribution();
    
    /**
     * 查询各年级学生人数统计
     *
     * @return 年级学生人数统计
     */
    @Select("SELECT c.grade AS grade, COUNT(s.id) AS studentCount " +
            "FROM student s " +
            "JOIN class c ON s.class_id = c.id " +
            "WHERE s.status = 0 " +
            "GROUP BY c.grade " +
            "ORDER BY c.grade")
    List<Map<String, Object>> selectGradeDistribution();
    
    /**
     * 查询学生性别分布统计
     *
     * @return 性别分布统计
     */
    @Select("SELECT " +
            "SUM(CASE WHEN s.gender = 0 THEN 1 ELSE 0 END) AS maleCount, " +
            "SUM(CASE WHEN s.gender = 1 THEN 1 ELSE 0 END) AS femaleCount " +
            "FROM student s")
    Map<String, Object> selectGenderDistribution();
    
    /**
     * 查询学生状态分布统计
     *
     * @return 状态分布统计
     */
    @Select("SELECT " +
            "SUM(CASE WHEN s.status = 0 THEN 1 ELSE 0 END) AS studyingCount, " +
            "SUM(CASE WHEN s.status = 1 THEN 1 ELSE 0 END) AS graduatedCount, " +
            "SUM(CASE WHEN s.status = 2 THEN 1 ELSE 0 END) AS suspendedCount, " +
            "SUM(CASE WHEN s.status = 3 THEN 1 ELSE 0 END) AS droppedCount " +
            "FROM student s")
    Map<String, Object> selectStatusDistribution();
    
    /**
     * 根据学号查询学生信息
     *
     * @param studentNo 学号
     * @return 学生信息
     */
    Student selectByStudentNo(@Param("studentNo") String studentNo);
    
    /**
     * 根据身份证号查询学生信息
     *
     * @param idCard 身份证号
     * @return 学生信息
     */
    Student selectByIdCard(@Param("idCard") String idCard);
    
    /**
     * 查询院系学生分布统计 (按年份)
     *
     * @param params 查询参数，包含 year (年份)
     * @return 院系分布统计数据
     */
    @Select({"<script>",
            "SELECT d.name AS departmentName, COUNT(s.id) AS studentCount ",
            "FROM student s ",
            "JOIN class c ON s.class_id = c.id ",
            "JOIN major m ON c.major_id = m.id ",
            "JOIN department d ON m.department_id = d.id ",
            "<where>",
            "  <if test='year != null'>",
            "    YEAR(s.create_time) = #{year}",
            "  </if>",
            "</where>",
            "GROUP BY d.id, d.name ",
            "ORDER BY studentCount DESC",
            "</script>"})
    List<Map<String, Object>> selectDepartmentDistributionByYear(Map<String, Object> params);

    /**
     * 通过用户ID查询学生ID
     *
     * @param userId 用户ID
     * @return 学生ID
     */
    @Select("SELECT id FROM student WHERE user_id = #{userId}")
    Long selectStudentIdByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询学生课程分页
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @param courseName 课程名称
     * @param courseCode 课程编码
     * @param semester 学期
     * @return 学生课程分页数据
     */
    IPage<StudentCourseVO> selectUserCourses(
        Page<StudentCourseVO> page,
        @Param("userId") Long userId,
        @Param("courseName") String courseName,
        @Param("courseCode") String courseCode,
        @Param("semester") String semester
    );
} 