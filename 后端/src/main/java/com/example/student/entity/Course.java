package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course {
    
    /**
     * 课程ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 课程代码
     */
    private String code;
    
    /**
     * 课程名称
     */
    private String name;
    
    /**
     * 课程类型: 0-必修，1-选修
     */
    private Integer type;
    
    /**
     * 学分
     */
    private BigDecimal credit;
    
    /**
     * 教学学时
     */
    private Integer hours;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 院系ID
     */
    private Long departmentId;
    
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
    
    /**
     * 教师ID
     */
    @TableField(exist = false)
    private Long teacherId;
    
    /**
     * 教师姓名
     */
    @TableField(exist = false)
    private String teacherName;
    
    /**
     * 教师联系方式
     */
    @TableField(exist = false)
    private String teacherContact;
    
    /**
     * 学期
     */
    @TableField(exist = false)
    private String semester;
    
    /**
     * 上课时间
     */
    @TableField(exist = false)
    private String classTime;
    
    /**
     * 上课地点
     */
    @TableField(exist = false)
    private String location;
    
    /**
     * 课程状态
     */
    @TableField(exist = false)
    private Integer status;
    
    /**
     * 状态描述
     */
    @TableField(exist = false)
    private String statusDesc;
    
    /**
     * 课程容量
     */
    @TableField(exist = false)
    private Integer capacity;
    
    /**
     * 已选人数
     */
    @TableField(exist = false)
    private Integer selectedCount;
    
    /**
     * 所属院系名称
     */
    @TableField(exist = false)
    private String departmentName;
    
    /**
     * 考核方式
     */
    @TableField(exist = false)
    private String assessmentMethod;
} 
