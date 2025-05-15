package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 选课统计视图对象
 *
 * @author example
 */
@Data
@Schema(description = "选课统计数据")
public class CourseSelectionStatisticsVO {

    @Schema(description = "总选课数")
    private Integer totalSelections;

    @Schema(description = "待审核选课数")
    private Integer pendingSelections;

    @Schema(description = "已确认选课数")
    private Integer confirmedSelections;

    @Schema(description = "已退选数")
    private Integer withdrawnSelections;

    @Schema(description = "选课学生数")
    private Integer totalStudents;
    
    @Schema(description = "本学期选课数")
    private Integer currentSelections;
    
    @Schema(description = "已修完课程数")
    private Integer completedSelections;
} 