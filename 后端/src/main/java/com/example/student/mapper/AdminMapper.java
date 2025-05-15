package com.example.student.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.entity.SysUser;
import com.example.student.vo.AdminVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 管理员Mapper接口
 *
 * @author example
 */
@Repository
public interface AdminMapper {

    /**
     * 分页查询管理员
     *
     * @param page     分页参数
     * @param username 用户名
     * @param name     姓名
     * @param status   状态
     * @return 分页数据
     */
    IPage<AdminVO> selectAdminPage(Page<AdminVO> page,
                                  @Param("username") String username,
                                  @Param("name") String name,
                                  @Param("status") Integer status);

    /**
     * 根据ID查询管理员
     *
     * @param id 管理员ID
     * @return 管理员信息
     */
    AdminVO selectAdminById(@Param("id") Long id);
} 