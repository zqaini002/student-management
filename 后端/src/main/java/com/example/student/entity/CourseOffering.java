package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程开设实体类
 *
 * @author example
 */
@Data
@TableName("course_offering")
public class CourseOffering implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开课ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 学期
     */
    private String semester;

    /**
     * 上课时间
     */
    private String classTime;

    /**
     * 上课地点
     */
    private String location;

    /**
     * 容量
     */
    private Integer capacity;

    /**
     * 已选人数
     */
    private Integer selectedCount;

    /**
     * 状态 0:正常 1:已结课
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 
 
 