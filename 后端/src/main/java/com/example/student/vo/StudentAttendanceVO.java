package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生考勤视图对象
 *
 * @author example
 */
@Data
@Schema(description = "学生考勤视图对象")
public class StudentAttendanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "考勤ID")
    private Long id;

    @Schema(description = "课程开设ID")
    private Long courseOfferingId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程编码")
    private String courseCode;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "考勤日期")
    private LocalDate attendanceDate;

    @Schema(description = "考勤状态 0:出勤 1:缺勤 2:迟到 3:早退 4:请假")
    private Integer status;

    @Schema(description = "考勤状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
} 