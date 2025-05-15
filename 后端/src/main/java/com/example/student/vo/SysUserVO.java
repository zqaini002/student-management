package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户视图对象
 *
 * @author example
 */
@Data
@Schema(description = "系统用户视图对象")
public class SysUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态 0:正常 1:禁用")
    private Integer status;
    
    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "用户类型 0:管理员 1:教师 2:学生")
    private Integer userType;
    
    @Schema(description = "用户类型描述")
    private String userTypeDesc;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
    
    @Schema(description = "角色名称列表")
    private List<String> roleNames;
} 