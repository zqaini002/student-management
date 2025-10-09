package com.example.student.service.impl;

import com.example.student.dto.LoginDTO;
import com.example.student.dto.LoginResponseDTO;
import com.example.student.entity.SysMenu;
import com.example.student.entity.SysUser;
import com.example.student.entity.Teacher;
import com.example.student.mapper.SysMenuMapper;
import com.example.student.mapper.SysUserMapper;
import com.example.student.mapper.TeacherMapper;
import com.example.student.mapper.StudentMapper;
import com.example.student.security.LoginUser;
import com.example.student.service.LoginService;
import com.example.student.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录服务实现
 *
 * @author example
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private SysUserMapper sysUserMapper;
    
    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private TeacherMapper teacherMapper;
    
    @Resource
    private StudentMapper studentMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public LoginResponseDTO login(LoginDTO loginDTO) {
        // 创建认证令牌
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // 认证
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 从认证信息中获取用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = loginUser.getUser();

        // 从数据库中查询用户的角色和权限
        List<String> roles = sysUserMapper.selectRoleKeysByUserId(user.getId());
        List<String> permissions = sysUserMapper.selectPermsByUserId(user.getId());

        // 根据用户类型查询对应的teacherId或studentId
        Long teacherId = null;
        Long studentId = null;
        
        if (user.getUserType() != null) {
            if (user.getUserType() == 1) {
                // 教师用户
                Teacher teacher = teacherMapper.selectTeacherByUserId(user.getId());
                System.out.println("login - 查询教师信息 - userId: " + user.getId() + ", username: " + user.getUsername() + ", teacher: " + teacher);
                if (teacher != null) {
                    teacherId = teacher.getId();
                    System.out.println("login - 教师用户 " + user.getUsername() + " 的teacherId: " + teacherId);
                } else {
                    System.out.println("login - 警告：教师用户 " + user.getUsername() + " (userId=" + user.getId() + ") 在teacher表中没有对应记录");
                }
            } else if (user.getUserType() == 2) {
                // 学生用户
                studentId = studentMapper.selectStudentIdByUserId(user.getId());
                System.out.println("login - 学生用户 " + user.getUsername() + " 的studentId: " + studentId);
            }
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(loginUser);

        // 构建并返回登录响应
        return LoginResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .avatar(user.getAvatar())
                .userType(user.getUserType())
                .teacherId(teacherId)
                .studentId(studentId)
                .roles(roles)
                .permissions(permissions)
                .token(token)
                .build();
    }

    @Override
    public LoginResponseDTO getUserInfo() {
        // 从SecurityContextHolder中获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = loginUser.getUser();

        // 从数据库中查询用户的角色和权限
        List<String> roles = sysUserMapper.selectRoleKeysByUserId(user.getId());
        List<String> permissions = sysUserMapper.selectPermsByUserId(user.getId());

        // 根据用户类型查询对应的teacherId或studentId
        Long teacherId = null;
        Long studentId = null;
        
        if (user.getUserType() != null) {
            if (user.getUserType() == 1) {
                // 教师用户
                Teacher teacher = teacherMapper.selectTeacherByUserId(user.getId());
                System.out.println("getUserInfo - 查询教师信息 - userId: " + user.getId() + ", username: " + user.getUsername() + ", teacher: " + teacher);
                if (teacher != null) {
                    teacherId = teacher.getId();
                    System.out.println("getUserInfo - 教师用户 " + user.getUsername() + " 的teacherId: " + teacherId);
                } else {
                    System.out.println("getUserInfo - 警告：教师用户 " + user.getUsername() + " (userId=" + user.getId() + ") 在teacher表中没有对应记录");
                }
            } else if (user.getUserType() == 2) {
                // 学生用户
                studentId = studentMapper.selectStudentIdByUserId(user.getId());
                System.out.println("getUserInfo - 学生用户 " + user.getUsername() + " 的studentId: " + studentId);
            }
        }

        // 构建并返回用户信息
        return LoginResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .avatar(user.getAvatar())
                .userType(user.getUserType())
                .teacherId(teacherId)
                .studentId(studentId)
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
    
    @Override
    public List<SysMenu> getUserMenuList() {
        // 从SecurityContextHolder中获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return new ArrayList<>();
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof LoginUser)) {
            return new ArrayList<>();
        }
        
        LoginUser loginUser = (LoginUser) principal;
        SysUser user = loginUser.getUser();
        
        // 获取用户ID
        Long userId = user.getId();
        
        // 从数据库中查询用户菜单
        List<SysMenu> menuList = sysMenuMapper.selectMenuTreeByUserId(userId);
        
        // 如果用户菜单为空，检查是否是因为查询方法未实现
        if (menuList == null || menuList.isEmpty()) {
            // 获取用户角色
            List<String> roles = sysUserMapper.selectRoleKeysByUserId(userId);
            
            // 如果用户是管理员，返回所有菜单
            if (roles.contains("admin")) {
                menuList = sysMenuMapper.selectMenuTreeAll();
            } else {
                // 这里需要手动构建菜单，或者完善selectMenuTreeByUserId方法
                // 临时返回空列表
                menuList = new ArrayList<>();
            }
        }
        
        // 处理菜单数据，确保格式正确
        if (menuList != null) {
            for (SysMenu menu : menuList) {
                // 确保路径以/开头
                if (menu.getPath() != null && !menu.getPath().isEmpty() && !menu.getPath().startsWith("/")) {
                    menu.setPath("/" + menu.getPath());
                }
                
                // 处理组件路径，确保格式正确
                if (menu.getComponent() != null && !menu.getComponent().isEmpty()) {
                    // 如果是Layout组件，保留原样
                    if ("Layout".equals(menu.getComponent())) {
                        continue;
                    }
                    
                    // 如果组件路径不包含 .vue 后缀，则添加
                    if (!menu.getComponent().endsWith(".vue") && 
                        !menu.getComponent().equals("Layout")) {
                        // 检查是否已经包含 index.vue
                        if (menu.getComponent().endsWith("/index")) {
                            menu.setComponent(menu.getComponent() + ".vue");
                        } else if (!menu.getComponent().endsWith("/index.vue")) {
                            // 检查路径是否以/结尾
                            if (menu.getComponent().endsWith("/")) {
                                menu.setComponent(menu.getComponent() + "index.vue");
                            } else {
                                // 假设这是一个目录，添加/index.vue
                                menu.setComponent(menu.getComponent() + "/index.vue");
                            }
                        }
                    }
                }
            }
        }
        
        return menuList;
    }
} 