package com.example.student.service.impl;

import com.example.student.common.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 日志使用示例服务
 *
 * @author example
 */
@Service
public class LogDemoServiceImpl {
    
    private static final Logger logger = LoggerFactory.getLogger(LogDemoServiceImpl.class);
    
    /**
     * 演示不同级别日志记录
     *
     * @param param 参数
     * @return 处理结果
     */
    public String logDemo(String param) {
        // 使用日志工具类记录不同级别的日志
        LogUtils.debug(logger, "调试信息: 参数={}", param);
        LogUtils.info(logger, "处理业务开始: 参数={}", param);
        
        try {
            // 模拟业务处理
            if (param == null || param.isEmpty()) {
                LogUtils.warn(logger, "参数为空，使用默认值");
                param = "默认参数";
            }
            
            // 记录业务操作日志
            LogUtils.logOperation("日志示例", "查询操作", "查询参数: " + param, logger);
            
            // 返回处理结果
            String result = "处理结果: " + param;
            LogUtils.info(logger, "处理业务完成: 结果={}", result);
            return result;
            
        } catch (Exception e) {
            // 异常日志记录
            LogUtils.error(logger, "处理业务异常: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 演示异常日志记录
     *
     * @param param 参数
     * @return 处理结果
     */
    public String logExceptionDemo(String param) {
        LogUtils.info(logger, "执行可能出现异常的操作: 参数={}", param);
        
        try {
            // 模拟异常场景
            if ("error".equals(param)) {
                throw new RuntimeException("模拟业务异常");
            }
            
            int number = Integer.parseInt(param);
            return "数值处理结果: " + (number * 10);
            
        } catch (NumberFormatException e) {
            LogUtils.error(logger, "参数格式异常，无法转换为数字: {}", param, e);
            throw new IllegalArgumentException("参数必须是数字");
        } catch (Exception e) {
            LogUtils.error(logger, "业务处理异常", e);
            throw e;
        }
    }
} 