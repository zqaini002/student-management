package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 成绩统计VO
 *
 * @author example
 */
@Data
@Schema(description = "成绩统计数据")
public class GradeStatisticsVO {

    @Schema(description = "概览数据")
    private Map<String, Object> overview;

    @Schema(description = "成绩分布")
    private List<Map<String, Object>> scoreDistribution;

    @Schema(description = "班级统计数据")
    private List<Map<String, Object>> classStats;
} 