package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.common.PageResult;
import com.example.student.common.exception.BusinessException;
import com.example.student.dto.CourseOfferingDTO;
import com.example.student.dto.CourseOfferingQueryDTO;
import com.example.student.entity.Course;
import com.example.student.entity.CourseOffering;
import com.example.student.entity.Teacher;
import com.example.student.mapper.ClassMapper;
import com.example.student.mapper.CourseMapper;
import com.example.student.mapper.CourseOfferingMapper;
import com.example.student.mapper.TeacherMapper;
import com.example.student.service.CourseOfferingService;
import com.example.student.service.CourseSelectionService;
import com.example.student.vo.CourseOfferingVO;
import com.example.student.vo.TeacherVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseOfferingServiceImpl extends ServiceImpl<CourseOfferingMapper, CourseOffering> implements CourseOfferingService {
    private final CourseOfferingMapper courseOfferingMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final ClassMapper classMapper;
    private final CourseSelectionService courseSelectionService;

    @Override
    public PageResult<CourseOfferingVO> pageList(CourseOfferingQueryDTO queryDTO) {
        Page<CourseOffering> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<CourseOffering> wrapper = new LambdaQueryWrapper<>();
        // 学期条件
        if (queryDTO.getSemester() != null && !queryDTO.getSemester().isEmpty()) {
            wrapper.eq(CourseOffering::getSemester, queryDTO.getSemester());
        }
        // 课程名、教师名模糊查询
        List<Long> courseIds = null;
        if (queryDTO.getCourseName() != null && !queryDTO.getCourseName().isEmpty()) {
            courseIds = courseMapper.selectList(new LambdaQueryWrapper<Course>().like(Course::getName, queryDTO.getCourseName()))
                    .stream().map(Course::getId).collect(Collectors.toList());
            if (!courseIds.isEmpty()) {
                wrapper.in(CourseOffering::getCourseId, courseIds);
            } else {
                // 没有匹配课程，直接返回空
                return PageResult.build(List.of(), 0L, queryDTO.getPageNum(), queryDTO.getPageSize());
            }
        }
        List<Long> teacherIds = null;
        if (queryDTO.getTeacherName() != null && !queryDTO.getTeacherName().isEmpty()) {
            // 通过VO查询教师姓名
            List<TeacherVO> teacherVOs = teacherMapper.selectTeacherPage(new Page<>(1, 1000), null, queryDTO.getTeacherName(), null, null, null, null, null, null, null, null, null).getRecords();
            teacherIds = teacherVOs.stream().map(TeacherVO::getId).collect(Collectors.toList());
            if (!teacherIds.isEmpty()) {
                wrapper.in(CourseOffering::getTeacherId, teacherIds);
            } else {
                return PageResult.build(List.of(), 0L, queryDTO.getPageNum(), queryDTO.getPageSize());
            }
        }
        wrapper.orderByDesc(CourseOffering::getCreateTime);
        IPage<CourseOffering> result = courseOfferingMapper.selectPage(page, wrapper);
        List<CourseOfferingVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.build(voList, result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public CourseOfferingVO getById(Long id) {
        CourseOffering entity = courseOfferingMapper.selectById(id);
        if (entity == null) throw new BusinessException("课程开设记录不存在");
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(CourseOfferingDTO dto) {
        CourseOffering entity = new CourseOffering();
        BeanUtils.copyProperties(dto, entity);
        // 处理location字段
        entity.setLocation(dto.getLocation());
        entity.setCapacity(dto.getCapacity());
        entity.setSelectedCount(0); // 新增时已选人数为0
        if (!this.save(entity)) {
            throw new BusinessException("新增课程开设失败");
        }
        
        // 如果指定了班级ID，则在课程开设记录保存后，为该班级学生批量创建选课记录
        if (dto.getClassId() != null) {
            try {
                // 获取班级学生并创建选课记录
                courseSelectionService.createSelectionsForClass(entity.getId(), dto.getClassId());
            } catch (Exception e) {
                log.error("为班级创建选课记录失败", e);
                throw new BusinessException("为班级创建选课记录失败: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CourseOfferingDTO dto) {
        CourseOffering entity = courseOfferingMapper.selectById(dto.getId());
        if (entity == null) throw new BusinessException("课程开设记录不存在");
        BeanUtils.copyProperties(dto, entity);
        // 处理location字段
        entity.setLocation(dto.getLocation());
        entity.setCapacity(dto.getCapacity());
        if (!this.updateById(entity)) {
            throw new BusinessException("更新课程开设失败");
        }
        
        // 处理班级关联
        if (dto.getClassId() != null) {
            // 获取已关联的班级ID
            Long existingClassId = courseOfferingMapper.getClassIdByCourseOfferingId(entity.getId());
            // 如果已经关联了班级且与要设置的班级不同，先取消原班级关联
            if (existingClassId != null && !existingClassId.equals(dto.getClassId())) {
                // 取消原班级关联（可以选择删除或标记已退选）
                courseSelectionService.cancelSelectionsForClass(entity.getId(), existingClassId);
                // 创建新班级关联
                courseSelectionService.createSelectionsForClass(entity.getId(), dto.getClassId());
            } 
            // 如果没有关联班级，直接创建
            else if (existingClassId == null) {
                courseSelectionService.createSelectionsForClass(entity.getId(), dto.getClassId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 删除课程开设记录前，先删除相关的选课记录
        try {
            courseSelectionService.deleteByOfferingId(id);
        } catch (Exception e) {
            log.error("删除课程开设相关选课记录失败", e);
            throw new BusinessException("删除课程开设相关选课记录失败");
        }
        
        if (!this.removeById(id)) {
            throw new BusinessException("删除课程开设失败");
        }
    }

    private CourseOfferingVO toVO(CourseOffering entity) {
        CourseOfferingVO vo = new CourseOfferingVO();
        BeanUtils.copyProperties(entity, vo);
        // 课程名
        Course course = courseMapper.selectById(entity.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getName());
            vo.setCourseCode(course.getCode());
        }
        // 教师名
        TeacherVO teacherVO = teacherMapper.selectTeacherById(entity.getTeacherId());
        if (teacherVO != null) {
            vo.setTeacherName(teacherVO.getName());
        }
        
        // 获取班级信息
        try {
            // 获取关联的班级信息
            Long classId = courseOfferingMapper.getClassIdByCourseOfferingId(entity.getId());
            if (classId != null) {
                // 使用现有的查询方法获取班级详细信息
                Map<String, Object> classInfo = courseOfferingMapper.getCourseAndClassInfo(entity.getId());
                if (classInfo != null) {
                    vo.setClassId(classId);
                    vo.setClassName((String) classInfo.get("class_name"));
                    vo.setClassCode((String) classInfo.get("class_code"));
                }
            }
        } catch (Exception e) {
            log.error("获取课程开设相关班级信息失败", e);
        }
        
        return vo;
    }
} 