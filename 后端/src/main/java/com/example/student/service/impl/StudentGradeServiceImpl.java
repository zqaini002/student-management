package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.PageResult;
import com.example.student.common.exception.BusinessException;
import com.example.student.entity.Student;
import com.example.student.mapper.GradeMapper;
import com.example.student.mapper.StudentMapper;
import com.example.student.service.StudentGradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 学生成绩服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentGradeServiceImpl implements StudentGradeService {

    private final GradeMapper gradeMapper;
    private final StudentMapper studentMapper;

    @Override
    public PageResult<Map<String, Object>> getStudentGrades(Long studentId, Map<String, Object> params) {
        // 获取学生成绩列表的实现
        // 1. 验证学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 2. 处理查询参数
        int pageNum = params.containsKey("pageNum") ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        String courseName = (String) params.get("courseName");
        String semester = (String) params.get("semester");
        Integer status = params.containsKey("status") ? Integer.parseInt(params.get("status").toString()) : null;
        
        // 3. 查询成绩数据
        IPage<Map<String, Object>> page = gradeMapper.selectStudentGrades(
                new Page<>(pageNum, pageSize),
                studentId,
                courseName,
                semester,
                status);
        
        // 4. 处理成绩数据，计算绩点和等级
        List<Map<String, Object>> records = page.getRecords();
        records.forEach(this::processGradeRecord);
        
        return new PageResult<>(records, page.getTotal());
    }

    @Override
    public Map<String, Object> getStudentGradeStats(Long studentId) {
        // 获取学生成绩统计数据的实现
        // 1. 验证学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 2. 统计数据初始化
        Map<String, Object> stats = new HashMap<>();
        
        // 3. 查询并计算统计数据
        Map<String, Object> dbStats = gradeMapper.selectStudentGradeStats(studentId);
        
        // 4. 设置统计数据
        stats.put("totalCourses", dbStats.get("totalCourses"));
        stats.put("totalCredits", dbStats.get("totalCredits"));
        stats.put("averageScore", dbStats.get("averageScore"));
        stats.put("gpa", calculateGPA(Double.parseDouble(dbStats.get("averageScore").toString())));
        
        // 5. 获取每学期成绩趋势数据
        List<Map<String, Object>> semesterStats = gradeMapper.selectStudentSemesterStats(studentId);
        stats.put("semesterStats", semesterStats);
        
        // 6. 获取成绩分布数据
        List<Map<String, Object>> gradeDistribution = gradeMapper.selectStudentGradeDistribution(studentId);
        stats.put("gradeDistribution", gradeDistribution);
        
        return stats;
    }

    @Override
    public Map<String, Object> getStudentInfo(Long studentId) {
        // 获取学生信息的实现
        // 1. 验证学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 2. 创建结果对象
        Map<String, Object> studentInfo = new HashMap<>();
        
        // 3. 设置学生基本信息
        studentInfo.put("studentId", student.getStudentNo());
        studentInfo.put("studentName", student.getName());
        studentInfo.put("className", student.getClassName());
        studentInfo.put("majorName", student.getMajorName());
        studentInfo.put("departmentName", student.getDepartmentName());
        studentInfo.put("admissionYear", student.getAdmissionYear());
        studentInfo.put("gender", student.getGender());
        studentInfo.put("idCard", student.getIdCard());
        studentInfo.put("phone", student.getPhone());
        studentInfo.put("email", student.getEmail());
        studentInfo.put("address", student.getAddress());
        
        return studentInfo;
    }

    @Override
    public Map<String, Object> getGradeReport(Long studentId) {
        // 获取打印成绩单所需数据的实现
        // 1. 获取学生信息
        Map<String, Object> studentInfo = getStudentInfo(studentId);
        
        // 2. 获取学生所有成绩（不分页）
        List<Map<String, Object>> gradeList = gradeMapper.selectAllStudentGrades(studentId);
        gradeList.forEach(this::processGradeRecord);
        
        // 3. 获取成绩统计数据
        Map<String, Object> gradeStats = getStudentGradeStats(studentId);
        
        // 4. 组装成绩单数据
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("studentInfo", studentInfo);
        reportData.put("gradeList", gradeList);
        reportData.put("gradeStats", gradeStats);
        reportData.put("printTime", new Date());
        
        return reportData;
    }
    
    /**
     * 处理成绩记录，计算绩点和等级
     * 
     * @param record 成绩记录
     */
    private void processGradeRecord(Map<String, Object> record) {
        // 处理成绩记录中的特殊字段
        // 1. 计算绩点
        if (record.containsKey("score") && record.get("score") != null) {
            double score = Double.parseDouble(record.get("score").toString());
            record.put("gp", calculateGP(score));
            record.put("level", getGradeLevel(score));
        } else {
            // 成绩为空时，设置默认值
            record.put("score", null);
            record.put("gp", "-");
            record.put("level", "未评分");
        }
        
        // 2. 处理成绩构成
        if (record.containsKey("gradeComponents") && record.get("gradeComponents") != null) {
            String gradeComponentsStr = record.get("gradeComponents").toString();
            record.put("gradeComponents", parseGradeComponents(gradeComponentsStr));
        }
        
        // 3. 处理状态显示值
        if (record.containsKey("status")) {
            Integer status = record.get("status") != null ? Integer.parseInt(record.get("status").toString()) : null;
            String statusText;
            if (status == null) {
                statusText = "未知";
            } else {
                switch (status) {
                    case 0:
                        statusText = "待评分";
                        break;
                    case 1:
                        statusText = "已评分";
                        break;
                    case 2:
                        statusText = "已取消";
                        break;
                    default:
                        statusText = "未知";
                        break;
                }
            }
            record.put("statusText", statusText);
        }
    }
    
    /**
     * 计算绩点
     * 
     * @param score 分数
     * @return 绩点
     */
    private String calculateGP(double score) {
        if (score >= 90) return "4.0";
        if (score >= 85) return "3.7";
        if (score >= 80) return "3.3";
        if (score >= 75) return "3.0";
        if (score >= 70) return "2.7";
        if (score >= 65) return "2.3";
        if (score >= 60) return "2.0";
        return "0.0";
    }
    
    /**
     * 计算GPA
     * 
     * @param averageScore 平均分
     * @return GPA
     */
    private double calculateGPA(double averageScore) {
        // 简单实现，根据平均分计算GPA
        if (averageScore >= 90) return 4.0;
        if (averageScore >= 85) return 3.7;
        if (averageScore >= 80) return 3.3;
        if (averageScore >= 75) return 3.0;
        if (averageScore >= 70) return 2.7;
        if (averageScore >= 65) return 2.3;
        if (averageScore >= 60) return 2.0;
        return 0.0;
    }
    
    /**
     * 获取成绩等级
     * 
     * @param score 分数
     * @return 等级
     */
    private String getGradeLevel(double score) {
        if (score >= 90) return "A";
        if (score >= 85) return "A-";
        if (score >= 80) return "B+";
        if (score >= 75) return "B";
        if (score >= 70) return "B-";
        if (score >= 65) return "C+";
        if (score >= 60) return "C";
        return "F";
    }
    
    /**
     * 解析成绩构成
     * 
     * @param gradeComponentsStr 成绩构成字符串
     * @return 成绩构成列表
     */
    private List<Map<String, Object>> parseGradeComponents(String gradeComponentsStr) {
        // 假设成绩构成字符串格式为：平时成绩:30%:85,期中考试:30%:78,期末考试:40%:92
        if (gradeComponentsStr == null || gradeComponentsStr.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Map<String, Object>> components = new ArrayList<>();
        String[] items = gradeComponentsStr.split(",");
        
        for (String item : items) {
            String[] parts = item.split(":");
            if (parts.length == 3) {
                Map<String, Object> component = new HashMap<>();
                component.put("name", parts[0]);
                component.put("weight", parts[1].replace("%", ""));
                component.put("score", parts[2]);
                components.add(component);
            }
        }
        
        return components;
    }

    @Override
    public PageResult<Map<String, Object>> getStudentCourseSelections(Long studentId, Map<String, Object> params) {
        // 获取学生选课记录的实现
        // 1. 验证学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 2. 处理查询参数
        int pageNum = params.containsKey("pageNum") ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        String courseName = (String) params.get("courseName");
        String semester = (String) params.get("semester");
        Integer status = params.containsKey("status") ? Integer.parseInt(params.get("status").toString()) : null;
        
        // 3. 查询选课数据，与成绩查询相比不过滤score为null的记录
        IPage<Map<String, Object>> page = gradeMapper.selectStudentCourseSelections(
                new Page<>(pageNum, pageSize),
                studentId,
                courseName,
                semester,
                status);
        
        // 4. 处理数据，计算绩点和等级
        List<Map<String, Object>> records = page.getRecords();
        records.forEach(this::processGradeRecord);
        
        return new PageResult<>(records, page.getTotal());
    }
} 