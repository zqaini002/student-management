package com.example.student.controller;

import com.example.student.common.Result;
import com.example.student.dto.LoginDTO;
import com.example.student.dto.LoginResponseDTO;
import com.example.student.entity.SysMenu;
import com.example.student.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录控制器
 *
 * @author example
 */
@RequestMapping("/auth")
@Tag(name = "登录管理", description = "登录相关接口")
@RestController
public class LoginController {

    @Resource
    private LoginService loginService;

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO loginResponse = loginService.login(loginDTO);
        return Result.success("登录成功", loginResponse);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取当前登录用户信息", description = "获取当前登录用户信息接口")
    @GetMapping("/user/info")
    public Result<LoginResponseDTO> getUserInfo() {
        LoginResponseDTO userInfo = loginService.getUserInfo();
        return Result.success(userInfo);
    }

    /**
     * 用户登出
     *
     * @return 结果
     */
    @Operation(summary = "用户登出", description = "用户登出接口")
    @PostMapping("/logout")
    public Result<Void> logout() {
        loginService.logout();
        return Result.success("登出成功");
    }
    
    /**
     * 获取用户路由菜单
     *
     * @return 路由菜单列表
     */
    @Operation(summary = "获取用户路由菜单", description = "获取当前用户可访问的路由菜单")
    @GetMapping("/routes")
    public Result<List<SysMenu>> getRoutes() {
        List<SysMenu> menuList = loginService.getUserMenuList();
        return Result.success(menuList);
    }
} 