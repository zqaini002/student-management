package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.common.PageResult;
import com.example.student.common.exception.BusinessException;
import com.example.student.dto.CourseDTO;
import com.example.student.dto.CourseQueryDTO;
import com.example.student.entity.Course;
import com.example.student.mapper.CourseMapper;
import com.example.student.service.CourseService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 课程服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseMapper courseMapper;

    @Override
    public PageResult<Course> getCoursePage(CourseQueryDTO queryDTO) {
        // 创建分页对象
        Page<Course> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(queryDTO.getCode()), Course::getCode, queryDTO.getCode())
                .like(StringUtils.isNotBlank(queryDTO.getName()), Course::getName, queryDTO.getName())
                .eq(queryDTO.getType() != null, Course::getType, queryDTO.getType())
                .eq(queryDTO.getDepartmentId() != null, Course::getDepartmentId, queryDTO.getDepartmentId());
        
        // 使用BigDecimal的compareTo方法进行比较
        if (queryDTO.getCreditMin() != null) {
            queryWrapper.apply("credit >= {0}", queryDTO.getCreditMin());
        }
        if (queryDTO.getCreditMax() != null) {
            queryWrapper.apply("credit <= {0}", queryDTO.getCreditMax());
        }
        
        if (queryDTO.getHoursMin() != null) {
            queryWrapper.ge(Course::getHours, queryDTO.getHoursMin());
        }
        if (queryDTO.getHoursMax() != null) {
            queryWrapper.le(Course::getHours, queryDTO.getHoursMax());
        }
        
        queryWrapper.orderByDesc(Course::getCreateTime);
        
        // 执行查询
        IPage<Course> result = page(page, queryWrapper);
        
        // 返回分页结果
        return PageResult.build(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public Course getCourseById(Long id) {
        // 先使用mapper的selectCourseDetailById获取详细信息
        Map<String, Object> courseDetail = courseMapper.selectCourseDetailById(id);
        
        if (courseDetail == null || courseDetail.isEmpty()) {
            throw new BusinessException("课程不存在");
        }
        
        // 构造Course对象
        Course course = new Course();
        
        // 设置基本属性
        course.setId(Long.valueOf(courseDetail.get("id").toString()));
        course.setCode((String) courseDetail.get("code"));
        course.setName((String) courseDetail.get("name"));
        
        if (courseDetail.get("type") != null) {
            course.setType(Integer.valueOf(courseDetail.get("type").toString()));
        }
        
        if (courseDetail.get("credit") != null) {
            if (courseDetail.get("credit") instanceof BigDecimal) {
                course.setCredit((BigDecimal) courseDetail.get("credit"));
            } else {
                course.setCredit(new BigDecimal(courseDetail.get("credit").toString()));
            }
        }
        
        if (courseDetail.get("hours") != null) {
            course.setHours(Integer.valueOf(courseDetail.get("hours").toString()));
        }
        
        course.setDescription((String) courseDetail.get("description"));
        
        if (courseDetail.get("department_id") != null) {
            course.setDepartmentId(Long.valueOf(courseDetail.get("department_id").toString()));
        }
        
        // 设置扩展字段
        if (courseDetail.get("teacher_id") != null) {
            course.setTeacherId(Long.valueOf(courseDetail.get("teacher_id").toString()));
        }
        
        course.setTeacherName((String) courseDetail.get("teacher_name"));
        course.setTeacherContact((String) courseDetail.get("teacher_contact"));
        course.setSemester((String) courseDetail.get("semester"));
        course.setClassTime((String) courseDetail.get("class_time"));
        course.setLocation((String) courseDetail.get("location"));
        
        if (courseDetail.get("status") != null) {
            course.setStatus(Integer.valueOf(courseDetail.get("status").toString()));
        }
        
        course.setStatusDesc((String) courseDetail.get("status_desc"));
        
        if (courseDetail.get("capacity") != null) {
            course.setCapacity(Integer.valueOf(courseDetail.get("capacity").toString()));
        }
        
        if (courseDetail.get("selected_count") != null) {
            course.setSelectedCount(Integer.valueOf(courseDetail.get("selected_count").toString()));
        }
        
        course.setDepartmentName((String) courseDetail.get("department_name"));
        // 考核方式，由于数据库中可能没有，暂时使用固定值
        course.setAssessmentMethod("笔试+平时考核");
        
        return course;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCourse(CourseDTO courseDTO) {
        // 检查课程编号是否已存在
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getCode, courseDTO.getCode());
        if (count(queryWrapper) > 0) {
            throw new BusinessException("课程编号已存在");
        }
        
        // 创建课程对象并保存
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        
        // 手动设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        course.setCreateTime(now);
        course.setUpdateTime(now);
        
        if (!save(course)) {
            throw new BusinessException("新增课程失败");
        }
        
        // TODO: 保存课程与专业的关系、保存先修课程关系等
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCourse(CourseDTO courseDTO) {
        // 检查课程是否存在
        Course existingCourse = getById(courseDTO.getId());
        if (existingCourse == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 如果课程编号有变更，需要检查是否与其他课程冲突
        if (!existingCourse.getCode().equals(courseDTO.getCode())) {
            LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Course::getCode, courseDTO.getCode());
            if (count(queryWrapper) > 0) {
                throw new BusinessException("课程编号已存在");
            }
        }
        
        // 更新课程信息
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        
        // 手动设置更新时间
        course.setUpdateTime(LocalDateTime.now());
        
        if (!updateById(course)) {
            throw new BusinessException("更新课程失败");
        }
        
        // TODO: 更新课程与专业的关系、更新先修课程关系等
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(Long id) {
        // 检查课程是否存在
        if (!removeById(id)) {
            throw new BusinessException("删除课程失败");
        }
        
        // TODO: 删除课程与专业的关系、删除先修课程关系等
    }

    @Override
    public void exportCourse(CourseQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        // TODO: 实现导出课程功能，可参考TeacherService的导出方法
        throw new BusinessException("导出课程功能暂未实现");
    }

    @Override
    public void importCourse(MultipartFile file) throws IOException {
        // TODO: 实现导入课程功能，可参考TeacherService的导入方法
        throw new BusinessException("导入课程功能暂未实现");
    }

    @Override
    public List<Course> getCoursesByDepartmentId(Long departmentId) {
        return courseMapper.selectCoursesByDepartmentId(departmentId);
    }

    @Override
    public Map<String, Object> getCourseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCourses", count());
        stats.put("requiredCourses", count(new LambdaQueryWrapper<Course>().eq(Course::getType, 0)));
        stats.put("electiveCourses", count(new LambdaQueryWrapper<Course>().eq(Course::getType, 1)));
        
        // 按学分分布统计
        stats.put("creditDistribution", courseMapper.selectCreditDistribution());
        
        return stats;
    }

    @Override
    public PageResult<Course> getCoursesForAttendance(String keyword, Integer pageNum, Integer pageSize) {
        log.info("考勤记录模块查询课程，关键词: {}", keyword);
        
        // 查询总数
        List<Course> courses = courseMapper.selectCoursesForAttendance(keyword);
        int total = courses.size();
        
        // 手动分页
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<Course> pageRecords;
        if (fromIndex < total) {
            pageRecords = courses.subList(fromIndex, toIndex);
        } else {
            pageRecords = new ArrayList<>();
        }
        
        log.info("考勤记录模块查询课程结果数量: {}", pageRecords.size());
        
        return PageResult.build(pageRecords, total, pageNum, pageSize);
    }

    @Override
    public boolean checkTimeConflict(String courseTime1, String courseTime2) {
        if (courseTime1 == null || courseTime2 == null ||
            courseTime1.isEmpty() || courseTime2.isEmpty()) {
            return false;
        }
        
        log.info("检查课程时间冲突: courseTime1={}, courseTime2={}", courseTime1, courseTime2);
        
        // 解析课程时间
        // 假设时间格式为 "周X 1-2节" 或 "周X 第3节" 等
        return hasConflict(courseTime1, courseTime2);
    }

    /**
     * 检查两个课程时间是否冲突
     *
     * @param courseTime1 课程时间1
     * @param courseTime2 课程时间2
     * @return 是否冲突
     */
    private boolean hasConflict(String courseTime1, String courseTime2) {
        // 将课程时间分解成日期和节次
        Map<String, List<Integer>> schedule1 = parseSchedule(courseTime1);
        Map<String, List<Integer>> schedule2 = parseSchedule(courseTime2);
        
        // 检查是否有相同的日期
        for (String day : schedule1.keySet()) {
            if (schedule2.containsKey(day)) {
                // 同一天，检查节次是否冲突
                List<Integer> periods1 = schedule1.get(day);
                List<Integer> periods2 = schedule2.get(day);
                
                for (Integer period1 : periods1) {
                    if (periods2.contains(period1)) {
                        // 存在相同节次，冲突
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * 解析课程时间格式
     * 
     * @param courseTime 课程时间字符串
     * @return 日期和节次的映射
     */
    private Map<String, List<Integer>> parseSchedule(String courseTime) {
        Map<String, List<Integer>> schedule = new HashMap<>();
        
        if (courseTime == null || courseTime.trim().isEmpty()) {
            return schedule;
        }
        
        // 预处理：移除多余空格，标准化分隔符
        courseTime = courseTime.replaceAll("\\s+", " ").trim();
        
        // 分隔多个时间段，支持多种分隔符
        String[] timeParts = courseTime.split("[,;，；]\\s*");
        
        for (String timePart : timeParts) {
            // 过滤空字符串
            if (timePart.trim().isEmpty()) {
                continue;
            }
            
            try {
                // 尝试多种格式匹配
                // 1. 标准格式：周一 1-2节
                // 2. 简写格式：周一1-2节
                // 3. 单节格式：周一 第3节
                // 4. 连续节格式：周一 1,2,3节
                // 5. 其他可能格式：周一(1-2)、周一第1,2节等
                
                // 首先尝试匹配最常见的格式
                Pattern pattern = Pattern.compile("周([一二三四五六日])\\s*(?:第)?([\\d]+)(?:-([\\d]+))?(?:节)?");
                Matcher matcher = pattern.matcher(timePart);
                
                if (matcher.find()) {
                    String day = matcher.group(1);
                    int startPeriod = Integer.parseInt(matcher.group(2));
                    
                    List<Integer> periods = new ArrayList<>();
                    
                    if (matcher.group(3) != null) {
                        // 范围，如1-3节
                        int endPeriod = Integer.parseInt(matcher.group(3));
                        for (int i = startPeriod; i <= endPeriod; i++) {
                            periods.add(i);
                        }
                    } else {
                        // 单节
                        periods.add(startPeriod);
                    }
                    
                    // 合并同一天的多个节次
                    if (schedule.containsKey(day)) {
                        schedule.get(day).addAll(periods);
                    } else {
                        schedule.put(day, periods);
                    }
                } else {
                    // 尝试匹配更复杂的格式
                    // 首先提取星期
                    Pattern dayPattern = Pattern.compile("周([一二三四五六日])");
                    Matcher dayMatcher = dayPattern.matcher(timePart);
                    
                    if (dayMatcher.find()) {
                        String day = dayMatcher.group(1);
                        
                        // 然后尝试提取节次
                        // 可能包含的格式：
                        // 1. 数字-数字
                        // 2. 数字,数字
                        // 3. (数字-数字)
                        // 4. 第数字节
                        Pattern periodPattern = Pattern.compile("(\\d+)(?:[-,]|\\s+到\\s+|\\s+至\\s+)(\\d+)|第(\\d+)节|\\((\\d+)-(\\d+)\\)|(\\d+)节");
                        Matcher periodMatcher = periodPattern.matcher(timePart);
                        
                        List<Integer> periods = new ArrayList<>();
                        
                        if (periodMatcher.find()) {
                            // 处理各种可能的格式
                            if (periodMatcher.group(1) != null && periodMatcher.group(2) != null) {
                                // 类型1: 数字-数字 或 数字,数字
                                int startPeriod = Integer.parseInt(periodMatcher.group(1));
                                int endPeriod = Integer.parseInt(periodMatcher.group(2));
                                
                                for (int i = startPeriod; i <= endPeriod; i++) {
                                    periods.add(i);
                                }
                            } else if (periodMatcher.group(3) != null) {
                                // 类型2: 第N节
                                periods.add(Integer.parseInt(periodMatcher.group(3)));
                            } else if (periodMatcher.group(4) != null && periodMatcher.group(5) != null) {
                                // 类型3: (N-M)
                                int startPeriod = Integer.parseInt(periodMatcher.group(4));
                                int endPeriod = Integer.parseInt(periodMatcher.group(5));
                                
                                for (int i = startPeriod; i <= endPeriod; i++) {
                                    periods.add(i);
                                }
                            } else if (periodMatcher.group(6) != null) {
                                // 类型4: N节
                                periods.add(Integer.parseInt(periodMatcher.group(6)));
                            }
                        } else {
                            // 如果找不到节次信息，查找单个数字
                            Pattern singleNumberPattern = Pattern.compile("(\\d+)");
                            Matcher singleNumberMatcher = singleNumberPattern.matcher(timePart);
                            
                            while (singleNumberMatcher.find()) {
                                periods.add(Integer.parseInt(singleNumberMatcher.group(1)));
                            }
                        }
                        
                        // 如果找到了有效节次
                        if (!periods.isEmpty()) {
                            // 合并同一天的多个节次
                            if (schedule.containsKey(day)) {
                                schedule.get(day).addAll(periods);
                            } else {
                                schedule.put(day, periods);
                            }
                        } else {
                            log.warn("无法解析课程节次信息: {}", timePart);
                        }
                    } else {
                        log.warn("无法解析课程日期信息: {}", timePart);
                    }
                }
            } catch (Exception e) {
                log.error("解析课程时间出错: {}", timePart, e);
            }
        }
        
        return schedule;
    }
} 