package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 系统用户数据传输对象
 *
 * @author example
 */
@Data
@Schema(description = "系统用户数据传输对象")
public class SysUserDTO {

    @Schema(description = "用户ID")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2到20个字符之间")
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度必须在2到20个字符之间")
    @Schema(description = "姓名")
    private String name;

    @Schema(description = "头像")
    private String avatar;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态 0:正常 1:禁用")
    private Integer status;

    @Schema(description = "用户类型 0:管理员 1:教师 2:学生")
    private Integer userType;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
} 