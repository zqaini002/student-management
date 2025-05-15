package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤实体类
 *
 * @author example
 */
@Data
@TableName("attendance")
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 考勤日期
     */
    private LocalDate attendanceDate;

    /**
     * 状态 0:出勤 1:缺勤 2:迟到 3:早退 4:请假
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 