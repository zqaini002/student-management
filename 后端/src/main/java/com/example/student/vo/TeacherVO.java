package com.example.student.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教师视图对象
 *
 * @author example
 */
@Data
@Schema(description = "教师视图对象")
public class TeacherVO {

    @Schema(description = "教师ID")
    private Long id;

    @Schema(description = "教师编号")
    private String teacherNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "院系ID")
    private Long departmentId;

    @Schema(description = "院系名称")
    private String departmentName;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "性别 0:男 1:女")
    private Integer gender;

    @Schema(description = "性别描述")
    private String genderDesc;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "出生日期")
    private LocalDate birthday;

    @Schema(description = "入职日期")
    private LocalDate entryDate;

    @Schema(description = "状态 0:在职 1:离职 2:退休")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 