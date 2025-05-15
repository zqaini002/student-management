package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级实体
 *
 * @author example
 */
@Data
@TableName("class")
@Schema(description = "班级实体")
public class Class implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "班级ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "班级名称")
    private String name;

    @Schema(description = "班级编码")
    private String code;

    @Schema(description = "所属专业ID")
    private Long majorId;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "班主任ID")
    private Long advisorId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 