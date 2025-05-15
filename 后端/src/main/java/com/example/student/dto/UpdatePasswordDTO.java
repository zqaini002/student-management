package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码数据传输对象
 *
 * @author example
 */
@Data
@Schema(description = "修改密码数据传输对象")
public class UpdatePasswordDTO {

    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    @Schema(description = "新密码")
    private String newPassword;
} 