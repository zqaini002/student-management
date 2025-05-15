package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "课程开设DTO")
public class CourseOfferingDTO implements Serializable {
    @Schema(description = "主键ID")
    private Long id;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long courseId;

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "班级ID")
    private Long classId;

    @NotNull(message = "学期不能为空")
    @Schema(description = "学期")
    private String semester;

    @NotNull(message = "上课时间不能为空")
    @Schema(description = "上课时间")
    private String classTime;

    @NotNull(message = "上课地点不能为空")
    @Schema(description = "上课地点")
    private String location;

    @NotNull(message = "容量不能为空")
    @Schema(description = "容量")
    private Integer capacity;

    @Schema(description = "已选人数")
    private Integer selectedCount;

    @Schema(description = "状态 0:正常 1:已结课")
    private Integer status;
} 