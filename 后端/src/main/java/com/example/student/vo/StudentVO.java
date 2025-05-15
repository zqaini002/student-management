package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生视图对象
 *
 * @author example
 */
@Data
@Schema(description = "学生视图对象")
public class StudentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "学生ID")
    private Long id;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "专业ID")
    private Long majorId;

    @Schema(description = "专业名称")
    private String majorName;

    @Schema(description = "院系ID")
    private Long departmentId;

    @Schema(description = "院系名称")
    private String departmentName;

    @Schema(description = "入学日期")
    private LocalDate admissionDate;

    @Schema(description = "毕业日期")
    private LocalDate graduationDate;

    @Schema(description = "性别 0:男 1:女")
    private Integer gender;

    @Schema(description = "性别描述")
    private String genderDesc;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "出生日期")
    private LocalDate birthday;

    @Schema(description = "家庭住址")
    private String address;

    @Schema(description = "状态 0:在读 1:毕业 2:休学 3:退学")
    private Integer status;
    
    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
} 