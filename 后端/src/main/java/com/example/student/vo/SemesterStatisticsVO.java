package com.example.student.vo;

import lombok.Data;

/**
 * 学期统计视图对象
 */
@Data
public class SemesterStatisticsVO {

    /**
     * 学期
     */
    private String semester;

    /**
     * 选课数量
     */
    private Integer count;
} 