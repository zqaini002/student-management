package com.example.student.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * 统计数据实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsData {
    
    /**
     * 总学生数
     */
    private Integer totalStudents;
    
    /**
     * 总教师数
     */
    private Integer totalTeachers;
    
    /**
     * 总课程数
     */
    private Integer totalCourses;
    
    /**
     * 总院系数
     */
    private Integer totalDepartments;
    
    /**
     * 总专业数
     */
    private Integer totalMajors;
    
    /**
     * 总班级数
     */
    private Integer totalClasses;
    
    /**
     * 本周考勤率
     */
    private String weeklyAttendanceRate;
} 