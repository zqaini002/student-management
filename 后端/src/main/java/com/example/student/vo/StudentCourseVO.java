package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生课程视图对象
 *
 * @author example
 */
@Data
@Schema(description = "学生课程视图对象")
public class StudentCourseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "选课ID")
    private Long id;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程编码")
    private String courseCode;

    @Schema(description = "课程学分")
    private BigDecimal credit;

    @Schema(description = "课程学时")
    private Integer hours;

    @Schema(description = "课程类型 0:必修 1:选修")
    private Integer courseType;

    @Schema(description = "课程类型描述")
    private String courseTypeDesc;

    @Schema(description = "课程开设ID")
    private Long courseOfferingId;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "上课时间")
    private String classTime;

    @Schema(description = "上课地点")
    private String location;

    @Schema(description = "选课时间")
    private LocalDateTime selectionTime;

    @Schema(description = "成绩")
    private BigDecimal score;

    @Schema(description = "选课状态 0:已选 1:已退 2:已修完")
    private Integer status;

    @Schema(description = "选课状态描述")
    private String statusDesc;
} 