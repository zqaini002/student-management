package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.entity.Attendance;
import com.example.student.entity.CourseOffering;
import com.example.student.entity.Student;
import com.example.student.entity.SysUser;
import com.example.student.mapper.AttendanceMapper;
import com.example.student.mapper.CourseOfferingMapper;
import com.example.student.mapper.StudentMapper;
import com.example.student.mapper.SysUserMapper;
import com.example.student.service.AttendanceService;
import com.example.student.vo.AttendanceStatisticsVO;
import com.example.student.vo.AttendanceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 考勤服务实现类
 *
 * @author example
 */
@Service
@Slf4j
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private CourseOfferingMapper courseOfferingMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public Page<AttendanceVO> listAttendanceRecords(Page<Attendance> page, Map<String, Object> params) {
        LambdaQueryWrapper<Attendance> queryWrapper = Wrappers.lambdaQuery();
        
        // 学生姓名查询
        String studentName = (String) params.get("studentName");
        if (StringUtils.hasText(studentName)) {
            // 1. 通过姓名查询用户表获取用户ID
            List<Long> userIds = sysUserMapper.selectList(
                    Wrappers.<SysUser>lambdaQuery()
                            .like(SysUser::getName, studentName)
                            // 移除用户类型限制，查询所有包含该姓名的用户
            ).stream().map(SysUser::getId).collect(Collectors.toList());
            
            System.out.println("按姓名[" + studentName + "]查询到的用户ID: " + userIds);
            
            if (!userIds.isEmpty()) {
                // 2. 通过用户ID查询学生表获取学生ID
                List<Long> studentIds = studentMapper.selectList(
                        Wrappers.<Student>lambdaQuery()
                                .in(Student::getUserId, userIds)
                ).stream().map(Student::getId).collect(Collectors.toList());
                
                System.out.println("关联查询到的学生ID: " + studentIds);
                
                if (!studentIds.isEmpty()) {
                    queryWrapper.in(Attendance::getStudentId, studentIds);
                } else {
                    // 没有找到匹配的学生，返回空结果
                    System.out.println("未找到匹配的学生，返回空结果");
                    return new Page<>(page.getCurrent(), page.getSize());
                }
            } else {
                // 没有找到匹配的用户，返回空结果
                System.out.println("未找到匹配的用户，返回空结果");
                return new Page<>(page.getCurrent(), page.getSize());
            }
        }
        
        // 学号查询
        String studentNo = (String) params.get("studentNo");
        if (StringUtils.hasText(studentNo)) {
            List<Long> studentIds = studentMapper.selectList(
                    Wrappers.<Student>lambdaQuery().like(Student::getStudentNo, studentNo)
            ).stream().map(Student::getId).collect(Collectors.toList());
            
            if (!studentIds.isEmpty()) {
                queryWrapper.in(Attendance::getStudentId, studentIds);
            } else {
                // 没有找到匹配的学生，返回空结果
                return new Page<>(page.getCurrent(), page.getSize());
            }
        }
        
        // 课程ID查询
        Object courseIdObj = params.get("courseId");
        if (courseIdObj != null) {
            try {
                Long courseId;
                if (courseIdObj instanceof Integer) {
                    courseId = ((Integer) courseIdObj).longValue();
                } else if (courseIdObj instanceof Long) {
                    courseId = (Long) courseIdObj;
                } else {
                    courseId = Long.parseLong(courseIdObj.toString());
                }
                
                log.info("按课程ID查询考勤记录: {}", courseId);
                List<Long> courseOfferingIds = courseOfferingMapper.selectList(
                        Wrappers.<CourseOffering>lambdaQuery()
                                .eq(CourseOffering::getCourseId, courseId)
                ).stream().map(CourseOffering::getId).collect(Collectors.toList());
                
                if (!courseOfferingIds.isEmpty()) {
                    queryWrapper.in(Attendance::getCourseOfferingId, courseOfferingIds);
                } else {
                    // 没有找到匹配的课程开设记录，返回空结果
                    return new Page<>(page.getCurrent(), page.getSize());
                }
            } catch (Exception e) {
                log.error("处理课程ID参数异常", e);
                // 异常处理，返回空结果
                return new Page<>(page.getCurrent(), page.getSize());
            }
        }
        
        // 课程名称查询
        String courseName = (String) params.get("courseName");
        if (StringUtils.hasText(courseName)) {
            try {
                List<Long> courseOfferingIds = courseOfferingMapper.selectCourseOfferingsByCourseName(courseName);
                
                if (!courseOfferingIds.isEmpty()) {
                    queryWrapper.in(Attendance::getCourseOfferingId, courseOfferingIds);
                } else {
                    // 没有找到匹配的课程，返回空结果
                    return new Page<>(page.getCurrent(), page.getSize());
                }
            } catch (Exception e) {
                // 异常处理，返回空结果
                return new Page<>(page.getCurrent(), page.getSize());
            }
        }
        
        // 日期范围查询
        String startDate = (String) params.get("startDate");
        if (StringUtils.hasText(startDate)) {
            queryWrapper.ge(Attendance::getAttendanceDate, LocalDate.parse(startDate));
        }
        
        String endDate = (String) params.get("endDate");
        if (StringUtils.hasText(endDate)) {
            queryWrapper.le(Attendance::getAttendanceDate, LocalDate.parse(endDate));
        }
        
        // 考勤状态查询
        Object status = params.get("status");
        if (status != null) {
            queryWrapper.eq(Attendance::getStatus, status);
        }
        
        // 按日期降序排序
        queryWrapper.orderByDesc(Attendance::getAttendanceDate);
        
        // 分页查询
        Page<Attendance> attendancePage = this.page(page, queryWrapper);
        
        // 转换为VO
        Page<AttendanceVO> resultPage = convertToVoPage(attendancePage);
        
        return resultPage;
    }
    
    @Override
    public Page<AttendanceVO> getStudentAttendance(Long studentId, Page<Attendance> page, Map<String, Object> params) {
        LambdaQueryWrapper<Attendance> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Attendance::getStudentId, studentId);
        
        // 课程名称查询
        String courseName = (String) params.get("courseName");
        if (StringUtils.hasText(courseName)) {
            try {
                List<Long> courseOfferingIds = courseOfferingMapper.selectCourseOfferingsByCourseName(courseName);
                
                if (!courseOfferingIds.isEmpty()) {
                    queryWrapper.in(Attendance::getCourseOfferingId, courseOfferingIds);
                } else {
                    // 没有找到匹配的课程，返回空结果
                    return new Page<>(page.getCurrent(), page.getSize());
                }
            } catch (Exception e) {
                // 异常处理，返回空结果
                return new Page<>(page.getCurrent(), page.getSize());
            }
        }
        
        // 日期范围查询
        String startDate = (String) params.get("startDate");
        if (StringUtils.hasText(startDate)) {
            queryWrapper.ge(Attendance::getAttendanceDate, LocalDate.parse(startDate));
        }
        
        String endDate = (String) params.get("endDate");
        if (StringUtils.hasText(endDate)) {
            queryWrapper.le(Attendance::getAttendanceDate, LocalDate.parse(endDate));
        }
        
        // 考勤状态查询
        Object status = params.get("status");
        if (status != null) {
            queryWrapper.eq(Attendance::getStatus, status);
        }
        
        // 按日期降序排序
        queryWrapper.orderByDesc(Attendance::getAttendanceDate);
        
        // 分页查询
        Page<Attendance> attendancePage = this.page(page, queryWrapper);
        
        // 转换为VO
        Page<AttendanceVO> resultPage = convertToVoPage(attendancePage);
        
        return resultPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSubmitAttendance(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return false;
        }
        
        return this.saveOrUpdateBatch(attendances);
    }
    
    @Override
    public Page<AttendanceStatisticsVO> getAttendanceStatistics(Page<AttendanceStatisticsVO> page, Map<String, Object> params) {
        String type = (String) params.getOrDefault("type", "course");
        Long courseId = null;
        if (params.get("courseId") != null) {
            courseId = Long.valueOf(params.get("courseId").toString());
        }
        
        Long classId = null;
        if (params.get("classId") != null) {
            classId = Long.valueOf(params.get("classId").toString());
        }
        
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");
        
        // 从数据库获取实际统计数据
        Page<AttendanceStatisticsVO> resultPage = new Page<>(page.getCurrent(), page.getSize());
        List<AttendanceStatisticsVO> statistics;
        
        if ("course".equals(type)) {
            // 按课程统计
            statistics = baseMapper.getAttendanceStatisticsByCourse(
                    classId, 
                    startDate != null ? LocalDate.parse(startDate) : null, 
                    endDate != null ? LocalDate.parse(endDate) : null
            );
        } else if ("class".equals(type)) {
            // 按班级统计
            statistics = baseMapper.getAttendanceStatisticsByClass(
                    courseId, 
                    startDate != null ? LocalDate.parse(startDate) : null, 
                    endDate != null ? LocalDate.parse(endDate) : null
            );
        } else if ("date".equals(type)) {
            // 按日期统计
            statistics = baseMapper.getAttendanceStatisticsByDate(
                    courseId, 
                    classId, 
                    startDate != null ? LocalDate.parse(startDate) : null, 
                    endDate != null ? LocalDate.parse(endDate) : null
            );
        } else if ("status".equals(type)) {
            // 按状态统计
            statistics = baseMapper.getAttendanceStatisticsByStatus(
                    courseId, 
                    classId, 
                    startDate != null ? LocalDate.parse(startDate) : null, 
                    endDate != null ? LocalDate.parse(endDate) : null
            );
        } else {
            statistics = new ArrayList<>();
        }
        
        resultPage.setRecords(statistics);
        resultPage.setTotal(statistics.size());
        
        return resultPage;
    }
    
    @Override
    public AttendanceStatisticsVO getCourseAttendanceStatistics(Long courseId) {
        // 从数据库查询课程考勤统计信息
        if (courseId == null) {
            return new AttendanceStatisticsVO();
        }
        
        AttendanceStatisticsVO vo = baseMapper.getCourseAttendanceStatisticsById(courseId);
        if (vo == null) {
            vo = new AttendanceStatisticsVO();
            vo.setType("course");
            vo.setCourseId(courseId);
        }
        
        return vo;
    }
    
    @Override
    public AttendanceStatisticsVO getClassAttendanceStatistics(Long classId) {
        // 从数据库查询班级考勤统计信息
        if (classId == null) {
            return new AttendanceStatisticsVO();
        }
        
        AttendanceStatisticsVO vo = baseMapper.getClassAttendanceStatisticsById(classId);
        if (vo == null) {
            vo = new AttendanceStatisticsVO();
            vo.setType("class");
            vo.setClassId(classId);
        }
        
        return vo;
    }
    
    @Override
    public List<AttendanceVO> exportAttendance(Map<String, Object> params) {
        // 导出考勤记录，不分页
        Integer pageSize = 1000; // 限制导出数量
        Page<Attendance> page = new Page<>(1, pageSize);
        Page<AttendanceVO> resultPage = listAttendanceRecords(page, params);
        
        return resultPage.getRecords();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAttendanceByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        return this.removeByIds(ids);
    }
    
    /**
     * 将Attendance实体分页结果转换为VO分页结果
     */
    private Page<AttendanceVO> convertToVoPage(Page<Attendance> attendancePage) {
        Page<AttendanceVO> voPage = new Page<>(attendancePage.getCurrent(), attendancePage.getSize());
        
        // 设置分页信息
        voPage.setTotal(attendancePage.getTotal());
        voPage.setPages(attendancePage.getPages());
        voPage.setCurrent(attendancePage.getCurrent());
        voPage.setSize(attendancePage.getSize());
        
        if (attendancePage.getRecords() == null || attendancePage.getRecords().isEmpty()) {
            voPage.setRecords(new ArrayList<>());
            return voPage;
        }
        
        log.info("转换考勤记录，记录数量: {}", attendancePage.getRecords().size());
        
        // 获取学生和课程信息
        Set<Long> studentIds = attendancePage.getRecords().stream()
                .map(Attendance::getStudentId)
                .collect(Collectors.toSet());
        
        log.info("需要查询的学生ID: {}", studentIds);
        
        // 从数据库查询学生记录
        final Map<Long, Student> studentMap = studentMapper.selectList(
                Wrappers.<Student>lambdaQuery().in(Student::getId, studentIds)
        ).stream().collect(Collectors.toMap(Student::getId, Function.identity(), (k1, k2) -> k1));
        
        log.info("查询到学生数量: {}", studentMap.size());
        
        // 获取关联的用户信息（包含学生姓名）
        Set<Long> userIds = studentMap.values().stream()
                .map(Student::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        log.info("需要查询的用户ID: {}", userIds);
        
        // 查询用户信息，获取真实姓名
        final Map<Long, String> userNameMap = !userIds.isEmpty() ? 
                getUserNamesById(userIds) : new HashMap<>();
        
        log.info("查询到用户姓名数量: {}", userNameMap.size());
        
        // 获取课程开设记录信息
        Set<Long> courseOfferingIds = attendancePage.getRecords().stream()
                .map(Attendance::getCourseOfferingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.info("需要查询的课程开设ID: {}", courseOfferingIds);

        // 查询课程开设记录信息
        final Map<Long, CourseOffering> courseOfferingMap = new HashMap<>();
        if (!courseOfferingIds.isEmpty()) {
            try {
                List<CourseOffering> courseOfferings = courseOfferingMapper.selectList(
                Wrappers.<CourseOffering>lambdaQuery().in(CourseOffering::getId, courseOfferingIds)
                );
                courseOfferingMap.putAll(courseOfferings.stream()
                        .collect(Collectors.toMap(CourseOffering::getId, Function.identity(), (k1, k2) -> k1)));
                log.info("查询到课程开设记录数量: {}", courseOfferingMap.size());
            } catch (Exception e) {
                log.error("查询课程开设记录异常", e);
            }
        }

        // 获取课程ID集合，用于查询课程基本信息
        Set<Long> courseIds = courseOfferingMap.values().stream()
                .map(CourseOffering::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.info("需要查询的课程ID: {}", courseIds);

        // 获取班级ID集合
        Set<Long> classIds = studentMap.values().stream()
                .map(Student::getClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        log.info("从学生信息获取到的班级ID: {}", classIds);

        // 课程开设ID到课程和班级信息的映射
        final Map<Long, Map<String, Object>> courseOfferingInfoMap = new HashMap<>();
        for (Long courseOfferingId : courseOfferingIds) {
            try {
                Map<String, Object> info = courseOfferingMapper.getCourseAndClassInfo(courseOfferingId);
                if (info != null) {
                    courseOfferingInfoMap.put(courseOfferingId, info);
                    // 如果获取到了班级ID，添加到班级ID集合中
                    if (info.get("class_id") != null) {
                        classIds.add(Long.valueOf(info.get("class_id").toString()));
                    }
                }
            } catch (Exception e) {
                log.error("查询课程开设对应课程和班级信息异常, courseOfferingId: {}", courseOfferingId, e);
            }
        }

        log.info("查询到课程开设对应课程和班级信息数量: {}", courseOfferingInfoMap.size());

        // 查询课程基本信息
        final Map<Long, com.example.student.entity.Course> courseMap = new HashMap<>();
        if (!courseIds.isEmpty()) {
            try {
            List<com.example.student.entity.Course> courses = baseMapper.selectCoursesByIds(courseIds);
                courseMap.putAll(courses.stream()
                        .collect(Collectors.toMap(com.example.student.entity.Course::getId, Function.identity(), (k1, k2) -> k1)));
                log.info("查询到课程数量: {}", courseMap.size());
            } catch (Exception e) {
                log.error("查询课程信息异常", e);
            }
        }
        
        // 查询班级信息
        final Map<Long, com.example.student.entity.Class> classMap = new HashMap<>();
        if (!classIds.isEmpty()) {
            try {
                List<com.example.student.entity.Class> classes = baseMapper.getClassesByIds(classIds);
                if (classes != null) {
                    classMap.putAll(classes.stream()
                            .collect(Collectors.toMap(com.example.student.entity.Class::getId, Function.identity(), (k1, k2) -> k1)));
                    log.info("查询到班级数量: {}", classMap.size());
                }
            } catch (Exception e) {
                log.error("查询班级信息异常", e);
            }
        }

        // 在处理考勤记录时使用真实的课程信息和班级信息
        List<AttendanceVO> voList = attendancePage.getRecords().stream().map(attendance -> {
            AttendanceVO vo = new AttendanceVO();
            BeanUtils.copyProperties(attendance, vo);
            
            // 设置学生信息
            Student student = studentMap.get(attendance.getStudentId());
            if (student != null) {
                vo.setStudentNo(student.getStudentNo());
                
                // 获取真实姓名
                Long userId = student.getUserId();
                String realName = userNameMap.get(userId);
                vo.setStudentName(realName != null ? realName : "未知学生");
                
                // 如果学生有班级ID，直接设置班级信息
                if (student.getClassId() != null) {
                    com.example.student.entity.Class studentClass = classMap.get(student.getClassId());
                    if (studentClass != null) {
                        vo.setClassName(studentClass.getName());
                    } else {
                        vo.setClassName("未找到班级信息");
                    }
                }
            } else {
                vo.setStudentNo("");
                vo.setStudentName("未知学生");
            }
            
            // 获取课程开设对应的课程和班级信息
            Map<String, Object> courseInfo = courseOfferingInfoMap.get(attendance.getCourseOfferingId());
            if (courseInfo != null) {
            // 设置课程信息
                vo.setCourseName(courseInfo.get("course_name") != null ? courseInfo.get("course_name").toString() : "未知课程");
                vo.setCourseCode(courseInfo.get("course_code") != null ? courseInfo.get("course_code").toString() : "");
                
                // 设置班级信息（如果从学生信息没有获取到）
                if ((vo.getClassName() == null || vo.getClassName().isEmpty()) && courseInfo.get("class_name") != null) {
                    vo.setClassName(courseInfo.get("class_name").toString());
                }
            } else {
                // 备用方式：从课程开设和课程表获取信息
            CourseOffering courseOffering = courseOfferingMap.get(attendance.getCourseOfferingId());
            if (courseOffering != null) {
                // 获取课程基本信息
                com.example.student.entity.Course course = courseMap.get(courseOffering.getCourseId());
                if (course != null) {
                    vo.setCourseName(course.getName());
                    vo.setCourseCode(course.getCode());
                } else {
                    vo.setCourseName("未知课程");
                    vo.setCourseCode("");
                }
                
                    // 设置学期信息
                    vo.setSemester(courseOffering.getSemester());
                } else {
                    // 处理新添加的特殊课程开设ID情况
                    if (attendance.getCourseOfferingId() != null && attendance.getCourseOfferingId() == 1000L) {
                        log.info("处理特殊课程开设ID: 1000");
                        // 硬编码课程信息
                        vo.setCourseName("临时课程");
                        vo.setCourseCode("TEMP001");
                        vo.setSemester("当前学期");
                    } else {
                vo.setCourseName("未知课程");
                vo.setCourseCode("");
                        vo.setSemester("");
                    }
                }
            }
            
            // 确保班级信息有值
            if (vo.getClassName() == null || vo.getClassName().isEmpty()) {
                vo.setClassName("未分配班级");
            }
            
            // 直接设置状态描述，不调用VO的方法
            vo.setStatusDesc(getStatusDesc(attendance.getStatus()));
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        log.info("考勤记录转换完成，转换后记录数: {}", voList.size());
        return voPage;
    }
    
    /**
     * 根据用户ID获取用户姓名
     */
    private Map<Long, String> getUserNamesById(Set<Long> userIds) {
        // 从数据库查询用户姓名
        Map<Long, String> userNameMap = new HashMap<>();
        if (userIds.isEmpty()) {
            return userNameMap;
        }
        
        // 批量查询用户信息
        List<SysUser> userList = sysUserMapper.selectList(
                Wrappers.<SysUser>lambdaQuery().in(SysUser::getId, userIds)
        );
        
        // 使用for循环代替stream.collect，避免lambda表达式中的变量引用问题
        for (SysUser user : userList) {
            userNameMap.put(user.getId(), user.getName());
        }
        
        return userNameMap;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 0: return "出勤";
            case 1: return "缺勤";
            case 2: return "迟到";
            case 3: return "早退";
            case 4: return "请假";
            default: return "未知";
        }
    }
} 