package com.example.student.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤视图对象
 *
 * @author example
 */
@Data
public class AttendanceVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 课程开设ID
     */
    private Long courseOfferingId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编码
     */
    private String courseCode;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学期
     */
    private String semester;

    /**
     * 考勤日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    /**
     * 状态 0:出勤 1:缺勤 2:迟到 3:早退 4:请假
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 0:
                return "出勤";
            case 1:
                return "缺勤";
            case 2:
                return "迟到";
            case 3:
                return "早退";
            case 4:
                return "请假";
            default:
                return "未知";
        }
    }
} 