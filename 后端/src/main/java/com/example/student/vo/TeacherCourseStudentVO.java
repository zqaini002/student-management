package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师课程学生视图对象
 *
 * @author example
 */
@Data
@Schema(description = "教师课程学生视图对象")
public class TeacherCourseStudentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "选课ID")
    private Long courseSelectionId;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "专业名称")
    private String majorName;

    @Schema(description = "院系名称")
    private String departmentName;

    @Schema(description = "性别 0:男 1:女")
    private Integer gender;

    @Schema(description = "性别描述")
    private String genderDesc;

    @Schema(description = "选课时间")
    private LocalDateTime selectionTime;

    @Schema(description = "分数")
    private BigDecimal score;

    @Schema(description = "选课状态 0:已选 1:已退 2:已修完")
    private Integer status;

    @Schema(description = "选课状态描述")
    private String statusDesc;

    @Schema(description = "出勤率")
    private BigDecimal attendanceRate;

    @Schema(description = "最近一次考勤状态")
    private Integer lastAttendanceStatus;

    @Schema(description = "最近一次考勤日期")
    private LocalDate lastAttendanceDate;

    @Schema(description = "最近一次考勤描述")
    private String lastAttendanceDesc;

    @Schema(description = "出勤次数")
    private Integer attendCount;

    @Schema(description = "缺勤次数")
    private Integer absentCount;

    @Schema(description = "迟到次数")
    private Integer lateCount;

    @Schema(description = "早退次数")
    private Integer leaveEarlyCount;

    @Schema(description = "请假次数")
    private Integer leaveCount;
    
    @Schema(description = "考勤记录列表")
    private List<AttendanceRecordVO> attendanceRecords;
} 