package com.example.student.service;

import com.example.student.common.PageResult;

import java.util.Map;

/**
 * 学生成绩服务接口
 */
public interface StudentGradeService {

    /**
     * 获取学生成绩列表
     *
     * @param studentId 学生ID
     * @param params 查询参数
     * @return 成绩列表
     */
    PageResult<Map<String, Object>> getStudentGrades(Long studentId, Map<String, Object> params);

    /**
     * 获取学生成绩统计数据
     *
     * @param studentId 学生ID
     * @return 成绩统计数据
     */
    Map<String, Object> getStudentGradeStats(Long studentId);

    /**
     * 获取学生信息
     *
     * @param studentId 学生ID
     * @return 学生信息
     */
    Map<String, Object> getStudentInfo(Long studentId);

    /**
     * 获取打印成绩单所需数据
     *
     * @param studentId 学生ID
     * @return 成绩单数据
     */
    Map<String, Object> getGradeReport(Long studentId);
    
    /**
     * 获取学生选课记录(包括未公布成绩的)
     *
     * @param studentId 学生ID
     * @param params 查询参数
     * @return 选课记录列表
     */
    PageResult<Map<String, Object>> getStudentCourseSelections(Long studentId, Map<String, Object> params);
} 