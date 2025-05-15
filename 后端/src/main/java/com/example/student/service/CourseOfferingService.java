package com.example.student.service;

import com.example.student.common.PageResult;
import com.example.student.dto.CourseOfferingDTO;
import com.example.student.dto.CourseOfferingQueryDTO;
import com.example.student.vo.CourseOfferingVO;

public interface CourseOfferingService {
    PageResult<CourseOfferingVO> pageList(CourseOfferingQueryDTO queryDTO);
    CourseOfferingVO getById(Long id);
    void add(CourseOfferingDTO dto);
    void update(CourseOfferingDTO dto);
    void delete(Long id);
} 