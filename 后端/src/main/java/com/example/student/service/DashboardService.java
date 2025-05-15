package com.example.student.service;

import com.example.student.entity.ChartData;
import com.example.student.entity.Notice;
import com.example.student.entity.StatisticsData;
import com.example.student.entity.TodoItem;

import java.util.List;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取统计数据
     *
     * @return 统计数据
     */
    StatisticsData getStatisticsData();

    /**
     * 获取待办事项列表
     *
     * @return 待办事项列表
     */
    List<TodoItem> getTodoList();

    /**
     * 获取系统公告列表
     *
     * @return 系统公告列表
     */
    List<Notice> getNoticeList();

    /**
     * 获取学生按学期分布数据
     *
     * @param timeRange 时间范围，可选值：近半年, 近一年, 全部
     * @return 图表数据
     */
    ChartData getStudentSemesterData(String timeRange);
    
    /**
     * 获取学生按学期分布数据 (默认近半年)
     *
     * @return 图表数据
     */
    default ChartData getStudentSemesterData() {
        return getStudentSemesterData("近半年");
    }

    /**
     * 获取院系分布数据
     *
     * @param year 年份，可选值：今年, 去年, 全部
     * @return 图表数据
     */
    ChartData getDepartmentDistribution(String year);
    
    /**
     * 获取院系分布数据 (默认今年)
     *
     * @return 图表数据
     */
    default ChartData getDepartmentDistribution() {
        return getDepartmentDistribution("今年");
    }
    
    /**
     * 获取学生个人学习统计数据
     *
     * @return 图表数据
     */
    ChartData getStudentPersonalStats();
    
    /**
     * 获取学生成绩分布数据
     *
     * @return 图表数据
     */
    ChartData getStudentGradeDistribution();
} 