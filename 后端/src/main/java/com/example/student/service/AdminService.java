package com.example.student.service;

import com.example.student.common.PageResult;
import com.example.student.dto.AdminDTO;
import com.example.student.dto.AdminQueryDTO;
import com.example.student.vo.AdminVO;

/**
 * 管理员服务接口
 *
 * @author example
 */
public interface AdminService {

    /**
     * 分页查询管理员
     *
     * @param queryDTO 查询条件
     * @return 管理员分页数据
     */
    PageResult<AdminVO> pageAdmin(AdminQueryDTO queryDTO);

    /**
     * 根据ID获取管理员
     *
     * @param id 管理员ID
     * @return 管理员信息
     */
    AdminVO getAdminById(Long id);

    /**
     * 新增管理员
     *
     * @param adminDTO 管理员信息
     * @return 是否成功
     */
    boolean addAdmin(AdminDTO adminDTO);

    /**
     * 更新管理员
     *
     * @param adminDTO 管理员信息
     * @return 是否成功
     */
    boolean updateAdmin(AdminDTO adminDTO);

    /**
     * 删除管理员
     *
     * @param id 管理员ID
     * @return 是否成功
     */
    boolean deleteAdmin(Long id);

    /**
     * 重置密码
     *
     * @param id 管理员ID
     * @return 是否成功
     */
    boolean resetPassword(Long id);

    /**
     * 更新管理员状态
     *
     * @param id 管理员ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
} 