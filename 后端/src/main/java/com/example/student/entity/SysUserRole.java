package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联表
 *
 * @author example
 */
@Data
@TableName("sys_user_role")
@Schema(description = "用户角色关联表")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableId(value = "user_id")
    private Long userId;

    @Schema(description = "角色ID")
    @TableField(value = "role_id")
    private Long roleId;
} 