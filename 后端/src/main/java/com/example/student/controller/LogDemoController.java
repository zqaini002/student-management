package com.example.student.controller;

import com.example.student.common.Result;
import com.example.student.service.impl.LogDemoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志功能演示控制器
 *
 * @author example
 */
@Tag(name = "日志功能演示")
@RestController
@RequestMapping("/log")
public class LogDemoController {

    @Autowired
    private LogDemoServiceImpl logDemoService;

    /**
     * 正常日志记录演示
     */
    @Operation(summary = "正常日志记录演示")
    @GetMapping("/demo")
    public Result<String> logDemo(
            @Parameter(description = "参数") @RequestParam(required = false) String param) {
        String result = logDemoService.logDemo(param);
        return Result.success(result);
    }

    /**
     * 异常日志记录演示
     */
    @Operation(summary = "异常日志记录演示")
    @GetMapping("/exception")
    public Result<String> logExceptionDemo(
            @Parameter(description = "参数(输入'error'触发异常，输入非数字触发格式异常)") 
            @RequestParam String param) {
        String result = logDemoService.logExceptionDemo(param);
        return Result.success(result);
    }
} 