package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@Data
@TableName("grade")
public class Grade {

    /**
     * 成绩ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 分数
     */
    private Double score;

    /**
     * 成绩构成，如：平时成绩:30%:85,期中考试:30%:78,期末考试:40%:92
     */
    private String gradeComponents;

    /**
     * 状态：0-未评分，1-已评分，2-已作废
     */
    private Integer status;

    /**
     * 评分时间
     */
    private LocalDateTime gradeTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 