package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "课程开设分页查询DTO")
public class CourseOfferingQueryDTO {
    @Schema(description = "课程名称关键字")
    private String courseName;

    @Schema(description = "教师名称关键字")
    private String teacherName;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
} 