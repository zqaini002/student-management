package com.example.student.service;

import com.example.student.common.PageResult;
import com.example.student.dto.TeacherDTO;
import com.example.student.dto.TeacherQueryDTO;
import com.example.student.vo.TeacherVO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 教师服务接口
 *
 * @author example
 */
public interface TeacherService {

    /**
     * 分页查询教师
     *
     * @param queryDTO 查询条件
     * @return 教师分页数据
     */
    PageResult<TeacherVO> pageTeacher(TeacherQueryDTO queryDTO);

    /**
     * 根据ID获取教师
     *
     * @param id 教师ID
     * @return 教师信息
     */
    TeacherVO getTeacherById(Long id);

    /**
     * 新增教师
     *
     * @param teacherDTO 教师信息
     * @return 是否成功
     */
    boolean addTeacher(TeacherDTO teacherDTO);

    /**
     * 更新教师
     *
     * @param teacherDTO 教师信息
     * @return 是否成功
     */
    boolean updateTeacher(TeacherDTO teacherDTO);

    /**
     * 删除教师
     *
     * @param id 教师ID
     * @return 是否成功
     */
    boolean deleteTeacher(Long id);

    /**
     * 重置密码
     *
     * @param id 教师ID
     * @return 是否成功
     */
    boolean resetPassword(Long id);

    /**
     * 更新教师状态
     *
     * @param id 教师ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 获取教师课程列表
     *
     * @param teacherId 教师ID
     * @param queryDTO 查询条件
     * @return 课程分页数据
     */
    PageResult<?> getTeacherCourses(Long teacherId, TeacherQueryDTO queryDTO);
    
    /**
     * 获取教师课程学生列表
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param queryDTO 查询条件
     * @return 学生分页数据
     */
    PageResult<?> getTeacherCourseStudents(Long teacherId, Long courseId, TeacherQueryDTO queryDTO);
    
    /**
     * 提交学生成绩
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param data 成绩数据
     * @return 是否成功
     */
    boolean submitStudentGrade(Long teacherId, Long courseId, Map<String, Object> data);
    
    /**
     * 提交学生考勤
     *
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param data 考勤数据
     * @return 是否成功
     */
    boolean submitStudentAttendance(Long teacherId, Long courseId, Map<String, Object> data);
    
    /**
     * 获取教师统计数据
     *
     * @param teacherId 教师ID
     * @return 统计数据
     */
    Map<String, Object> getTeacherStatistics(Long teacherId);
    
    /**
     * 导入教师数据
     *
     * @param file 教师数据文件
     * @return 导入结果
     * @throws IOException IO异常
     */
    Map<String, Object> importTeacher(MultipartFile file) throws IOException;
    
    /**
     * 导出教师数据
     *
     * @param queryDTO 查询条件
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportTeacher(TeacherQueryDTO queryDTO, HttpServletResponse response) throws IOException;
    
    /**
     * 下载教师导入模板
     *
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void downloadTeacherTemplate(HttpServletResponse response) throws IOException;
} 