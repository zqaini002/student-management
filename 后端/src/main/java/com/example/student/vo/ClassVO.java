package com.example.student.vo;

import com.example.student.entity.Class;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 班级视图对象
 *
 * @author example
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "班级信息视图对象")
public class ClassVO extends Class {

    private static final long serialVersionUID = 1L;

    @Schema(description = "专业名称")
    private String majorName;

    @Schema(description = "班主任姓名")
    private String advisorName;

    @Schema(description = "学生数量")
    private Integer studentCount;
} 