package com.example.student.dto;

import lombok.Data;

/**
 * 选课查询数据传输对象
 */
@Data
public class CourseSelectionQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 学生学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 学期
     */
    private String semester;

    /**
     * 选课状态
     */
    private Integer status;
} 