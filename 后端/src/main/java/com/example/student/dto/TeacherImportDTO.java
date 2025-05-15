package com.example.student.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 教师导入数据传输对象
 *
 * @author example
 */
@Data
public class TeacherImportDTO {
    
    /**
     * 教师编号
     */
    @ExcelProperty("教师编号")
    private String teacherNo;
    
    /**
     * 用户名
     */
    @ExcelProperty("用户名")
    private String username;
    
    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String name;
    
    /**
     * 性别
     */
    @ExcelProperty("性别")
    private String gender;
    
    /**
     * 院系ID
     */
    @ExcelProperty("院系ID")
    private Long departmentId;
    
    /**
     * 职称
     */
    @ExcelProperty("职称")
    private String title;
    
    /**
     * 身份证号
     */
    @ExcelProperty("身份证号")
    private String idCard;
    
    /**
     * 手机号
     */
    @ExcelProperty("手机号")
    private String phone;
    
    /**
     * 邮箱
     */
    @ExcelProperty("邮箱")
    private String email;
    
    /**
     * 出生日期
     */
    @ExcelProperty("出生日期")
    private LocalDate birthday;
    
    /**
     * 入职日期
     */
    @ExcelProperty("入职日期")
    private LocalDate entryDate;
} 