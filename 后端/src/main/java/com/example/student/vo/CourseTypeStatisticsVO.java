package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程类型统计视图对象
 *
 * @author example
 */
@Data
@Schema(description = "课程类型统计数据")
public class CourseTypeStatisticsVO {

    /**
     * 课程类型名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 数量值
     */
    @Schema(description = "数值")
    private Integer value;
} 