package com.example.student.service;

import com.example.student.common.PageResult;
import com.example.student.dto.CourseDTO;
import com.example.student.dto.CourseQueryDTO;
import com.example.student.entity.Course;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 分页查询课程列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<Course> getCoursePage(CourseQueryDTO queryDTO);

    /**
     * 根据ID获取课程详情
     *
     * @param id 课程ID
     * @return 课程信息
     */
    Course getCourseById(Long id);

    /**
     * 新增课程
     *
     * @param courseDTO 课程信息
     */
    void addCourse(CourseDTO courseDTO);

    /**
     * 更新课程信息
     *
     * @param courseDTO 课程信息
     */
    void updateCourse(CourseDTO courseDTO);

    /**
     * 删除课程
     *
     * @param id 课程ID
     */
    void deleteCourse(Long id);

    /**
     * 导出课程数据
     *
     * @param queryDTO 查询条件
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportCourse(CourseQueryDTO queryDTO, HttpServletResponse response) throws IOException;

    /**
     * 导入课程数据
     *
     * @param file Excel文件
     * @throws IOException IO异常
     */
    void importCourse(MultipartFile file) throws IOException;

    /**
     * 根据院系ID获取课程列表
     *
     * @param departmentId 院系ID
     * @return 课程列表
     */
    List<Course> getCoursesByDepartmentId(Long departmentId);

    /**
     * 获取课程统计数据
     *
     * @return 统计数据
     */
    Map<String, Object> getCourseStats();

    /**
     * 获取考勤记录模块可用的课程列表
     *
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 课程分页数据
     */
    PageResult<Course> getCoursesForAttendance(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 检查课程时间冲突
     *
     * @param courseTime1 课程时间1
     * @param courseTime2 课程时间2
     * @return 是否冲突
     */
    boolean checkTimeConflict(String courseTime1, String courseTime2);
}