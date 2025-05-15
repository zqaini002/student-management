package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 课程数据传输对象
 */
@Data
@Schema(description = "课程数据传输对象")
public class CourseDTO {

    @Schema(description = "课程ID")
    private Long id;

    @NotBlank(message = "课程编号不能为空")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "课程编号只能包含字母、数字和连字符")
    @Size(max = 30, message = "课程编号长度不能超过30个字符")
    @Schema(description = "课程编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    @Schema(description = "课程名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "学分不能为空")
    @Schema(description = "学分", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal credit;

    @NotNull(message = "学时不能为空")
    @Schema(description = "学时", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer hours;

    @NotNull(message = "课程类型不能为空")
    @Schema(description = "课程类型 0:必修 1:选修", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    @NotNull(message = "开课院系不能为空")
    @Schema(description = "开课院系ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long departmentId;

    @Size(max = 500, message = "课程描述长度不能超过500个字符")
    @Schema(description = "课程描述")
    private String description;
    
    @Schema(description = "适用专业ID列表")
    private List<Long> majorIds;
    
    @Schema(description = "推荐学期 (例如: 1-1, 2-2)")
    private String recommendSemester;
    
    @Schema(description = "先修课程ID列表")
    private List<Long> prerequisites;
    
    @Schema(description = "课程难度 (1-5)")
    private Integer difficulty;
} 