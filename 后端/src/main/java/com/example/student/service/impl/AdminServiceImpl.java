package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student.common.PageResult;
import com.example.student.dto.AdminDTO;
import com.example.student.dto.AdminQueryDTO;
import com.example.student.entity.SysUser;
import com.example.student.entity.SysUserRole;
import com.example.student.mapper.AdminMapper;
import com.example.student.mapper.SysUserMapper;
import com.example.student.mapper.SysUserRoleMapper;
import com.example.student.service.AdminService;
import com.example.student.vo.AdminVO;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 管理员服务实现
 *
 * @author example
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    // 默认密码
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public PageResult<AdminVO> pageAdmin(AdminQueryDTO queryDTO) {
        // 创建分页对象
        Page<AdminVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 查询数据
        IPage<AdminVO> result = adminMapper.selectAdminPage(page, queryDTO.getUsername(), queryDTO.getName(), queryDTO.getStatus());
        // 返回结果
        return PageResult.build(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public AdminVO getAdminById(Long id) {
        return adminMapper.selectAdminById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAdmin(AdminDTO adminDTO) {
        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(adminDTO.getUsername());
        user.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        user.setName(adminDTO.getName());
        user.setEmail(adminDTO.getEmail());
        user.setPhone(adminDTO.getPhone());
        user.setAvatar(adminDTO.getAvatar());
        user.setStatus(adminDTO.getStatus() != null ? adminDTO.getStatus() : 0);
        user.setUserType(0); // 管理员
        int rows = sysUserMapper.insert(user);

        // 分配管理员角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(1L); // 默认为超管角色
        sysUserRoleMapper.insert(userRole);

        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdmin(AdminDTO adminDTO) {
        // 更新用户
        SysUser user = new SysUser();
        user.setId(adminDTO.getId());
        user.setName(adminDTO.getName());
        user.setEmail(adminDTO.getEmail());
        user.setPhone(adminDTO.getPhone());
        user.setAvatar(adminDTO.getAvatar());
        user.setStatus(adminDTO.getStatus());
        // 如果密码不为空，则更新密码
        if (adminDTO.getPassword() != null && !adminDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        }
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAdmin(Long id) {
        // 查询用户
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getUserType() != 0) {
            return false;
        }
        
        // 删除用户角色关系
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, id);
        sysUserRoleMapper.delete(queryWrapper);
        
        // 删除用户
        return sysUserMapper.deleteById(id) > 0;
    }

    @Override
    public boolean resetPassword(Long id) {
        // 查询用户
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getUserType() != 0) {
            return false;
        }
        
        // 更新密码
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        return sysUserMapper.updateById(updateUser) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        // 查询用户
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getUserType() != 0) {
            return false;
        }
        
        // 更新状态
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(status);
        return sysUserMapper.updateById(updateUser) > 0;
    }
} 