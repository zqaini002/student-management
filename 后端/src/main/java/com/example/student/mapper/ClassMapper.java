package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student.entity.Class;
import com.example.student.vo.ClassVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 班级 Mapper 接口
 *
 * @author example
 */
@Repository
public interface ClassMapper extends BaseMapper<Class> {
    
    /**
     * 查询所有班级列表
     * 
     * @return 班级列表
     */
    @Select("SELECT * FROM class ORDER BY update_time DESC")
    List<Class> selectClassList();
    
    /**
     * 根据班级编码查询班级
     *
     * @param code 班级编码
     * @return 班级
     */
    @Select("SELECT * FROM class WHERE code = #{code} LIMIT 1")
    Class selectByCode(@Param("code") String code);

    /**
     * 根据班级名称查询班级
     *
     * @param name 班级名称
     * @return 班级
     */
    @Select("SELECT * FROM class WHERE name = #{name} LIMIT 1")
    Class selectByName(@Param("name") String name);

    /**
     * 查询班级选项列表
     *
     * @return 班级选项列表
     */
    List<Map<String, Object>> selectClassOptions();

    /**
     * 查询所有班级VO列表
     *
     * @return 班级VO列表
     */
    List<ClassVO> selectClassVOList();

    /**
     * 根据班级ID集合查询班级VO列表
     *
     * @param classList 班级列表
     * @return 班级VO列表
     */
    List<ClassVO> selectClassVOListByIds(@Param("classList") List<Class> classList);

    /**
     * 根据ID查询班级详情VO
     *
     * @param id 班级ID
     * @return 班级详情VO
     */
    ClassVO selectClassVOById(@Param("id") Long id);
} 
