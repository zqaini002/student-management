package com.example.student.dto;

import com.example.student.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师查询条件
 *
 * @author example
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "教师查询条件")
public class TeacherQueryDTO extends PageRequest {

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师编号")
    private String teacherNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "院系ID")
    private Long departmentId;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "状态 0:在职 1:离职 2:退休")
    private Integer status;
    
    @Schema(description = "学期")
    private String semester;
    
    @Schema(description = "课程名称")
    private String courseName;
    
    @Schema(description = "学生姓名")
    private String studentName;
    
    @Schema(description = "学号")
    private String studentNo;
    
    @Schema(description = "学生ID，用于查询单个学生的考勤详情")
    private Long studentId;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "出生日期开始")
    private String birthdayStart;
    
    @Schema(description = "出生日期结束")
    private String birthdayEnd;
    
    @Schema(description = "入职日期开始")
    private String entryDateStart;
    
    @Schema(description = "入职日期结束")
    private String entryDateEnd;
} 