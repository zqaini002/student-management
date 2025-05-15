package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 教师数据传输对象
 *
 * @author example
 */
@Data
@Schema(description = "教师数据传输对象")
public class TeacherDTO {

    @Schema(description = "教师ID")
    private Long id;

    @NotBlank(message = "教师编号不能为空")
    @Schema(description = "教师编号", example = "T0001")
    private String teacherNo;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$", message = "用户名必须为4-16位字母或数字")
    @Schema(description = "用户名", example = "teacher001")
    private String username;

    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", example = "李教授")
    private String name;

    @NotNull(message = "院系ID不能为空")
    @Schema(description = "院系ID", example = "1")
    private Long departmentId;

    @Schema(description = "院系名称", example = "计算机科学与技术学院")
    private String departmentName;

    @Schema(description = "职称", example = "教授")
    private String title;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer gender;

    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    @Schema(description = "身份证号", example = "110101198001010011")
    private String idCard;

    @Schema(description = "出生日期", example = "1980-01-01")
    private LocalDate birthday;

    @NotNull(message = "入职日期不能为空")
    @Schema(description = "入职日期", example = "2010-09-01")
    private LocalDate entryDate;

    @Schema(description = "状态 0:在职 1:离职 2:退休", example = "0")
    private Integer status;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "teacher@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800000001")
    private String phone;

    @Schema(description = "头像")
    private String avatar;
} 