package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "课程开设VO")
public class CourseOfferingVO implements Serializable {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程代码")
    private String courseCode;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师名称")
    private String teacherName;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "班级代码")
    private String classCode;

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
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 