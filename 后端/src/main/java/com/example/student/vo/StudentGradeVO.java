package com.example.student.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生成绩视图对象
 *
 * @author example
 */
@Data
public class StudentGradeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选课ID
     */
    private Long selectionId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 性别（0:男 1:女）
     */
    private Integer gender;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 专业ID
     */
    private Long majorId;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程编码
     */
    private String courseCode;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型（0:必修 1:选修）
     */
    private Integer courseType;

    /**
     * 课程类型描述
     */
    private String courseTypeDesc;

    /**
     * 学分
     */
    private BigDecimal credit;

    /**
     * 学期
     */
    private String semester;

    /**
     * 成绩
     */
    private BigDecimal score;

    /**
     * 状态（0:已选 1:已退 2:已修完）
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 选课时间
     */
    private LocalDateTime selectionTime;

    /**
     * 是否通过（1:通过 0:未通过）
     */
    private Integer isPassed;

    /**
     * 是否通过描述
     */
    private String isPassedDesc;
    
    /**
     * 绩点(GPA)
     */
    private BigDecimal gpa;
} 