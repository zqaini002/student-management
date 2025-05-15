package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师课程视图对象
 *
 * @author example
 */
@Data
@Schema(description = "教师课程视图对象")
public class TeacherCourseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "课程开设ID")
    private Long courseOfferingId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程编码")
    private String courseCode;

    @Schema(description = "学分")
    private Double credit;

    @Schema(description = "课程类型 0:必修 1:选修")
    private Integer courseType;

    @Schema(description = "课程类型描述")
    private String courseTypeDesc;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "上课时间")
    private String classTime;

    @Schema(description = "上课地点")
    private String location;

    @Schema(description = "容量")
    private Integer capacity;

    @Schema(description = "已选人数")
    private Integer selectedCount;

    @Schema(description = "状态 0:正常 1:已结课")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 