package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户登录响应
 *
 * @author example
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录响应")
public class LoginResponseDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "用户类型 0:管理员 1:教师 2:学生")
    private Integer userType;

    @Schema(description = "角色列表")
    private List<String> roles;

    @Schema(description = "权限列表")
    private List<String> permissions;

    @Schema(description = "token")
    private String token;
} 