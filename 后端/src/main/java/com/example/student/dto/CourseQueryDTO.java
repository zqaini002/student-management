package com.example.student.dto;

import com.example.student.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程查询数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程查询数据传输对象")
public class CourseQueryDTO extends PageRequest {

    @Schema(description = "课程编号")
    private String code;

    @Schema(description = "课程名称")
    private String name;

    @Schema(description = "课程类型 0:必修 1:选修")
    private Integer type;

    @Schema(description = "开课院系ID")
    private Long departmentId;
    
    @Schema(description = "学分下限")
    private BigDecimal creditMin;
    
    @Schema(description = "学分上限")
    private BigDecimal creditMax;
    
    @Schema(description = "学时下限")
    private Integer hoursMin;
    
    @Schema(description = "学时上限")
    private Integer hoursMax;
} 