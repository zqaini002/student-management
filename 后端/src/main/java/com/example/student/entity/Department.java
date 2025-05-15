package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 院系实体
 *
 * @author example
 */
@Data
@TableName("department")
@Schema(description = "院系实体")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "院系ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "院系名称")
    private String name;

    @Schema(description = "院系编码")
    private String code;

    @Schema(description = "院系描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 