package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.common.PageResult;
import com.example.student.common.exception.BusinessException;
import com.example.student.dto.TeacherDTO;
import com.example.student.dto.TeacherImportDTO;
import com.example.student.dto.TeacherQueryDTO;
import com.example.student.entity.Attendance;
import com.example.student.entity.CourseSelection;
import com.example.student.entity.SysUser;
import com.example.student.entity.SysUserRole;
import com.example.student.entity.Teacher;
import com.example.student.mapper.AttendanceMapper;
import com.example.student.mapper.CourseSelectionMapper;
import com.example.student.mapper.SysUserMapper;
import com.example.student.mapper.SysUserRoleMapper;
import com.example.student.mapper.TeacherMapper;
import com.example.student.service.TeacherService;
import com.example.student.util.ExcelUtils;
import com.example.student.vo.TeacherCourseStudentVO;
import com.example.student.vo.TeacherCourseVO;
import com.example.student.vo.TeacherVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;

/**
 * 教师服务实现类
 *
 * @author example
 */
@Slf4j
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private CourseSelectionMapper courseSelectionMapper;

    @Resource
    private AttendanceMapper attendanceMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    // 默认密码
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public PageResult<TeacherVO> pageTeacher(TeacherQueryDTO queryDTO) {
        // 创建分页对象
        Page<TeacherVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 查询数据
        IPage<TeacherVO> result = teacherMapper.selectTeacherPage(page,
                queryDTO.getTeacherNo(),
                queryDTO.getName(),
                queryDTO.getDepartmentId(),
                queryDTO.getTitle(),
                queryDTO.getStatus(),
                queryDTO.getEmail(),
                queryDTO.getPhone(),
                queryDTO.getBirthdayStart(),
                queryDTO.getBirthdayEnd(),
                queryDTO.getEntryDateStart(),
                queryDTO.getEntryDateEnd());
        // 返回结果
        return PageResult.build(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public TeacherVO getTeacherById(Long id) {
        return teacherMapper.selectTeacherById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTeacher(TeacherDTO teacherDTO) {
        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(teacherDTO.getUsername());
        user.setPassword(passwordEncoder.encode(teacherDTO.getPassword() != null ? teacherDTO.getPassword() : DEFAULT_PASSWORD));
        user.setName(teacherDTO.getName());
        user.setEmail(teacherDTO.getEmail());
        user.setPhone(teacherDTO.getPhone());
        user.setAvatar(teacherDTO.getAvatar());
        user.setStatus(0); // 正常状态
        user.setUserType(1); // 教师类型
        sysUserMapper.insert(user);

        // 创建教师
        Teacher teacher = new Teacher();
        teacher.setTeacherNo(teacherDTO.getTeacherNo());
        teacher.setUserId(user.getId());
        teacher.setDepartmentId(teacherDTO.getDepartmentId());
        teacher.setTitle(teacherDTO.getTitle());
        teacher.setGender(teacherDTO.getGender());
        teacher.setIdCard(teacherDTO.getIdCard());
        teacher.setBirthday(teacherDTO.getBirthday());
        teacher.setEntryDate(teacherDTO.getEntryDate());
        teacher.setStatus(teacherDTO.getStatus() != null ? teacherDTO.getStatus() : 0);
        boolean result = save(teacher);

        // 分配教师角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2L); // 教师角色ID
        sysUserRoleMapper.insert(userRole);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeacher(TeacherDTO teacherDTO) {
        // 查询教师
        Teacher teacher = getById(teacherDTO.getId());
        if (teacher == null) {
            return false;
        }

        // 更新教师信息
        teacher.setDepartmentId(teacherDTO.getDepartmentId());
        teacher.setTitle(teacherDTO.getTitle());
        teacher.setGender(teacherDTO.getGender());
        teacher.setIdCard(teacherDTO.getIdCard());
        teacher.setBirthday(teacherDTO.getBirthday());
        teacher.setEntryDate(teacherDTO.getEntryDate());
        teacher.setStatus(teacherDTO.getStatus());
        updateById(teacher);

        // 更新用户信息
        SysUser user = sysUserMapper.selectById(teacher.getUserId());
        if (user != null) {
            user.setName(teacherDTO.getName());
            user.setEmail(teacherDTO.getEmail());
            user.setPhone(teacherDTO.getPhone());
            user.setAvatar(teacherDTO.getAvatar());

            // 如果密码不为空，则更新密码
            if (teacherDTO.getPassword() != null && !teacherDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(teacherDTO.getPassword()));
            }

            sysUserMapper.updateById(user);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeacher(Long id) {
        // 查询教师
        Teacher teacher = getById(id);
        if (teacher == null) {
            return false;
        }

        // 删除教师
        removeById(id);

        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, teacher.getUserId());
        sysUserRoleMapper.delete(queryWrapper);

        // 删除用户
        sysUserMapper.deleteById(teacher.getUserId());

        return true;
    }

    @Override
    public boolean resetPassword(Long id) {
        // 查询教师
        Teacher teacher = getById(id);
        if (teacher == null) {
            return false;
        }

        // 重置密码
        SysUser user = new SysUser();
        user.setId(teacher.getUserId());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        // 查询教师
        Teacher teacher = getById(id);
        if (teacher == null) {
            return false;
        }

        // 更新教师状态
        teacher.setStatus(status);
        return updateById(teacher);
    }

    @Override
    public PageResult<?> getTeacherCourses(Long teacherId, TeacherQueryDTO queryDTO) {
        // 创建分页对象
        Page<TeacherCourseVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 查询数据
        IPage<TeacherCourseVO> result = teacherMapper.selectTeacherCoursePage(page,
                teacherId,
                queryDTO.getSemester(),
                queryDTO.getCourseName(),
                queryDTO.getStatus());
        // 返回结果
        return PageResult.build(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<?> getTeacherCourseStudents(Long teacherId, Long courseId, TeacherQueryDTO queryDTO) {
        // 检查权限：
        // 1. 如果teacherId为null，视为管理员访问，无需权限检查
        // 2. 如果有teacherId，检查该教师是否有权限操作这个课程
        if (teacherId != null) {
            int count = teacherMapper.checkTeacherCoursePermission(courseId, teacherId);
            if (count == 0) {
                log.warn("教师 {} 没有权限操作课程 {}", teacherId, courseId);
                throw new BusinessException("没有权限操作该课程");
            }
        } else {
            // teacherId为null且courseId为null，参数错误
            if (courseId == null) {
                log.error("参数错误: teacherId和courseId都为空");
                throw new BusinessException("参数不完整：缺少courseId");
            }
            // teacherId为null，作为管理员访问处理
            log.info("管理员用户访问课程学生列表: courseId={}", courseId);
        }

        // 创建分页对象
        Page<TeacherCourseStudentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 从查询DTO中获取studentId
        Long studentId = queryDTO.getStudentId();
        if (studentId != null) {
            log.info("查询单个学生详细考勤记录: studentId={}, courseId={}", studentId, courseId);
        }
        
        // 查询数据
        IPage<TeacherCourseStudentVO> result = teacherMapper.selectTeacherCourseStudentPage(page,
                courseId,
                queryDTO.getStudentName(),
                queryDTO.getStudentNo(),
                queryDTO.getStatus(),
                studentId);
                
        // 返回结果
        return PageResult.build(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitStudentGrade(Long teacherId, Long courseId, Map<String, Object> data) {
        // 检查教师是否有权限操作课程
        int count = teacherMapper.checkTeacherCoursePermission(teacherId, courseId);
        if (count == 0) {
            throw new BusinessException("没有权限操作该课程");
        }

        try {
            // 获取选课ID和成绩
            Long selectionId = Long.valueOf(data.get("selectionId").toString());
            BigDecimal score = new BigDecimal(data.get("score").toString());

            // 更新选课成绩
            CourseSelection courseSelection = new CourseSelection();
            courseSelection.setId(selectionId);
            courseSelection.setScore(score);
            courseSelection.setStatus(2); // 已修完

            return courseSelectionMapper.updateById(courseSelection) > 0;
        } catch (Exception e) {
            log.error("提交学生成绩失败", e);
            throw new BusinessException("提交学生成绩失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitStudentAttendance(Long teacherId, Long courseId, Map<String, Object> data) {
        // 检查权限：
        // 1. 如果teacherId为null，视为管理员访问，无需权限检查
        // 2. 如果有teacherId，检查该教师是否有权限操作这个课程
        if (teacherId != null) {
            int count = teacherMapper.checkTeacherCoursePermission(courseId, teacherId);
            if (count == 0) {
                log.warn("教师 {} 没有权限操作课程 {}", teacherId, courseId);
                throw new BusinessException("没有权限操作该课程");
            }
        } else {
            // teacherId为null且courseId为null，参数错误
            if (courseId == null) {
                log.error("参数错误: teacherId和courseId都为空");
                throw new BusinessException("参数不完整：缺少courseId");
            }
            // teacherId为null，作为管理员访问处理
            log.info("管理员用户提交考勤记录: courseId={}", courseId);
        }

        try {
            // 获取考勤日期
            String dateStr = data.get("date").toString();
            LocalDate attendanceDate = LocalDate.parse(dateStr);
            
            // 获取考勤记录列表
            List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
            if (records == null || records.isEmpty()) {
                throw new BusinessException("考勤记录不能为空");
            }
            
            // 批量处理考勤记录
            for (Map<String, Object> record : records) {
                Long studentId = Long.valueOf(record.get("studentId").toString());
                Integer status = Integer.valueOf(record.get("status").toString());
                String remark = record.get("remark") != null ? record.get("remark").toString() : null;
                
                // 查询是否已有考勤记录
                LambdaQueryWrapper<Attendance> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Attendance::getCourseOfferingId, courseId)
                        .eq(Attendance::getStudentId, studentId)
                        .eq(Attendance::getAttendanceDate, attendanceDate);
                
                Attendance attendance = attendanceMapper.selectOne(queryWrapper);
                
                // 如果已有记录，则更新；否则，新增
                if (attendance != null) {
                    attendance.setStatus(status);
                    attendance.setRemark(remark);
                    attendanceMapper.updateById(attendance);
                } else {
                    attendance = new Attendance();
                    attendance.setCourseOfferingId(courseId);
                    attendance.setStudentId(studentId);
                    attendance.setAttendanceDate(attendanceDate);
                    attendance.setStatus(status);
                    attendance.setRemark(remark);
                    attendanceMapper.insert(attendance);
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("提交学生考勤失败", e);
            throw new BusinessException("提交学生考勤失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getTeacherStatistics(Long teacherId) {
        return teacherMapper.selectTeacherStatistics(teacherId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importTeacher(MultipartFile file) throws IOException {
        // 记录导入结果
        int successCount = 0;
        int failCount = 0;
        List<String> failMessages = new ArrayList<>();
        List<TeacherImportDTO> teacherList = new ArrayList<>();

        // 使用EasyExcel解析Excel文件
        EasyExcel.read(file.getInputStream(), TeacherImportDTO.class, new PageReadListener<TeacherImportDTO>(dataList -> {
            for (TeacherImportDTO importDTO : dataList) {
                teacherList.add(importDTO);
            }
        })).sheet().doRead();

        if (teacherList.isEmpty()) {
            throw new BusinessException("导入数据为空");
        }

        // 遍历导入数据
        for (TeacherImportDTO importDTO : teacherList) {
            try {
                // 封装为TeacherDTO
                TeacherDTO teacherDTO = new TeacherDTO();
                teacherDTO.setTeacherNo(importDTO.getTeacherNo());
                teacherDTO.setUsername(importDTO.getUsername());
                teacherDTO.setPassword(DEFAULT_PASSWORD);
                teacherDTO.setName(importDTO.getName());
                teacherDTO.setGender("男".equals(importDTO.getGender()) ? 0 : 1);
                teacherDTO.setDepartmentId(importDTO.getDepartmentId());
                teacherDTO.setTitle(importDTO.getTitle());
                teacherDTO.setIdCard(importDTO.getIdCard());
                teacherDTO.setPhone(importDTO.getPhone());
                teacherDTO.setEmail(importDTO.getEmail());
                teacherDTO.setBirthday(importDTO.getBirthday());
                teacherDTO.setEntryDate(importDTO.getEntryDate());
                teacherDTO.setStatus(0); // 在职状态

                // 添加教师
                addTeacher(teacherDTO);
                successCount++;
            } catch (Exception e) {
                failCount++;
                failMessages.add("第" + (successCount + failCount) + "行：" + e.getMessage());
                log.error("导入教师失败", e);
            }
        }

        // 返回导入结果
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", teacherList.size());
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failMessages", failMessages);

        return result;
    }

    @Override
    public void exportTeacher(TeacherQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        // 创建分页对象（不分页，查询所有数据）
        Page<TeacherVO> page = new Page<>(1, Integer.MAX_VALUE);
        // 查询数据
        IPage<TeacherVO> result = teacherMapper.selectTeacherPage(page,
                queryDTO.getTeacherNo(),
                queryDTO.getName(),
                queryDTO.getDepartmentId(),
                queryDTO.getTitle(),
                queryDTO.getStatus(),
                queryDTO.getEmail(),
                queryDTO.getPhone(),
                queryDTO.getBirthdayStart(),
                queryDTO.getBirthdayEnd(),
                queryDTO.getEntryDateStart(),
                queryDTO.getEntryDateEnd());

        List<TeacherVO> teacherList = result.getRecords();

        // 转换为导出DTO
        List<TeacherImportDTO> exportList = new ArrayList<>();
        for (TeacherVO teacher : teacherList) {
            TeacherImportDTO exportDTO = new TeacherImportDTO();
            exportDTO.setTeacherNo(teacher.getTeacherNo());
            exportDTO.setUsername(teacher.getUsername());
            exportDTO.setName(teacher.getName());
            exportDTO.setGender(teacher.getGenderDesc());
            exportDTO.setDepartmentId(teacher.getDepartmentId());
            exportDTO.setTitle(teacher.getTitle());
            exportDTO.setIdCard(teacher.getIdCard());
            exportDTO.setPhone(teacher.getPhone());
            exportDTO.setEmail(teacher.getEmail());
            exportDTO.setBirthday(teacher.getBirthday());
            exportDTO.setEntryDate(teacher.getEntryDate());
            exportList.add(exportDTO);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教师数据", StandardCharsets.UTF_8) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // 使用EasyExcel导出
        EasyExcel.write(response.getOutputStream(), TeacherImportDTO.class).sheet("教师数据").doWrite(exportList);
    }

    @Override
    public void downloadTeacherTemplate(HttpServletResponse response) throws IOException {
        // 创建模板数据
        List<TeacherImportDTO> templateList = new ArrayList<>();

        // 添加示例数据
        TeacherImportDTO template = new TeacherImportDTO();
        template.setTeacherNo("T20230101");
        template.setUsername("teacher001");
        template.setName("张教授");
        template.setGender("男");
        template.setDepartmentId(1L);
        template.setTitle("教授");
        template.setIdCard("110101199001010011");
        template.setPhone("13800138000");
        template.setEmail("teacher001@example.com");
        template.setBirthday(LocalDate.of(1990, 1, 1));
        template.setEntryDate(LocalDate.of(2023, 1, 1));
        templateList.add(template);

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教师导入模板", StandardCharsets.UTF_8) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // 使用EasyExcel导出模板
        EasyExcel.write(response.getOutputStream(), TeacherImportDTO.class).sheet("教师信息").doWrite(templateList);
    }
}