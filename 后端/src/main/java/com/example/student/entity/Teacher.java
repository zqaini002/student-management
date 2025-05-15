package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教师实体类
 */
@Data
@TableName("teacher")
public class Teacher {
    
    /**
     * 教师ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 教师编号
     */
    private String teacherNo;
    
    /**
     * 关联用户ID
     */
    private Long userId;
    
    /**
     * 所属院系ID
     */
    private Long departmentId;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 性别 0:男 1:女
     */
    private Integer gender;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    /**
     * 出生日期
     */
    private LocalDate birthday;
    
    /**
     * 入职日期
     */
    private LocalDate entryDate;
    
    /**
     * 状态 0:在职 1:离职 2:退休
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
    
    /**
     * 所属院系
     */
    @TableField(exist = false)
    private Department department;
    
    /**
     * 关联用户
     */
    @TableField(exist = false)
    private SysUser user;
} 
