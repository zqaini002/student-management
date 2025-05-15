package com.example.student.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 学生导入数据传输对象
 *
 * @author example
 */
@Data
@Schema(description = "学生导入数据传输对象")
public class StudentImportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("学号")
    @Schema(description = "学号", example = "S202001001")
    private String studentNo;

    @ExcelProperty("姓名")
    @Schema(description = "姓名", example = "张三")
    private String name;

    @ExcelProperty("性别")
    @Schema(description = "性别", example = "男")
    private String gender;

    @ExcelProperty("班级名称")
    @Schema(description = "班级名称", example = "计算机科学2020-1班")
    private String className;

    @ExcelProperty("班级编码")
    @Schema(description = "班级编码", example = "CS2001")
    private String classCode;

    @ExcelProperty("入学日期")
    @Schema(description = "入学日期", example = "2020-09-01")
    private String admissionDate;

    @ExcelProperty("毕业日期")
    @Schema(description = "毕业日期", example = "2024-06-30")
    private String graduationDate;

    @ExcelProperty("身份证号")
    @Schema(description = "身份证号", example = "110101200001010033")
    private String idCard;

    @ExcelProperty("出生日期")
    @Schema(description = "出生日期", example = "2000-01-01")
    private String birthday;

    @ExcelProperty("家庭住址")
    @Schema(description = "家庭住址", example = "北京市海淀区")
    private String address;

    @ExcelProperty("邮箱")
    @Schema(description = "邮箱", example = "student1@example.com")
    private String email;

    @ExcelProperty("手机号")
    @Schema(description = "手机号", example = "13800000000")
    private String phone;

    @ExcelProperty("状态")
    @Schema(description = "状态", example = "在读")
    private String status;
    
    @Schema(description = "导入错误消息")
    private String errorMsg;
} 