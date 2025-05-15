package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.entity.Major;
import com.example.student.vo.MajorVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 专业Mapper接口
 */
@Repository
public interface MajorMapper extends BaseMapper<Major> {
    
    /**
     * 查询专业详情
     *
     * @param id 专业ID
     * @return 专业
     */
    Major selectMajorById(Long id);
    
    /**
     * 查询专业列表
     *
     * @return 专业列表
     */
    List<Major> selectMajorList();
    
    /**
     * 查询专业VO列表（包含院系名称）
     *
     * @return 专业VO列表
     */
    List<MajorVO> selectMajorVOList();
    
    /**
     * 分页查询专业VO列表（包含院系名称）
     *
     * @param page 分页参数
     * @param major 查询条件
     * @return 分页专业VO列表
     */
    Page<MajorVO> selectMajorVOPage(Page<Major> page, @Param("major") Major major);
    
    /**
     * 根据ID查询专业VO详情（包含院系名称）
     *
     * @param id 专业ID
     * @return 专业VO详情
     */
    MajorVO selectMajorVOById(@Param("id") Long id);
    
    /**
     * 查询专业选项列表（适用于下拉框）
     *
     * @return 专业选项列表，包含value和label字段
     */
    List<java.util.Map<String, Object>> selectMajorOptions();
} 
