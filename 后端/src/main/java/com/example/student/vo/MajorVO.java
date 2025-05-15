package com.example.student.vo;

import com.example.student.entity.Major;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专业视图对象
 *
 * @author example
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "专业视图对象")
public class MajorVO extends Major {

    private static final long serialVersionUID = 1L;

    @Schema(description = "所属院系名称")
    private String departmentName;
} 