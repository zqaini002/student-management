package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 成绩查询DTO
 *
 * @author example
 */
@Data
@Schema(description = "成绩查询参数")
public class GradeQueryDTO {

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "课程开设ID")
    private Long courseOfferingId;

    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", defaultValue = "10")
    private Integer pageSize = 10;
} 