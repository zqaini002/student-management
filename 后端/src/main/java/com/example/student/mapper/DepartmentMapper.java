package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student.entity.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 院系Mapper接口
 */
@Repository
public interface DepartmentMapper extends BaseMapper<Department> {
    
    /**
     * 查询院系详情
     *
     * @param id 院系ID
     * @return 院系
     */
    Department selectDepartmentById(Long id);
    
    /**
     * 查询院系列表
     *
     * @return 院系列表
     */
    List<Department> selectDepartmentList();
} 
