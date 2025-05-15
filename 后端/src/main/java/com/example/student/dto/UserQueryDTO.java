package com.example.student.dto;

import com.example.student.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询条件
 *
 * @author example
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询条件")
public class UserQueryDTO extends PageRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态 0:正常 1:禁用")
    private Integer status;

    @Schema(description = "用户类型 0:管理员 1:教师 2:学生")
    private Integer userType;
} 