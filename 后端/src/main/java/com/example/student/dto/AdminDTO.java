package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 管理员数据传输对象
 *
 * @author example
 */
@Data
@Schema(description = "管理员数据传输对象")
public class AdminDTO {

    @Schema(description = "管理员ID")
    private Long id;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$", message = "用户名必须为4-16位字母或数字")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "状态 0:正常 1:禁用")
    private Integer status;
} 