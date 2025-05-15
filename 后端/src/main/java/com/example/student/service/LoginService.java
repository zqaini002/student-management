package com.example.student.service;

import com.example.student.dto.LoginDTO;
import com.example.student.dto.LoginResponseDTO;
import com.example.student.entity.SysMenu;

import java.util.List;

/**
 * 登录服务接口
 *
 * @author example
 */
public interface LoginService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return 登录结果
     */
    LoginResponseDTO login(LoginDTO loginDTO);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    LoginResponseDTO getUserInfo();

    /**
     * 用户登出
     */
    void logout();
    
    /**
     * 获取当前用户菜单列表
     *
     * @return 菜单列表
     */
    List<SysMenu> getUserMenuList();
} 