package com.example.student.service;

import com.example.student.common.PageResult;
import com.example.student.dto.StudentDTO;
import com.example.student.dto.StudentQueryDTO;
import com.example.student.vo.StudentVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 *
 * @author example
 */
public interface StudentService {

    /**
     * 分页查询学生信息
     *
     * @param queryDTO 查询参数
     * @return 学生信息
     */
    PageResult<StudentVO> pageStudent(StudentQueryDTO queryDTO);

    /**
     * 根据ID查询学生信息
     *
     * @param id 学生ID
     * @return 学生信息
     */
    StudentVO getStudentById(Long id);

    /**
     * 根据用户ID获取学生ID
     *
     * @param userId 用户ID
     * @return 学生ID
     */
    Long getStudentIdByUserId(Long userId);

    /**
     * 验证当前登录用户是否为指定的学生ID
     *
     * @param studentId 学生ID
     * @return 是否是当前学生
     */
    boolean isCurrentStudent(Long studentId);

    /**
     * 添加学生
     *
     * @param studentDTO 学生信息
     * @return 是否成功
     */
    boolean addStudent(StudentDTO studentDTO);

    /**
     * 修改学生
     *
     * @param studentDTO 学生信息
     * @return 是否成功
     */
    boolean updateStudent(StudentDTO studentDTO);

    /**
     * 删除学生
     *
     * @param id 学生ID
     * @return 是否成功
     */
    boolean deleteStudent(Long id);
    
    /**
     * 批量更新学生状态
     *
     * @param ids 学生ID列表
     * @param status 状态值
     * @return 是否成功
     */
    boolean updateBatchStudentStatus(List<Long> ids, Integer status);
    
    /**
     * 获取学生课程列表
     *
     * @param studentId 学生ID
     * @param current 当前页
     * @param size 每页数量
     * @param status 状态
     * @return 分页数据
     */
    PageResult<?> getStudentCourses(Long studentId, Integer current, Integer size, Integer status);
    
    /**
     * 导入学生信息
     *
     * @param file Excel文件
     * @return 导入结果
     * @throws Exception IO异常
     */
    Map<String, Object> importStudent(MultipartFile file) throws Exception;
    
    /**
     * 导出学生信息
     *
     * @param queryDTO 查询条件
     * @param response 响应对象
     * @throws Exception IO异常
     */
    void exportStudent(StudentQueryDTO queryDTO, HttpServletResponse response) throws Exception;
    
    /**
     * 获取学生成绩列表
     *
     * @param studentId 学生ID
     * @param current 当前页
     * @param size 每页数量
     * @param semester 学期
     * @return 分页数据
     */
    PageResult<?> getStudentGrades(Long studentId, Integer current, Integer size, String semester);
    
    /**
     * 获取学生考勤记录
     *
     * @param studentId 学生ID
     * @param current 当前页
     * @param size 每页数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 考勤状态
     * @return 分页数据
     */
    PageResult<?> getStudentAttendance(Long studentId, Integer current, Integer size, String startDate, String endDate, Integer status);
    
    /**
     * 获取学生统计数据
     *
     * @return 统计数据
     */
    Map<String, Object> getStudentStatistics();
    
    /**
     * 导入学生信息模板下载
     *
     * @param response 响应对象
     * @throws Exception IO异常
     */
    void downloadStudentTemplate(HttpServletResponse response) throws Exception;

    /**
     * 根据当前登录用户查询课程列表
     *
     * @param pageNum 当前页
     * @param pageSize 每页数量
     * @param courseName 课程名称
     * @param courseCode 课程编码
     * @param semester 学期
     * @return 课程列表分页结果
     */
    PageResult<?> getCurrentUserCourses(Integer pageNum, Integer pageSize, String courseName, String courseCode, String semester);
} 