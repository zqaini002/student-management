package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.entity.Teacher;
import com.example.student.vo.TeacherCourseStudentVO;
import com.example.student.vo.TeacherCourseVO;
import com.example.student.vo.TeacherVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 教师Mapper接口
 *
 * @author example
 */
@Repository
public interface TeacherMapper extends BaseMapper<Teacher> {
    
    /**
     * 分页查询教师列表
     *
     * @param page 分页参数
     * @param teacherNo 教师编号
     * @param name 姓名
     * @param departmentId 院系ID
     * @param title 职称
     * @param status 状态
     * @param email 邮箱
     * @param phone 手机号
     * @param birthdayStart 出生日期开始
     * @param birthdayEnd 出生日期结束
     * @param entryDateStart 入职日期开始
     * @param entryDateEnd 入职日期结束
     * @return 分页结果
     */
    IPage<TeacherVO> selectTeacherPage(Page<TeacherVO> page,
                                     @Param("teacherNo") String teacherNo,
                                     @Param("name") String name,
                                     @Param("departmentId") Long departmentId,
                                     @Param("title") String title,
                                      @Param("status") Integer status,
                                      @Param("email") String email,
                                      @Param("phone") String phone,
                                      @Param("birthdayStart") String birthdayStart,
                                      @Param("birthdayEnd") String birthdayEnd,
                                      @Param("entryDateStart") String entryDateStart,
                                      @Param("entryDateEnd") String entryDateEnd);
    
    /**
     * 根据ID查询教师
     *
     * @param id 教师ID
     * @return 教师视图对象
     */
    TeacherVO selectTeacherById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询教师
     *
     * @param userId 用户ID
     * @return 教师对象
     */
    Teacher selectTeacherByUserId(@Param("userId") Long userId);
    
    /**
     * 分页查询教师课程列表
     *
     * @param page 分页参数
     * @param teacherId 教师ID
     * @param semester 学期
     * @param courseName 课程名称
     * @param status 状态
     * @return 分页结果
     */
    IPage<TeacherCourseVO> selectTeacherCoursePage(Page<TeacherCourseVO> page,
                                                 @Param("teacherId") Long teacherId,
                                                 @Param("semester") String semester,
                                                 @Param("courseName") String courseName,
                                                 @Param("status") Integer status);
    
    /**
     * 分页查询教师课程学生列表
     *
     * @param page 分页参数
     * @param courseId 课程ID
     * @param studentName 学生姓名
     * @param studentNo 学号
     * @param status 状态
     * @return 分页结果
     */
    IPage<TeacherCourseStudentVO> selectTeacherCourseStudentPage(Page<TeacherCourseStudentVO> page,
                                                               @Param("courseId") Long courseId,
                                                               @Param("studentName") String studentName,
                                                               @Param("studentNo") String studentNo,
                                                               @Param("status") Integer status);
    
    /**
     * 获取教师统计数据
     *
     * @param teacherId 教师ID
     * @return 统计数据
     */
    Map<String, Object> selectTeacherStatistics(@Param("teacherId") Long teacherId);
    
    /**
     * 检查教师是否有权限操作课程
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @return 课程数量
     */
    int checkTeacherCoursePermission(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    
    /**
     * 获取教师选项列表（ID和姓名）
     *
     * @return 教师选项列表
     */
    List<Map<String, Object>> selectTeacherOptions();
    
    /**
     * 根据姓名搜索教师
     *
     * @param name 教师姓名(可为null)
     * @return 教师列表
     */
    List<Map<String, Object>> searchTeachersByName(@Param("name") String name);
}