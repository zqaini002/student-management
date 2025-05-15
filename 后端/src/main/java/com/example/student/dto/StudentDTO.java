package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学生数据传输对象
 *
 * @author example
 */
@Data
@Schema(description = "学生数据传输对象")
public class StudentDTO {

    @Schema(description = "学生ID")
    private Long id;

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号", example = "S202001001")
    private String studentNo;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", example = "张三")
    private String name;

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID", example = "1")
    private Long classId;

    @Schema(description = "班级名称", example = "计算机科学2020-1班")
    private String className;

    @NotNull(message = "入学日期不能为空")
    @Schema(description = "入学日期", example = "2020-09-01")
    private LocalDate admissionDate;

    @Schema(description = "毕业日期", example = "2024-06-30")
    private LocalDate graduationDate;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer gender;

    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    @Schema(description = "身份证号", example = "110101200001010033")
    private String idCard;

    @Schema(description = "出生日期", example = "2000-01-01")
    private LocalDate birthday;

    @Schema(description = "家庭住址", example = "北京市海淀区")
    private String address;

    @Schema(description = "状态 0:在读 1:毕业 2:休学 3:退学", example = "0")
    private Integer status;
    
    @Schema(description = "用户名", example = "student1")
    private String username;
    
    @Schema(description = "密码", example = "123456")
    private String password;
    
    @Schema(description = "邮箱", example = "student1@example.com")
    private String email;
    
    @Schema(description = "手机号", example = "13800000000")
    private String phone;
}