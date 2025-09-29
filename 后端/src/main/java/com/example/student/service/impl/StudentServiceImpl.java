package com.example.student.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.common.PageResult;
import com.example.student.dto.StudentDTO;
import com.example.student.dto.StudentImportDTO;
import com.example.student.dto.StudentQueryDTO;
import com.example.student.entity.Student;
import com.example.student.entity.SysUser;
import com.example.student.entity.SysUserRole;
import com.example.student.mapper.ClassMapper;
import com.example.student.mapper.StudentMapper;
import com.example.student.mapper.SysUserMapper;
import com.example.student.mapper.SysUserRoleMapper;
import com.example.student.service.StudentService;
import com.example.student.vo.StudentAttendanceVO;
import com.example.student.vo.StudentCourseVO;
import com.example.student.vo.StudentGradeVO;
import com.example.student.vo.StudentVO;
import com.example.student.util.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生服务实现
 *
 * @author example
 */
@Service
@Slf4j
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private SysUserMapper sysUserMapper;
    
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private ClassMapper classMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public PageResult<StudentVO> pageStudent(StudentQueryDTO queryDTO) {
        // 创建分页对象
        Page<StudentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 查询数据
        IPage<StudentVO> result = baseMapper.selectStudentPage(page, 
                                                      queryDTO.getStudentNo(), 
                                                      queryDTO.getName(), 
                                                      queryDTO.getClassId(),
                                                      queryDTO.getGender(),
                                                      queryDTO.getMajorId(),
                                                      queryDTO.getDepartmentId(),
                                                      queryDTO.getPhone(), 
                                                      queryDTO.getStatus(),
                                                      queryDTO.getStartDate(),
                                                      queryDTO.getEndDate());
        // 返回结果
        return PageResult.build(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public StudentVO getStudentById(Long id) {
        return baseMapper.selectStudentById(id);
    }

    @Override
    public Long getStudentIdByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        
        // 查询学生信息 - 修复SQL查询，只选择数据库表中存在的字段
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Student::getId)  // 只查询ID字段，提高性能
               .eq(Student::getUserId, userId);
        Student student = getOne(wrapper);
        
        return student != null ? student.getId() : null;
    }

    @Override
    public boolean isCurrentStudent(Long studentId) {
        if (studentId == null) {
            return false;
        }
        
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            return false;
        }
        
        // 根据用户ID查询学生ID
        Long currentStudentId = getStudentIdByUserId(currentUserId);
        if (currentStudentId == null) {
            return false;
        }
        
        // 判断当前学生ID是否与参数中的学生ID相同
        return currentStudentId.equals(studentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStudent(StudentDTO studentDTO) {
        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(studentDTO.getUsername());
        user.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        user.setName(studentDTO.getName());
        user.setEmail(studentDTO.getEmail());
        user.setPhone(studentDTO.getPhone());
        user.setStatus(0);
        user.setUserType(2); // 学生
        sysUserMapper.insert(user);

        // 创建学生
        Student student = new Student();
        student.setStudentNo(studentDTO.getStudentNo());
        student.setUserId(user.getId());
        student.setClassId(studentDTO.getClassId());
        student.setAdmissionDate(studentDTO.getAdmissionDate());
        student.setGraduationDate(studentDTO.getGraduationDate());
        student.setGender(studentDTO.getGender());
        student.setIdCard(studentDTO.getIdCard());
        student.setBirthday(studentDTO.getBirthday());
        student.setAddress(studentDTO.getAddress());
        student.setStatus(0); // 在读
        boolean result = save(student);
        
        // 分配学生角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(3L); // 学生角色ID
        sysUserRoleMapper.insert(userRole);
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudent(StudentDTO studentDTO) {
        // 查询学生
        Student student = getById(studentDTO.getId());
        if (student == null) {
            return false;
        }

        // 更新用户
        SysUser user = sysUserMapper.selectById(student.getUserId());
        if (user != null) {
            user.setName(studentDTO.getName());
            user.setEmail(studentDTO.getEmail());
            user.setPhone(studentDTO.getPhone());
            sysUserMapper.updateById(user);
        }

        // 更新学生
        student.setStudentNo(studentDTO.getStudentNo());
        student.setClassId(studentDTO.getClassId());
        student.setAdmissionDate(studentDTO.getAdmissionDate());
        student.setGraduationDate(studentDTO.getGraduationDate());
        student.setGender(studentDTO.getGender());
        student.setIdCard(studentDTO.getIdCard());
        student.setBirthday(studentDTO.getBirthday());
        student.setAddress(studentDTO.getAddress());
        student.setStatus(studentDTO.getStatus());
        return updateById(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudent(Long id) {
        // 查询学生
        Student student = getById(id);
        if (student == null) {
            return false;
        }

        // 删除用户
        sysUserMapper.deleteById(student.getUserId());

        // 删除学生
        return removeById(id);
    }
    
    @Override
    public PageResult<?> getStudentCourses(Long studentId, Integer current, Integer size, Integer status) {
        // 验证学生是否存在
        Student student = getById(studentId);
        if (student == null) {
            return PageResult.empty();
        }
        
        // 创建分页对象
        Page<StudentCourseVO> page = new Page<>(current, size);
        
        // 查询数据
        IPage<StudentCourseVO> result = baseMapper.selectStudentCoursePage(page, studentId, status);
        
        // 处理数据
        List<StudentCourseVO> records = result.getRecords();
        for (StudentCourseVO record : records) {
            // 处理课程类型描述
            record.setCourseTypeDesc(record.getCourseType() == 0 ? "必修" : "选修");
            
            // 处理选课状态描述
            String statusDesc;
            switch (record.getStatus()) {
                case 0:
                    statusDesc = "已选";
                    break;
                case 1:
                    statusDesc = "已退";
                    break;
                case 2:
                    statusDesc = "已修完";
                    break;
                default:
                    statusDesc = "未知";
            }
            record.setStatusDesc(statusDesc);
        }
        
        return PageResult.build(records, result.getTotal(), current, size);
    }
    
    @Override
    public PageResult<?> getStudentGrades(Long studentId, Integer current, Integer size, String semester) {
        // 验证学生是否存在
        Student student = getById(studentId);
        if (student == null) {
            return PageResult.empty();
        }
        
        // 创建分页对象
        Page<StudentGradeVO> page = new Page<>(current, size);
        
        // 查询数据
        IPage<StudentGradeVO> result = baseMapper.selectStudentGradePage(page, studentId, semester);
        
        // 处理数据
        List<StudentGradeVO> records = result.getRecords();
        for (StudentGradeVO record : records) {
            // 处理课程类型描述
            record.setCourseTypeDesc(record.getCourseType() == 0 ? "必修" : "选修");
            
            // 处理是否通过
            if (record.getScore() != null) {
                record.setIsPassed(record.getScore().doubleValue() >= 60 ? 1 : 0);
                record.setIsPassedDesc(record.getIsPassed() == 1 ? "通过" : "未通过");
                
                // 计算绩点
                double score = record.getScore().doubleValue();
                double gpa;
                if (score >= 90) {
                    gpa = 4.0;
                } else if (score >= 85) {
                    gpa = 3.7;
                } else if (score >= 80) {
                    gpa = 3.3;
                } else if (score >= 75) {
                    gpa = 3.0;
                } else if (score >= 70) {
                    gpa = 2.7;
                } else if (score >= 65) {
                    gpa = 2.3;
                } else if (score >= 60) {
                    gpa = 2.0;
                } else {
                    gpa = 0.0;
                }
                record.setGpa(java.math.BigDecimal.valueOf(gpa));
            } else {
                record.setIsPassed(0);
                record.setIsPassedDesc("未评分");
            }
        }
        
        return PageResult.build(records, result.getTotal(), current, size);
    }
    
    @Override
    public PageResult<?> getStudentAttendance(Long studentId, Integer current, Integer size, String startDate, String endDate, Integer status) {
        // 验证学生是否存在
        Student student = getById(studentId);
        if (student == null) {
            return PageResult.empty();
        }
        
        // 创建分页对象
        Page<StudentAttendanceVO> page = new Page<>(current, size);
        
        // 处理日期
        LocalDate start = null;
        LocalDate end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (StringUtils.hasText(startDate)) {
            start = LocalDate.parse(startDate, formatter);
        }
        
        if (StringUtils.hasText(endDate)) {
            end = LocalDate.parse(endDate, formatter);
        }
        
        // 查询数据
        IPage<StudentAttendanceVO> result = baseMapper.selectStudentAttendancePage(page, studentId, start, end, status);
        
        // 处理数据
        List<StudentAttendanceVO> records = result.getRecords();
        for (StudentAttendanceVO record : records) {
            // 处理考勤状态描述
            String statusDesc;
            switch (record.getStatus()) {
                case 0:
                    statusDesc = "出勤";
                    break;
                case 1:
                    statusDesc = "缺勤";
                    break;
                case 2:
                    statusDesc = "迟到";
                    break;
                case 3:
                    statusDesc = "早退";
                    break;
                case 4:
                    statusDesc = "请假";
                    break;
                default:
                    statusDesc = "未知";
            }
            record.setStatusDesc(statusDesc);
        }
        
        return PageResult.build(records, result.getTotal(), current, size);
    }
    
    @Override
    public Map<String, Object> getStudentStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 总学生数
            Long totalCount = count();
            result.put("totalCount", totalCount);
            
            // 各状态学生数
            Map<String, Object> statusDistribution = baseMapper.selectStatusDistribution();
            // 转换键名以匹配前端期望的格式
            Map<String, Object> formattedStatusDistribution = new HashMap<>();
            if (statusDistribution.containsKey("studyingCount")) {
                formattedStatusDistribution.put("inSchool", statusDistribution.get("studyingCount"));
            }
            if (statusDistribution.containsKey("graduatedCount")) {
                formattedStatusDistribution.put("graduated", statusDistribution.get("graduatedCount"));
            }
            if (statusDistribution.containsKey("suspendedCount")) {
                formattedStatusDistribution.put("leave", statusDistribution.get("suspendedCount"));
            }
            if (statusDistribution.containsKey("droppedCount")) {
                formattedStatusDistribution.put("dropout", statusDistribution.get("droppedCount"));
            }
            result.put("statusDistribution", formattedStatusDistribution);
            
            // 性别分布
            Map<String, Object> genderDistribution = baseMapper.selectGenderDistribution();
            result.put("genderDistribution", genderDistribution);
            
            // 院系分布
            List<Map<String, Object>> departmentDistribution = baseMapper.selectDepartmentDistribution();
            // 转换院系分布数据格式
            List<Map<String, Object>> formattedDepartmentDistribution = departmentDistribution.stream()
                .map(item -> {
                    Map<String, Object> formatted = new HashMap<>();
                    if (item.containsKey("departmentName")) {
                        formatted.put("name", item.get("departmentName"));
                    }
                    if (item.containsKey("studentCount")) {
                        formatted.put("count", item.get("studentCount"));
                    }
                    return formatted;
                })
                .collect(Collectors.toList());
            result.put("departmentDistribution", formattedDepartmentDistribution);
            
            // 班级分布
            List<Map<String, Object>> classDistribution = baseMapper.selectClassDistribution();
            result.put("classDistribution", classDistribution);
            
            // 年级分布
            List<Map<String, Object>> gradeDistribution = baseMapper.selectGradeDistribution();
            result.put("gradeDistribution", gradeDistribution);
        } catch (Exception e) {
            log.error("获取学生统计数据异常", e);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importStudent(MultipartFile file) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<StudentImportDTO> successList = new ArrayList<>();
        List<StudentImportDTO> failList = new ArrayList<>();
        
        try {
            // 读取Excel文件
            EasyExcel.read(file.getInputStream(), StudentImportDTO.class, new PageReadListener<StudentImportDTO>(dataList -> {
                for (StudentImportDTO importDTO : dataList) {
                    try {
                        // 数据校验
                        if (!StringUtils.hasText(importDTO.getStudentNo())) {
                            importDTO.setErrorMsg("学号不能为空");
                            failList.add(importDTO);
                            continue;
                        }
                        
                        if (!StringUtils.hasText(importDTO.getName())) {
                            importDTO.setErrorMsg("姓名不能为空");
                            failList.add(importDTO);
                            continue;
                        }
                        
                        if (!StringUtils.hasText(importDTO.getClassName()) && !StringUtils.hasText(importDTO.getClassCode())) {
                            importDTO.setErrorMsg("班级名称和班级编码不能同时为空");
                            failList.add(importDTO);
                            continue;
                        }
                        
                        // 检查学号是否已存在
                        Student existingStudent = baseMapper.selectByStudentNo(importDTO.getStudentNo());
                        if (existingStudent != null) {
                            importDTO.setErrorMsg("学号已存在");
                            failList.add(importDTO);
                            continue;
                        }
                        
                        // 检查身份证号是否已存在
                        if (StringUtils.hasText(importDTO.getIdCard())) {
                            existingStudent = baseMapper.selectByIdCard(importDTO.getIdCard());
                            if (existingStudent != null) {
                                importDTO.setErrorMsg("身份证号已存在");
                                failList.add(importDTO);
                                continue;
                            }
                        }
                        
                        // 查询班级ID
                        Long classId = null;
                        if (StringUtils.hasText(importDTO.getClassCode())) {
                            com.example.student.entity.Class clazz = classMapper.selectByCode(importDTO.getClassCode());
                            if (clazz != null) {
                                classId = clazz.getId();
                            }
                        } else if (StringUtils.hasText(importDTO.getClassName())) {
                            com.example.student.entity.Class clazz = classMapper.selectByName(importDTO.getClassName());
                            if (clazz != null) {
                                classId = clazz.getId();
                            }
                        }
                        
                        if (classId == null) {
                            importDTO.setErrorMsg("班级不存在");
                            failList.add(importDTO);
                            continue;
                        }
                        
                        // 处理日期
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate admissionDate = null;
                        LocalDate graduationDate = null;
                        LocalDate birthday = null;
                        
                        if (StringUtils.hasText(importDTO.getAdmissionDate())) {
                            try {
                                admissionDate = LocalDate.parse(importDTO.getAdmissionDate(), formatter);
                            } catch (Exception e) {
                                importDTO.setErrorMsg("入学日期格式错误，正确格式：yyyy-MM-dd");
                                failList.add(importDTO);
                                continue;
                            }
                        } else {
                            importDTO.setErrorMsg("入学日期不能为空");
                            failList.add(importDTO);
                            continue;
                        }
                        
                        if (StringUtils.hasText(importDTO.getGraduationDate())) {
                            try {
                                graduationDate = LocalDate.parse(importDTO.getGraduationDate(), formatter);
                            } catch (Exception e) {
                                importDTO.setErrorMsg("毕业日期格式错误，正确格式：yyyy-MM-dd");
                                failList.add(importDTO);
                                continue;
                            }
                        }
                        
                        if (StringUtils.hasText(importDTO.getBirthday())) {
                            try {
                                birthday = LocalDate.parse(importDTO.getBirthday(), formatter);
                            } catch (Exception e) {
                                importDTO.setErrorMsg("出生日期格式错误，正确格式：yyyy-MM-dd");
                                failList.add(importDTO);
                                continue;
                            }
                        }
                        
                        // 处理性别
                        Integer gender = null;
                        if (StringUtils.hasText(importDTO.getGender())) {
                            if ("男".equals(importDTO.getGender())) {
                                gender = 0;
                            } else if ("女".equals(importDTO.getGender())) {
                                gender = 1;
                            } else {
                                importDTO.setErrorMsg("性别只能是'男'或'女'");
                                failList.add(importDTO);
                                continue;
                            }
                        } else {
                            importDTO.setErrorMsg("性别不能为空");
                            failList.add(importDTO);
                            continue;
                        }
                        
                        // 处理状态
                        Integer status = 0; // 默认在读
                        if (StringUtils.hasText(importDTO.getStatus())) {
                            switch (importDTO.getStatus()) {
                                case "在读":
                                    status = 0;
                                    break;
                                case "毕业":
                                    status = 1;
                                    break;
                                case "休学":
                                    status = 2;
                                    break;
                                case "退学":
                                    status = 3;
                                    break;
                                default:
                                    importDTO.setErrorMsg("状态只能是'在读'、'毕业'、'休学'或'退学'");
                                    failList.add(importDTO);
                                    continue;
                            }
                        }
                        
                        // 创建用户
                        SysUser user = new SysUser();
                        // 默认用户名为学号
                        user.setUsername(importDTO.getStudentNo());
                        // 默认密码为123456
                        user.setPassword(passwordEncoder.encode("123456"));
                        user.setName(importDTO.getName());
                        user.setEmail(importDTO.getEmail());
                        user.setPhone(importDTO.getPhone());
                        user.setStatus(0);
                        user.setUserType(2); // 学生
                        sysUserMapper.insert(user);
                        
                        // 创建学生
                        Student student = new Student();
                        student.setStudentNo(importDTO.getStudentNo());
                        student.setUserId(user.getId());
                        student.setClassId(classId);
                        student.setAdmissionDate(admissionDate);
                        student.setGraduationDate(graduationDate);
                        student.setGender(gender);
                        student.setIdCard(importDTO.getIdCard());
                        student.setBirthday(birthday);
                        student.setAddress(importDTO.getAddress());
                        student.setStatus(status);
                        save(student);
                        
                        // 添加到成功列表
                        successList.add(importDTO);
                    } catch (Exception e) {
                        log.error("导入学生数据异常", e);
                        importDTO.setErrorMsg("导入异常：" + e.getMessage());
                        failList.add(importDTO);
                    }
                }
            })).sheet().doRead();
            
            result.put("total", successList.size() + failList.size());
            result.put("success", successList.size());
            result.put("fail", failList.size());
            result.put("failList", failList);
        } catch (IOException e) {
            log.error("读取Excel文件异常", e);
            throw e;
        }
        
        return result;
    }
    
    @Override
    public void exportStudent(StudentQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        try {
            // 创建查询对象
            LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("学生信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            // 查询学生数据
            List<StudentVO> studentList = baseMapper.selectStudentPage(
                    new Page<>(1, 10000), 
                    queryDTO.getStudentNo(), 
                    queryDTO.getName(), 
                    queryDTO.getClassId(), 
                    queryDTO.getGender(),
                    queryDTO.getMajorId(),
                    queryDTO.getDepartmentId(),
                    queryDTO.getPhone(),
                    queryDTO.getStatus(),
                    queryDTO.getStartDate(),
                    queryDTO.getEndDate()
            ).getRecords();
            
            // 转换为导出对象
            List<StudentImportDTO> exportList = new ArrayList<>();
            for (StudentVO student : studentList) {
                StudentImportDTO exportDTO = new StudentImportDTO();
                exportDTO.setStudentNo(student.getStudentNo());
                exportDTO.setName(student.getName());
                exportDTO.setGender(student.getGender() == 0 ? "男" : "女");
                exportDTO.setClassName(student.getClassName());
                exportDTO.setAdmissionDate(student.getAdmissionDate() != null ? student.getAdmissionDate().toString() : null);
                exportDTO.setGraduationDate(student.getGraduationDate() != null ? student.getGraduationDate().toString() : null);
                exportDTO.setIdCard(student.getIdCard());
                exportDTO.setBirthday(student.getBirthday() != null ? student.getBirthday().toString() : null);
                exportDTO.setAddress(student.getAddress());
                exportDTO.setEmail(student.getEmail());
                exportDTO.setPhone(student.getPhone());
                
                // 处理状态
                String status;
                switch (student.getStatus()) {
                    case 0:
                        status = "在读";
                        break;
                    case 1:
                        status = "毕业";
                        break;
                    case 2:
                        status = "休学";
                        break;
                    case 3:
                        status = "退学";
                        break;
                    default:
                        status = "未知";
                }
                exportDTO.setStatus(status);
                
                exportList.add(exportDTO);
            }
            
            // 写入响应
            EasyExcel.write(response.getOutputStream(), StudentImportDTO.class).sheet("学生信息").doWrite(exportList);
        } catch (Exception e) {
            log.error("导出学生信息异常", e);
            throw e;
        }
    }
    
    @Override
    public void downloadStudentTemplate(HttpServletResponse response) throws Exception {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("学生信息导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            // 创建模板数据
            List<StudentImportDTO> templateList = new ArrayList<>();
            StudentImportDTO template = new StudentImportDTO();
            template.setStudentNo("S202301001");
            template.setName("张三");
            template.setGender("男");
            template.setClassName("计算机科学2023-1班");
            template.setClassCode("CS2301");
            template.setAdmissionDate("2023-09-01");
            template.setGraduationDate("2027-06-30");
            template.setIdCard("110101200301010033");
            template.setBirthday("2003-01-01");
            template.setAddress("北京市海淀区");
            template.setEmail("zhangsan@example.com");
            template.setPhone("13800000001");
            template.setStatus("在读");
            templateList.add(template);
            
            // 写入响应
            EasyExcel.write(response.getOutputStream(), StudentImportDTO.class).sheet("学生信息").doWrite(templateList);
        } catch (Exception e) {
            log.error("下载学生信息导入模板异常", e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchStudentStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty() || status == null) {
            return false;
        }
        
        try {
            // 使用批量更新
            LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(Student::getId, ids)
                         .set(Student::getStatus, status);
            
            // 记录操作日志
            log.info("批量更新学生状态, IDs: {}, 状态: {}", ids, status);
            
            // 执行更新
            return update(updateWrapper);
        } catch (Exception e) {
            log.error("批量更新学生状态失败", e);
            return false;
        }
    }
    
    @Override
    public PageResult<?> getCurrentUserCourses(Integer pageNum, Integer pageSize, String courseName, String courseCode, String semester) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            log.error("获取当前登录用户ID失败");
            return PageResult.empty();
        }
        
        try {
            // 创建分页对象
            Page<StudentCourseVO> page = new Page<>(pageNum, pageSize);
            
            // 查询数据
            IPage<StudentCourseVO> result = baseMapper.selectUserCourses(page, userId, courseName, courseCode, semester);
            
            // 处理数据
            List<StudentCourseVO> records = result.getRecords();
            for (StudentCourseVO record : records) {
                // 处理课程类型描述
                record.setCourseTypeDesc(record.getCourseType() == 0 ? "必修" : "选修");
                
                // 处理选课状态描述
                String statusDesc;
                switch (record.getStatus()) {
                    case 0:
                        statusDesc = "已选";
                        break;
                    case 1:
                        statusDesc = "已退";
                        break;
                    case 2:
                        statusDesc = "已修完";
                        break;
                    default:
                        statusDesc = "未知";
                }
                record.setStatusDesc(statusDesc);
            }
            
            return PageResult.build(records, result.getTotal(), pageNum, pageSize);
        } catch (Exception e) {
            log.error("查询当前用户课程失败", e);
            return PageResult.empty();
        }
    }
} 