package com.example.student.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.dto.GradeQueryDTO;
import com.example.student.vo.GradeStatisticsVO;
import com.example.student.vo.StudentGradeVO;
import com.example.student.common.PageResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口
 *
 * @author example
 */
public interface GradeService {

    /**
     * 获取成绩统计数据
     *
     * @param queryDTO 查询参数
     * @return 成绩统计数据
     */
    GradeStatisticsVO getGradeStatistics(GradeQueryDTO queryDTO);

    /**
     * 导出成绩统计报告
     *
     * @param queryDTO 查询参数
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportGradeReport(GradeQueryDTO queryDTO, HttpServletResponse response) throws IOException;

    /**
     * 获取课程选项
     *
     * @return 课程选项列表
     */
    List<Map<String, Object>> getCourseOptions();

    /**
     * 获取班级选项
     *
     * @return 班级选项列表
     */
    List<Map<String, Object>> getClassOptions();
    
    /**
     * 获取课程学生列表(包含成绩)
     *
     * @param courseId 课程ID
     * @param semester 学期
     * @param classId 班级ID (可选)
     * @param studentName 学生姓名 (可选)
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 学生成绩列表
     */
    PageResult<StudentGradeVO> getStudentGradesList(Long courseId, String semester, Long classId, String studentName, Integer pageNum, Integer pageSize);
    
    /**
     * 批量提交学生成绩
     *
     * @param grades 学生成绩列表
     * @return 是否成功
     */
    boolean batchSubmitGrades(List<Map<String, Object>> grades);
    
    /**
     * 获取所有学期列表
     *
     * @return 学期列表
     */
    List<String> getSemesterList();
    
    /**
     * 获取学生成绩列表(按课程开设ID查询)
     *
     * @param page 分页参数
     * @param courseOfferingId 课程开设ID
     * @param semester 学期
     * @param classId 班级ID
     * @param keyword 关键词(学生姓名或学号)
     * @return 学生成绩列表
     */
    List<StudentGradeVO> getStudentGradeList(Page<StudentGradeVO> page, Long courseOfferingId, String semester, Long classId, String keyword);
    
    /**
     * 导入成绩数据
     *
     * @param file Excel文件
     * @return 导入结果
     * @throws Exception 异常
     */
    Map<String, Object> importGrade(MultipartFile file) throws Exception;
    
    /**
     * 导出成绩数据
     *
     * @param courseId 课程ID
     * @param semester 学期
     * @param classId 班级ID
     * @param studentName 学生姓名
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void exportGrade(Long courseId, String semester, Long classId, String studentName, HttpServletResponse response) throws IOException;
    
    /**
     * 下载成绩导入模板
     *
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void downloadGradeTemplate(HttpServletResponse response) throws IOException;
} 