package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.common.exception.BusinessException;
import com.example.student.common.PageResult;
import com.example.student.dto.CourseSelectionDTO;
import com.example.student.dto.CourseSelectionQueryDTO;
import com.example.student.entity.Course;
import com.example.student.entity.CourseOffering;
import com.example.student.entity.CourseSelection;
import com.example.student.entity.Student;
import com.example.student.mapper.ClassMapper;
import com.example.student.mapper.CourseMapper;
import com.example.student.mapper.CourseOfferingMapper;
import com.example.student.mapper.CourseSelectionMapper;
import com.example.student.mapper.StudentMapper;
import com.example.student.service.CourseSelectionService;
import com.example.student.util.ExcelUtils;
import com.example.student.vo.CourseSelectionManageVO;
import com.example.student.vo.CourseSelectionStatisticsVO;
import com.example.student.vo.CourseTypeStatisticsVO;
import com.example.student.vo.SemesterStatisticsVO;
import com.example.student.vo.SemesterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 选课服务实现类
 *
 * @author example
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSelectionServiceImpl extends ServiceImpl<CourseSelectionMapper, CourseSelection> implements CourseSelectionService {

    private final CourseSelectionMapper courseSelectionMapper;
    private final CourseMapper courseMapper;
    private final CourseOfferingMapper courseOfferingMapper;
    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;

    @Override
    public PageResult<CourseSelectionManageVO> getSelectionList(CourseSelectionQueryDTO queryDTO) {
        IPage<CourseSelectionManageVO> page = courseSelectionMapper.selectSelectionPage(
                new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), queryDTO);
        
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public CourseSelectionManageVO getSelectionById(Long id) {
        return courseSelectionMapper.selectSelectionById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSelection(CourseSelectionDTO courseSelectionDTO) {
        CourseSelection selection = courseSelectionMapper.selectById(courseSelectionDTO.getId());
        if (selection == null) {
            throw new BusinessException("选课记录不存在");
        }
        
        selection.setStatus(courseSelectionDTO.getStatus());
        // 设置成绩
        if (courseSelectionDTO.getScore() != null) {
            selection.setScore(courseSelectionDTO.getScore());
        }
        selection.setUpdateTime(LocalDateTime.now());
        
        return courseSelectionMapper.updateById(selection) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawCourse(Long id) {
        CourseSelection selection = courseSelectionMapper.selectById(id);
        if (selection == null) {
            throw new BusinessException("选课记录不存在");
        }
        
        selection.setStatus(2);
        selection.setUpdateTime(LocalDateTime.now());
        
        return courseSelectionMapper.updateById(selection) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchWithdrawCourses(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        for (Long id : ids) {
            withdrawCourse(id);
        }
        
        return true;
    }

    @Override
    public void exportSelection(CourseSelectionQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<CourseSelectionManageVO> selections = courseSelectionMapper.selectSelectionList(queryDTO);
        
        log.info("导出选课数据，数量：{}", selections.size());
    }

    @Override
    public CourseSelectionStatisticsVO getSelectionStatistics() {
        CourseSelectionStatisticsVO statisticsVO = new CourseSelectionStatisticsVO();
        statisticsVO.setTotalSelections(courseSelectionMapper.countTotalSelections());
        statisticsVO.setTotalStudents(courseSelectionMapper.countDistinctStudents());
        statisticsVO.setPendingSelections(courseSelectionMapper.countSelectionsByStatus(0));
        statisticsVO.setConfirmedSelections(courseSelectionMapper.countSelectionsByStatus(2));
        statisticsVO.setWithdrawnSelections(courseSelectionMapper.countSelectionsByStatus(1));
        
        Map<String, Object> settings = getSelectionSettings();
        String currentSemester = (String) settings.get("currentSemester");
        
        statisticsVO.setCurrentSelections(courseSelectionMapper.countCurrentSemesterSelections(currentSemester));
        statisticsVO.setCompletedSelections(courseSelectionMapper.countCompletedSelections());
        
        return statisticsVO;
    }

    @Override
    public List<CourseTypeStatisticsVO> getCourseTypeStatistics() {
        return courseSelectionMapper.selectCourseTypeStatistics();
    }

    @Override
    public List<SemesterStatisticsVO> getSemesterStatistics() {
        return courseSelectionMapper.selectSemesterStatistics();
    }

    @Override
    public List<SemesterVO> getSemesters() {
        return courseSelectionMapper.selectSemesters();
    }

    @Override
    public boolean generateSelectionReport(Map<String, Object> params) {
        log.info("生成选课报表，参数：{}", params);
        return true;
    }
    
    @Override
    public PageResult<Map<String, Object>> getStudentSelectedCourses(Long studentId, Map<String, Object> params) {
        int pageNum = params.containsKey("pageNum") ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        String courseName = (String) params.get("courseName");
        String courseCode = (String) params.get("courseCode");
        String semester = (String) params.get("semester");
        
        IPage<Map<String, Object>> page = courseSelectionMapper.selectStudentCourses(
                new Page<>(pageNum, pageSize),
                studentId,
                courseName,
                courseCode,
                semester);
        
        List<Map<String, Object>> records = page.getRecords();
        records.forEach(this::processSelectedCourseRecord);
        
        return new PageResult<>(records, page.getTotal());
    }

    @Override
    public Map<String, Object> getCourseDetail(Long courseId) {
        Map<String, Object> courseDetail = courseMapper.selectCourseDetailById(courseId);
        if (courseDetail == null) {
            throw new BusinessException("课程不存在");
        }
        
        return courseDetail;
    }

    @Override
    public List<Map<String, Object>> getCourseMaterials(Long courseId) {
        return courseMapper.selectCourseMaterialsById(courseId);
    }

    @Override
    public PageResult<Map<String, Object>> getAvailableCourses(Long studentId, Map<String, Object> params) {
        int pageNum = params.containsKey("pageNum") ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        String courseName = (String) params.get("courseName");
        String courseType = (String) params.get("courseType");
        String creditsRange = (String) params.get("creditsRange");
        
        IPage<Map<String, Object>> page = courseOfferingMapper.selectAvailableCourses(
                new Page<>(pageNum, pageSize),
                studentId,
                courseName,
                courseType,
                creditsRange);
        
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectCourse(Long studentId, Long courseOfferingId) {
        if (studentId == null || courseOfferingId == null) {
            throw new BusinessException("参数错误");
        }
        
        CourseOffering offering = courseOfferingMapper.selectById(courseOfferingId);
        if (offering == null) {
            throw new BusinessException("课程不存在");
        }
        
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, studentId)
                .eq(CourseSelection::getCourseOfferingId, courseOfferingId)
                .ne(CourseSelection::getStatus, 2);
        
        if (courseSelectionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("您已经选过该课程");
        }
        
        int selectedCount = courseSelectionMapper.countSelectedStudents(courseOfferingId);
        if (offering.getCapacity() != null && selectedCount >= offering.getCapacity()) {
            throw new BusinessException("该课程选课人数已满");
        }
        
        Map<String, Object> settings = getSelectionSettings();
        boolean isSelectionTime = (boolean) settings.get("isSelectionTime");
        if (!isSelectionTime) {
            throw new BusinessException("当前不在选课时间范围内");
        }
        
        CourseSelection selection = new CourseSelection();
        selection.setStudentId(studentId);
        selection.setCourseOfferingId(courseOfferingId);
        selection.setStatus(0);
        selection.setSelectionTime(LocalDateTime.now());
        selection.setCreateTime(LocalDateTime.now());
        selection.setUpdateTime(LocalDateTime.now());
        
        return courseSelectionMapper.insert(selection) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawCourse(Long studentId, Long courseOfferingId) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, studentId)
                .eq(CourseSelection::getCourseOfferingId, courseOfferingId)
                .ne(CourseSelection::getStatus, 2);
        
        CourseSelection selection = courseSelectionMapper.selectOne(wrapper);
        if (selection == null) {
            throw new BusinessException("未找到选课记录");
        }
        
        Map<String, Object> settings = getSelectionSettings();
        boolean isSelectionTime = (boolean) settings.get("isSelectionTime");
        if (!isSelectionTime) {
            throw new BusinessException("当前不在选课时间范围内");
        }
        
        selection.setStatus(2);
        selection.setUpdateTime(LocalDateTime.now());
        
        return courseSelectionMapper.updateById(selection) > 0;
    }

    @Override
    public Map<String, Object> getSelectionSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        settings.put("maxCourses", 10);
        settings.put("maxCredits", 30);
        settings.put("startTime", "2025-06-10 00:00:00");
        settings.put("endTime", "2025-06-20 23:59:59");
        settings.put("isSelectionTime", true);
        settings.put("currentSemester", "2024-2025-2");
        
        return settings;
    }

    @Override
    public boolean checkTimeConflict(String courseTime1, String courseTime2) {
        if (!StringUtils.hasText(courseTime1) || !StringUtils.hasText(courseTime2)) {
            return false;
        }
        
        List<CourseTimeSlot> slots1 = parseTimeSlots(courseTime1);
        List<CourseTimeSlot> slots2 = parseTimeSlots(courseTime2);
        
        for (CourseTimeSlot slot1 : slots1) {
            for (CourseTimeSlot slot2 : slots2) {
                if (isTimeSlotConflict(slot1, slot2)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    public Map<String, Object> getSelectionStats(Map<String, Object> params) {
        Map<String, Object> stats = new HashMap<>();
        
        Long studentId = params.containsKey("studentId") ? Long.parseLong(params.get("studentId").toString()) : null;
        String semester = (String) params.get("semester");
        
        int selectedCount = courseSelectionMapper.countStudentSelectedCourses(studentId, semester);
        stats.put("selectedCount", selectedCount);
        
        double selectedCredits = courseSelectionMapper.sumStudentSelectedCredits(studentId, semester);
        stats.put("selectedCredits", selectedCredits);
        
        stats.put("maxSelectableCount", 10);
        stats.put("maxSelectableCredits", 30);
        
        return stats;
    }

    private void processSelectedCourseRecord(Map<String, Object> record) {
        if (record.containsKey("status")) {
            Integer status = (Integer) record.get("status");
            switch (status) {
                case 0:
                    record.put("statusDesc", "未开始");
                    break;
                case 1:
                    record.put("statusDesc", "进行中");
                    break;
                case 2:
                    record.put("statusDesc", "已结束");
                    break;
                default:
                    record.put("statusDesc", "未知");
            }
        }
    }

    private List<CourseTimeSlot> parseTimeSlots(String courseTime) {
        List<CourseTimeSlot> slots = new ArrayList<>();
        
        String[] segments = courseTime.split(",");
        
        for (String segment : segments) {
            Pattern pattern = Pattern.compile("周(.)\\((\\d+)-(\\d+)\\)");
            Matcher matcher = pattern.matcher(segment.trim());
            
            if (matcher.find()) {
                CourseTimeSlot slot = new CourseTimeSlot();
                
                String weekDay = matcher.group(1);
                switch (weekDay) {
                    case "一": slot.setWeekDay(1); break;
                    case "二": slot.setWeekDay(2); break;
                    case "三": slot.setWeekDay(3); break;
                    case "四": slot.setWeekDay(4); break;
                    case "五": slot.setWeekDay(5); break;
                    case "六": slot.setWeekDay(6); break;
                    case "日": slot.setWeekDay(7); break;
                    default: continue;
                }
                
                slot.setStartSection(Integer.parseInt(matcher.group(2)));
                slot.setEndSection(Integer.parseInt(matcher.group(3)));
                
                slots.add(slot);
            }
        }
        
        return slots;
    }

    private boolean isTimeSlotConflict(CourseTimeSlot slot1, CourseTimeSlot slot2) {
        if (!slot1.getWeekDay().equals(slot2.getWeekDay())) {
            return false;
        }
        
        return slot1.getStartSection() <= slot2.getEndSection() && slot2.getStartSection() <= slot1.getEndSection();
    }

    private static class CourseTimeSlot {
        private Integer weekDay;
        private Integer startSection;
        private Integer endSection;

        public Integer getWeekDay() {
            return weekDay;
        }

        public void setWeekDay(Integer weekDay) {
            this.weekDay = weekDay;
        }

        public Integer getStartSection() {
            return startSection;
        }

        public void setStartSection(Integer startSection) {
            this.startSection = startSection;
        }

        public Integer getEndSection() {
            return endSection;
        }

        public void setEndSection(Integer endSection) {
            this.endSection = endSection;
        }
    }

    /**
     * 为班级创建选课记录
     *
     * @param courseOfferingId 课程开设ID
     * @param classId 班级ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSelectionsForClass(Long courseOfferingId, Long classId) {
        log.info("为班级创建选课记录：courseOfferingId={}, classId={}", courseOfferingId, classId);
        
        // 校验课程开设记录
        CourseOffering courseOffering = courseOfferingMapper.selectById(courseOfferingId);
        if (courseOffering == null) {
            throw new BusinessException("课程开设记录不存在");
        }
        
        // 查询班级下的所有学生
        List<Student> students = studentMapper.selectList(
                new LambdaQueryWrapper<Student>()
                        .eq(Student::getClassId, classId));
        
        if (students.isEmpty()) {
            log.warn("班级下没有学生，班级ID：{}", classId);
            return true;
        }
        
        int successCount = 0;
        int failCount = 0;
        
        // 为每个学生创建选课记录
        LocalDateTime now = LocalDateTime.now();
        for (Student student : students) {
            // 检查是否已经有选课记录
            CourseSelection existingSelection = this.getOne(
                    new LambdaQueryWrapper<CourseSelection>()
                            .eq(CourseSelection::getStudentId, student.getId())
                            .eq(CourseSelection::getCourseOfferingId, courseOfferingId));
            
            // 如果已存在且不是退选状态，则跳过
            if (existingSelection != null && existingSelection.getStatus() != 1) {
                log.debug("学生已有选课记录，学生ID：{}，选课ID：{}", student.getId(), existingSelection.getId());
                continue;
            }
            
            try {
                // 如果已存在但是退选状态，更新状态为已选
                if (existingSelection != null && existingSelection.getStatus() == 1) {
                    existingSelection.setStatus(0); // 0:已选
                    existingSelection.setUpdateTime(now);
                    this.updateById(existingSelection);
                    successCount++;
                    continue;
                }
                
                // 创建新的选课记录
                CourseSelection newSelection = new CourseSelection();
                newSelection.setStudentId(student.getId());
                newSelection.setCourseOfferingId(courseOfferingId);
                newSelection.setSelectionTime(now);
                newSelection.setStatus(0); // 0:已选
                newSelection.setCreateTime(now);
                newSelection.setUpdateTime(now);
                this.save(newSelection);
                successCount++;
            } catch (Exception e) {
                log.error("为学生创建选课记录失败，学生ID：{}，错误：{}", student.getId(), e.getMessage(), e);
                failCount++;
            }
        }
        
        // 更新课程开设记录的已选人数
        if (successCount > 0) {
            updateCourseOfferingSelectedCount(courseOfferingId);
        }
        
        log.info("为班级创建选课记录完成：成功{}条，失败{}条", successCount, failCount);
        return failCount == 0;
    }
    
    /**
     * 取消班级选课记录
     *
     * @param courseOfferingId 课程开设ID
     * @param classId 班级ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSelectionsForClass(Long courseOfferingId, Long classId) {
        log.info("取消班级选课记录：courseOfferingId={}, classId={}", courseOfferingId, classId);
        
        // 查询班级下的所有学生
        List<Student> students = studentMapper.selectList(
                new LambdaQueryWrapper<Student>()
                        .eq(Student::getClassId, classId));
        
        if (students.isEmpty()) {
            log.warn("班级下没有学生，班级ID：{}", classId);
            return true;
        }
        
        List<Long> studentIds = students.stream().map(Student::getId).toList();
        
        // 批量更新选课状态为退选
        boolean success = this.update(
                new LambdaUpdateWrapper<CourseSelection>()
                        .set(CourseSelection::getStatus, 1) // 1:已退
                        .set(CourseSelection::getUpdateTime, LocalDateTime.now())
                        .eq(CourseSelection::getCourseOfferingId, courseOfferingId)
                        .in(CourseSelection::getStudentId, studentIds)
                        .ne(CourseSelection::getStatus, 2) // 不包括已修完的
        );
        
        // 更新课程开设记录的已选人数
        if (success) {
            updateCourseOfferingSelectedCount(courseOfferingId);
        }
        
        return success;
    }
    
    /**
     * 删除指定课程开设的所有选课记录
     *
     * @param courseOfferingId 课程开设ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByOfferingId(Long courseOfferingId) {
        log.info("删除课程开设的所有选课记录：courseOfferingId={}", courseOfferingId);
        
        // 查询是否有已修完的选课记录
        long completedCount = this.count(
                new LambdaQueryWrapper<CourseSelection>()
                        .eq(CourseSelection::getCourseOfferingId, courseOfferingId)
                        .eq(CourseSelection::getStatus, 2) // 2:已修完
        );
        
        if (completedCount > 0) {
            log.warn("课程开设有已修完的选课记录，不能删除，courseOfferingId={}, count={}", courseOfferingId, completedCount);
            throw new BusinessException("课程开设有已修完的选课记录，不能删除");
        }
        
        // 删除选课记录
        return this.remove(
                new LambdaQueryWrapper<CourseSelection>()
                        .eq(CourseSelection::getCourseOfferingId, courseOfferingId)
        );
    }
    
    /**
     * 更新课程开设记录的已选人数
     *
     * @param courseOfferingId 课程开设ID
     */
    private void updateCourseOfferingSelectedCount(Long courseOfferingId) {
        // 统计已选人数（不包括退选的）
        long selectedCount = this.count(
                new LambdaQueryWrapper<CourseSelection>()
                        .eq(CourseSelection::getCourseOfferingId, courseOfferingId)
                        .ne(CourseSelection::getStatus, 1) // 不是退选状态
        );
        
        // 更新课程开设记录
        CourseOffering courseOffering = courseOfferingMapper.selectById(courseOfferingId);
        if (courseOffering != null) {
            courseOffering.setSelectedCount((int)selectedCount);
            courseOfferingMapper.updateById(courseOffering);
        }
    }
} 