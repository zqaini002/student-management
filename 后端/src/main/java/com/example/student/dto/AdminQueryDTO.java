package com.example.student.dto;

import com.example.student.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员查询条件
 *
 * @author example
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理员查询条件")
public class AdminQueryDTO extends PageRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "状态 0:正常 1:禁用")
    private Integer status;
} 