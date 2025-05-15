package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.student.common.PageResult;
import com.example.student.common.exception.BusinessException;
import com.example.student.util.SecurityUtils;
import com.example.student.common.utils.FileUtils;
import com.example.student.dto.SysUserDTO;
import com.example.student.dto.UpdatePasswordDTO;
import com.example.student.dto.UserQueryDTO;
import com.example.student.entity.SysUser;
import com.example.student.entity.SysUserRole;
import com.example.student.mapper.SysUserMapper;
import com.example.student.mapper.SysUserRoleMapper;
import com.example.student.service.SysUserService;
import com.example.student.vo.SysUserVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统用户服务实现类
 *
 * @author example
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private PasswordEncoder passwordEncoder;
    
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    
    @Value("${file.access.path:/uploads}")
    private String accessPath;

    @Value("${default.avatar.url:/uploads/avatar/default.jpg}")
    private String defaultAvatarUrl;

    @Override
    public SysUser getUserByUsername(String username) {
        return sysUserMapper.selectUserByUsername(username);
    }

    @Override
    public PageResult<SysUserVO> pageUser(UserQueryDTO queryDTO) {
        // 创建分页对象
        Page<SysUserVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 查询数据
        IPage<SysUserVO> result = sysUserMapper.selectUserList(page,
                queryDTO.getUsername(),
                queryDTO.getName(),
                queryDTO.getPhone(),
                queryDTO.getStatus(),
                queryDTO.getUserType());
        // 返回结果
        return PageResult.build(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public SysUserVO getUserById(Long userId) {
        SysUserVO userVO = sysUserMapper.selectUserById(userId);
        if (userVO != null) {
            // 处理头像URL，确保返回有效的URL
            userVO.setAvatar(getEffectiveAvatarUrl(userVO.getAvatar()));
        }
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(SysUserDTO userDTO) {
        // 检查用户名是否存在
        SysUser existUser = sysUserMapper.selectUserByUsername(userDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDTO, sysUser);
        // 密码加密
        sysUser.setPassword(passwordEncoder.encode(userDTO.getPassword() != null ? userDTO.getPassword() : "123456"));
        // 保存用户
        boolean result = save(sysUser);

        // 设置用户角色关联
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            setUserRoles(sysUser.getId(), userDTO.getRoleIds());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(SysUserDTO userDTO) {
        // 检查用户是否存在
        SysUser existUser = getById(userDTO.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果修改了用户名，检查是否重复
        if (!existUser.getUsername().equals(userDTO.getUsername())) {
            SysUser existUsername = sysUserMapper.selectUserByUsername(userDTO.getUsername());
            if (existUsername != null) {
                throw new BusinessException("用户名已存在");
            }
        }

        // 更新用户信息
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDTO, sysUser);
        // 如果密码不为空，则更新密码
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            sysUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            sysUser.setPassword(null); // 不更新密码
        }
        // 更新用户
        boolean result = updateById(sysUser);

        // 更新用户角色关联
        if (userDTO.getRoleIds() != null) {
            // 先删除原有角色关联
            LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserRole::getUserId, userDTO.getId());
            sysUserRoleMapper.delete(wrapper);
            
            // 设置新的角色关联
            if (!userDTO.getRoleIds().isEmpty()) {
                setUserRoles(userDTO.getId(), userDTO.getRoleIds());
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        // 不能删除超级管理员
        if (userId == 1L) {
            throw new BusinessException("不能删除超级管理员");
        }

        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleMapper.delete(wrapper);

        // 删除用户
        return removeById(userId);
    }

    @Override
    public boolean changeUserStatus(Long userId, Integer status) {
        // 不能修改超级管理员状态
        if (userId == 1L) {
            throw new BusinessException("不能修改超级管理员状态");
        }

        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setStatus(status);
        return updateById(sysUser);
    }

    @Override
    public boolean resetUserPassword(Long userId, String password) {
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setPassword(passwordEncoder.encode(password));
        return updateById(sysUser);
    }

    @Override
    public boolean updateUserPassword(Long userId, UpdatePasswordDTO passwordDTO) {
        // 获取用户信息
        SysUser sysUser = getById(userId);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 校验旧密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), sysUser.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 更新密码
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        return updateById(updateUser);
    }

    @Override
    public boolean updateUserProfile(Long userId, SysUserDTO userDTO) {
        // 获取用户信息
        SysUser sysUser = getById(userId);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setName(userDTO.getName());
        updateUser.setEmail(userDTO.getEmail());
        updateUser.setPhone(userDTO.getPhone());
        
        return updateById(updateUser);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        // 获取用户信息
        SysUser sysUser = getById(userId);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }

        try {
            // 如果用户已有头像，删除旧头像
            if (sysUser.getAvatar() != null && !sysUser.getAvatar().isEmpty() && !sysUser.getAvatar().startsWith("http")) {
                // 从头像URL中提取文件路径
                String oldAvatarPath = sysUser.getAvatar().replace(accessPath, uploadPath);
                // 删除旧头像文件
                FileUtils.delete(oldAvatarPath);
            }
            
            // 上传新头像
            String fileName = FileUtils.upload(file, uploadPath + "/avatar");
            String avatarUrl = accessPath + "/avatar/" + fileName;

            // 更新用户头像
            SysUser updateUser = new SysUser();
            updateUser.setId(userId);
            updateUser.setAvatar(avatarUrl);
            updateById(updateUser);

            return avatarUrl;
        } catch (IOException e) {
            log.error("上传头像失败", e);
            throw new BusinessException("上传头像失败:" + e.getMessage());
        }
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return sysUserMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setUserRoles(Long userId, List<Long> roleIds) {
        // 批量添加用户角色关联
        List<SysUserRole> userRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        }
        
        if (!userRoles.isEmpty()) {
            for (SysUserRole userRole : userRoles) {
                sysUserRoleMapper.insert(userRole);
            }
        }
        
        return true;
    }
    
    /**
     * 导出用户数据
     *
     * @param queryDTO 查询条件
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    @Override
    public void exportUser(UserQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        // 查询用户列表，不分页
        queryDTO.setPageSize(Integer.MAX_VALUE);
        queryDTO.setPageNum(1);
        PageResult<SysUserVO> pageResult = pageUser(queryDTO);
        List<SysUserVO> userList = pageResult.getList();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户数据");
            
            // 设置表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // 创建表头
            String[] headers = {"用户ID", "用户名", "姓名", "用户类型", "手机号码", "邮箱", "状态", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256);
            }
            
            // 写入数据
            int rowNum = 1;
            for (SysUserVO user : userList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId() != null ? user.getId().toString() : "");
                row.createCell(1).setCellValue(user.getUsername() != null ? user.getUsername() : "");
                row.createCell(2).setCellValue(user.getName() != null ? user.getName() : "");
                row.createCell(3).setCellValue(getUserTypeText(user.getUserType()));
                row.createCell(4).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(5).setCellValue(user.getEmail() != null ? user.getEmail() : "");
                row.createCell(6).setCellValue(user.getStatus() != null ? (user.getStatus() == 0 ? "正常" : "禁用") : "");
                row.createCell(7).setCellValue(user.getCreateTime() != null ? user.getCreateTime().toString() : "");
            }
            
            // 设置响应头
            String fileName = URLEncoder.encode("用户数据.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            
            // 写入输出流
            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }
        }
    }
    
    /**
     * 根据用户类型获取文本描述
     *
     * @param userType 用户类型
     * @return 用户类型文本
     */
    private String getUserTypeText(Integer userType) {
        if (userType == null) {
            return "未知";
        }
        switch (userType) {
            case 0: return "管理员";
            case 1: return "教师";
            case 2: return "学生";
            default: return "未知";
        }
    }
    
    @Override
    public SysUser findByUsername(String username) {
        return getUserByUsername(username);
    }
    
    @Override
    public boolean isAdmin(SysUser user) {
        return user != null && user.getUserType() != null && user.getUserType() == 0;
    }

    /**
     * 获取有效的头像URL
     * 
     * @param avatar 原头像URL
     * @return 有效的头像URL
     */
    private String getEffectiveAvatarUrl(String avatar) {
        if (avatar == null || avatar.isEmpty()) {
            return defaultAvatarUrl;
        }
        return avatar;
    }
} 