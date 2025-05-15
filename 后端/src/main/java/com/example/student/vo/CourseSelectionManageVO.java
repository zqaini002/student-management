package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 选课管理视图对象
 *
 * @author example
 */
@Data
@Schema(description = "选课管理视图对象")
public class CourseSelectionManageVO {

    @Schema(description = "选课ID")
    private Long id;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生学号")
    private String studentNo;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "课程开设ID")
    private Long courseOfferingId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程代码")
    private String courseCode;

    @Schema(description = "学分")
    private BigDecimal credits;

    @Schema(description = "课程类型")
    private Integer courseType;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "选课状态：0-已选，1-已退，2-已修完")
    private Integer status;

    @Schema(description = "选课状态描述")
    private String statusDesc;

    @Schema(description = "分数")
    private BigDecimal score;

    @Schema(description = "选课时间")
    private LocalDateTime selectionTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 