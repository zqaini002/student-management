package com.example.student.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 考勤统计视图对象
 *
 * @author example
 */
@Data
public class AttendanceStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计类型 (课程、班级、日期、状态)
     */
    private String type;
    
    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编码
     */
    private String courseCode;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级编码
     */
    private String classCode;

    /**
     * 日期
     */
    private String date;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 出勤数量
     */
    private Integer normalCount = 0;

    /**
     * 缺勤数量
     */
    private Integer absentCount = 0;

    /**
     * 迟到数量
     */
    private Integer lateCount = 0;

    /**
     * 早退数量
     */
    private Integer earlyLeaveCount = 0;

    /**
     * 请假数量
     */
    private Integer leaveCount = 0;

    /**
     * 总数量
     */
    private Integer totalCount = 0;

    /**
     * 记录数(用于状态统计)
     */
    private Integer count = 0;

    /**
     * 出勤率
     */
    private String attendanceRate;

    /**
     * 百分比(用于状态统计)
     */
    private String percentage;

    /**
     * 计算出勤率
     */
    public String getAttendanceRate() {
        if (totalCount == 0) {
            return "0";
        }
        BigDecimal rate = new BigDecimal(normalCount)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
        return rate.toString();
    }

    /**
     * 计算百分比
     */
    public String getPercentage() {
        if (count == 0 || totalCount == 0) {
            return "0";
        }
        BigDecimal percentage = new BigDecimal(count)
                .multiply(new BigDecimal(100))
                .divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
        return percentage.toString();
    }
} 