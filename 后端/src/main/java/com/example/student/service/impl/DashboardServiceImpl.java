package com.example.student.service.impl;

import com.example.student.entity.*;
import com.example.student.mapper.*;
import com.example.student.service.DashboardService;
import com.example.student.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private TodoItemMapper todoItemMapper;
    
    @Autowired
    private NoticeMapper noticeMapper;
    
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Override
    public StatisticsData getStatisticsData() {
        // 获取当前登录用户信息
        SysUser currentUser = SecurityUtils.getCurrentUser();
        
        // 查询各个统计数据
        Long studentCount = studentMapper.selectCount(null);
        Long teacherCount = teacherMapper.selectCount(null);
        Long courseCount = courseMapper.selectCount(null);
        Long departmentCount = departmentMapper.selectCount(null);
        Long majorCount = majorMapper.selectCount(null);
        Long classCount = classMapper.selectCount(null);
        
        // 计算本周考勤率
        String weeklyAttendanceRate = calculateWeeklyAttendanceRate();
        
        // 构建并返回统计数据
        return StatisticsData.builder()
                .totalStudents(studentCount.intValue())
                .totalTeachers(teacherCount.intValue())
                .totalCourses(courseCount.intValue())
                .totalDepartments(departmentCount.intValue())
                .totalMajors(majorCount.intValue())
                .totalClasses(classCount.intValue())
                .weeklyAttendanceRate(weeklyAttendanceRate)
                .build();
    }
    
    /**
     * 计算本周考勤率
     * 
     * @return 格式化的考勤率字符串，如 "98.5%"
     */
    private String calculateWeeklyAttendanceRate() {
        try {
            // 获取本周的开始日期和结束日期
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
            
            // 格式化日期为数据库查询所需格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = startOfWeek.format(formatter);
            String endDate = endOfWeek.format(formatter);
            
            // 添加日志，记录当前计算的周时间范围
            System.out.println("计算本周考勤率 - 时间范围: " + startDate + " 至 " + endDate);
            System.out.println("当前系统时间: " + today.format(formatter));
            
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            
            // 查询本周考勤统计数据
            Map<String, Object> stats = attendanceMapper.getWeeklyAttendanceStats(params);
            
            // 日志记录，帮助诊断问题
            System.out.println("计算本周考勤率 - 统计数据: " + stats);
            
            if (stats != null && stats.containsKey("totalCount")) {
                int totalCount = ((Number) stats.get("totalCount")).intValue();
                
                // 记录总记录数
                System.out.println("计算本周考勤率 - 总记录数: " + totalCount);
                
                // 如果没有考勤记录，返回0.0%
                if (totalCount == 0) {
                    System.out.println("计算本周考勤率 - 没有考勤记录，返回0.0%");
                    return "0.0%";
                }
                
                // 获取正常出勤数量，如果为null则默认为0
                int normalCount = 0;
                if (stats.containsKey("normalCount") && stats.get("normalCount") != null) {
                    normalCount = ((Number) stats.get("normalCount")).intValue();
                }
                
                // 记录正常出勤数
                System.out.println("计算本周考勤率 - 正常出勤数: " + normalCount);
                
                // 计算考勤率
                double attendanceRate = (double) normalCount / totalCount * 100;
                // 格式化为一位小数
                String result = String.format("%.1f%%", attendanceRate);
                System.out.println("计算本周考勤率 - 计算结果: " + result);
                return result;
            } else {
                System.out.println("计算本周考勤率 - 未获取到有效统计数据");
                // 当查询结果为空时，返回0%
                return "0.0%";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("计算本周考勤率异常: " + e.getMessage());
            // 出现异常时，返回0%
            return "0.0%";
        }
    }

    @Override
    public List<TodoItem> getTodoList() {
        // 获取当前登录用户信息
        SysUser currentUser = SecurityUtils.getCurrentUser();
        
        // 从数据库中查询当前用户的待办事项
        return todoItemMapper.selectTodoItemsByUserId(currentUser.getId());
    }

    @Override
    public List<Notice> getNoticeList() {
        // 从数据库中查询已发布的系统公告
        return noticeMapper.selectPublishedNotices();
    }

    @Override
    public ChartData getStudentSemesterData(String timeRange) {
        // 获取当前日期
        LocalDate now = LocalDate.now();
        LocalDate startDate = null;
        
        // 根据时间范围参数设置查询的起始日期
        switch (timeRange) {
            case "近半年":
                startDate = now.minusMonths(6);
                break;
            case "近一年":
                startDate = now.minusYears(1);
                break;
            case "全部":
            default:
                startDate = null; // 不设置开始日期，查询全部
                break;
        }
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (startDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            params.put("startDate", startDate.format(formatter));
        }
        
        // 查询数据库获取按时间范围筛选的学期课程数据
        List<Map<String, Object>> semesterStats;
        
        if (startDate != null) {
            // 使用带时间范围的查询
            semesterStats = courseMapper.selectCourseBySemesterWithTimeRange(params);
        } else {
            // 使用原有查询方法
            semesterStats = courseMapper.selectCourseBySemester();
        }
        
        // 处理成按学期排序的数据
        Map<String, Integer> newCourseMap = new TreeMap<>();
        Map<String, Integer> finishedCourseMap = new TreeMap<>();
        
        // 处理查询结果，分类为新开课程和结课课程
        for (Map<String, Object> stat : semesterStats) {
            String semester = (String) stat.get("semester");
            Integer totalCourses = ((Number) stat.get("totalCourses")).intValue();
            Integer finishedCourses = ((Number) stat.get("finishedCourses")).intValue();
            
            newCourseMap.put(semester, totalCourses);
            finishedCourseMap.put(semester, finishedCourses);
        }
        
        // 准备返回数据
        List<String> labels = new ArrayList<>(newCourseMap.keySet());
        Map<String, List<Integer>> seriesData = new HashMap<>();
        
        seriesData.put("新开课程", new ArrayList<>(newCourseMap.values()));
        seriesData.put("结课课程", new ArrayList<>(finishedCourseMap.values()));
        
        // 如果没有数据，提供一个默认的空结果
        if (labels.isEmpty()) {
            labels.add("暂无数据");
            seriesData.put("新开课程", Arrays.asList(0));
            seriesData.put("结课课程", Arrays.asList(0));
        }
        
        return ChartData.builder()
                .labels(labels)
                .seriesData(seriesData)
                .chartType("line")
                .title("学期课程分布")
                .unit("门")
                .build();
    }

    @Override
    public ChartData getDepartmentDistribution(String year) {
        // 获取当前年份
        int currentYear = LocalDate.now().getYear();
        Integer yearValue = null;
        
        // 根据年份参数设置查询的年份
        switch (year) {
            case "今年":
                yearValue = currentYear;
                break;
            case "去年":
                yearValue = currentYear - 1;
                break;
            case "全部":
            default:
                yearValue = null; // 不设置年份，查询全部
                break;
        }
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (yearValue != null) {
            params.put("year", yearValue);
        }
        
        // 查询数据库获取按年份筛选的院系分布数据
        List<Map<String, Object>> departmentStats;
        
        if (yearValue != null) {
            // 使用带年份的查询
            departmentStats = studentMapper.selectDepartmentDistributionByYear(params);
        } else {
            // 使用原有查询方法
            departmentStats = studentMapper.selectDepartmentDistribution();
        }
        
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        
        // 处理查询结果
        for (Map<String, Object> stat : departmentStats) {
            String departmentName = (String) stat.get("departmentName");
            Integer studentCount = ((Number) stat.get("studentCount")).intValue();
            
            labels.add(departmentName);
            data.add(studentCount);
        }
        
        // 如果没有数据，提供一个默认的空结果
        if (labels.isEmpty()) {
            labels.add("暂无数据");
            data.add(0);
        }
        
        return ChartData.builder()
                .labels(labels)
                .data(data)
                .chartType("pie")
                .title("院系学生分布")
                .unit("人")
                .build();
    }

    @Override
    public ChartData getStudentPersonalStats() {
        // 获取当前登录用户
        SysUser currentUser = SecurityUtils.getCurrentUser();
        
        // 查询关联的学生记录
        Long studentId = studentMapper.selectStudentIdByUserId(currentUser.getId());
        
        if (studentId == null) {
            // 如果找不到学生记录，返回空数据
            List<String> emptyLabels = Arrays.asList("暂无数据");
            Map<String, List<Integer>> emptyData = new HashMap<>();
            emptyData.put("数据", Arrays.asList(0));
            
            return ChartData.builder()
                    .labels(emptyLabels)
                    .seriesData(emptyData)
                    .chartType("pie")
                    .title("个人学习统计")
                    .unit("门")
                    .build();
        }
        
        // 查询学生的课程统计信息
        Map<String, Object> courseStats = courseSelectionMapper.getStudentCourseStats(studentId);
        
        // 准备图表数据
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        
        // 添加各类课程数据
        int totalCourses = ((Number) courseStats.getOrDefault("totalCourses", 0)).intValue();
        int completedCourses = ((Number) courseStats.getOrDefault("completedCourses", 0)).intValue();
        int inProgressCourses = ((Number) courseStats.getOrDefault("inProgressCourses", 0)).intValue();
        int withdrawnCourses = ((Number) courseStats.getOrDefault("withdrawnCourses", 0)).intValue();
        
        // 构建饼图数据
        labels.add("已完成课程");
        data.add(completedCourses);
        
        labels.add("进行中课程");
        data.add(inProgressCourses);
        
        labels.add("已退选课程");
        data.add(withdrawnCourses);
        
        // 处理数据
        Map<String, List<Integer>> seriesData = new HashMap<>();
        seriesData.put("课程数量", data);
        
        return ChartData.builder()
                .labels(labels)
                .seriesData(seriesData)
                .chartType("pie")
                .title("个人学习统计")
                .unit("门")
                .build();
    }

    @Override
    public ChartData getStudentGradeDistribution() {
        // 获取当前登录用户
        SysUser currentUser = SecurityUtils.getCurrentUser();
        
        // 查询关联的学生记录
        Long studentId = studentMapper.selectStudentIdByUserId(currentUser.getId());
        
        if (studentId == null) {
            // 如果找不到学生记录，返回空数据
            List<String> emptyLabels = Arrays.asList("暂无数据");
            Map<String, List<Integer>> emptyData = new HashMap<>();
            emptyData.put("成绩分布", Arrays.asList(0));
            
            return ChartData.builder()
                    .labels(emptyLabels)
                    .seriesData(emptyData)
                    .chartType("bar")
                    .title("成绩分布")
                    .unit("门")
                    .build();
        }
        
        // 查询学生的成绩分布信息
        Map<String, Object> gradeStats = courseSelectionMapper.getStudentGradeDistribution(studentId);
        
        // 准备图表数据
        List<String> labels = Arrays.asList("90-100分", "80-89分", "70-79分", "60-69分", "不及格");
        List<Integer> data = new ArrayList<>();
        
        // 检查gradeStats是否为null，如果为null说明该学生还没有成绩记录
        if (gradeStats == null || gradeStats.isEmpty()) {
            // 如果没有成绩数据，返回全部为0的图表数据
            data.add(0); // excellent
            data.add(0); // good
            data.add(0); // average
            data.add(0); // pass
            data.add(0); // fail
        } else {
            // 获取各分数段的课程数量
            int excellent = gradeStats.get("excellent") != null ? ((Number) gradeStats.get("excellent")).intValue() : 0;
            int good = gradeStats.get("good") != null ? ((Number) gradeStats.get("good")).intValue() : 0;
            int average = gradeStats.get("average") != null ? ((Number) gradeStats.get("average")).intValue() : 0;
            int pass = gradeStats.get("pass") != null ? ((Number) gradeStats.get("pass")).intValue() : 0;
            int fail = gradeStats.get("fail") != null ? ((Number) gradeStats.get("fail")).intValue() : 0;
            
            // 添加到数据列表
            data.add(excellent);
            data.add(good);
            data.add(average);
            data.add(pass);
            data.add(fail);
        }
        
        // 处理数据
        Map<String, List<Integer>> seriesData = new HashMap<>();
        seriesData.put("课程数量", data);
        
        // 添加平均分数据
        Map<String, List<Double>> seriesDataDouble = new HashMap<>();
        if (gradeStats != null && gradeStats.containsKey("averageScore") && gradeStats.get("averageScore") != null) {
            Double averageScore = ((Number) gradeStats.get("averageScore")).doubleValue();
            List<Double> averageScoreList = new ArrayList<>();
            averageScoreList.add(averageScore);
            seriesDataDouble.put("平均分", averageScoreList);
        }
        
        return ChartData.builder()
                .labels(labels)
                .seriesData(seriesData)
                .seriesDataDouble(seriesDataDouble)
                .chartType("bar")
                .title("成绩分布")
                .unit("门")
                .build();
    }
} 
