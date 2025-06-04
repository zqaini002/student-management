package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 考勤记录视图对象
 *
 * @author example
 */
@Data
@Schema(description = "考勤记录视图对象")
public class AttendanceRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "考勤记录ID")
    private Long id;

    @Schema(description = "考勤日期")
    private LocalDate date;

    @Schema(description = "考勤状态 0:出勤 1:缺勤 2:迟到 3:早退 4:请假")
    private Integer status;

    @Schema(description = "考勤备注")
    private String remark;

    @Schema(description = "课程开设ID")
    private Long courseOfferingId;

    @Schema(description = "学生ID")
    private Long studentId;
} 