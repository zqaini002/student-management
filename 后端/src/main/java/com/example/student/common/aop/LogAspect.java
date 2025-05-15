package com.example.student.common.aop;

import com.example.student.common.utils.LogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 日志切面，用于记录接口请求日志
 *
 * @author example
 */
@Aspect
@Component
public class LogAspect {
    
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 定义切点 - 所有controller包下的方法
     */
    @Pointcut("execution(* com.example.student.controller..*.*(..))")
    public void controllerLog() {
    }
    
    /**
     * 前置通知 - 在方法执行前记录请求信息
     */
    @Before("controllerLog()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            // 设置追踪ID
            TRACE_ID.set(UUID.randomUUID().toString().replace("-", ""));
            // 记录开始时间
            START_TIME.set(System.currentTimeMillis());
            
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 构建请求信息
                Map<String, Object> requestInfo = new HashMap<>(8);
                requestInfo.put("traceId", TRACE_ID.get());
                requestInfo.put("url", request.getRequestURL().toString());
                requestInfo.put("method", request.getMethod());
                requestInfo.put("ip", getClientIp(request));
                requestInfo.put("class", joinPoint.getSignature().getDeclaringTypeName());
                requestInfo.put("function", joinPoint.getSignature().getName());
                requestInfo.put("args", Arrays.toString(joinPoint.getArgs()));
                
                // 记录请求日志
                LogUtils.info(log, "接口请求: {}", objectMapper.writeValueAsString(requestInfo));
            }
        } catch (Exception e) {
            LogUtils.error(log, "记录请求日志异常", e);
        }
    }
    
    /**
     * 环绕通知 - 记录方法执行时间
     */
    @Around("controllerLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            // 执行方法
            Object result = proceedingJoinPoint.proceed();
            
            // 计算执行时间
            long endTime = System.currentTimeMillis();
            long startTime = START_TIME.get();
            long executeTime = endTime - startTime;
            
            // 记录执行时间
            if (executeTime > 500) {
                LogUtils.warn(log, "接口 [{}] 执行时间: {}ms, 请检查性能问题", 
                        proceedingJoinPoint.getSignature().getName(), executeTime);
            } else {
                LogUtils.info(log, "接口 [{}] 执行时间: {}ms", 
                        proceedingJoinPoint.getSignature().getName(), executeTime);
            }
            
            return result;
        } finally {
            // 清理ThreadLocal
            START_TIME.remove();
        }
    }
    
    /**
     * 后置通知 - 在方法执行后记录响应信息
     */
    @AfterReturning(returning = "result", pointcut = "controllerLog()")
    public void doAfterReturning(Object result) {
        try {
            // 构建响应信息
            Map<String, Object> responseInfo = new HashMap<>(4);
            responseInfo.put("traceId", TRACE_ID.get());
            responseInfo.put("result", result);
            
            // 记录响应日志
            LogUtils.info(log, "接口响应: {}", objectMapper.writeValueAsString(responseInfo));
        } catch (Exception e) {
            LogUtils.error(log, "记录响应日志异常", e);
        } finally {
            // 清理ThreadLocal
            TRACE_ID.remove();
        }
    }
    
    /**
     * 异常通知 - 在方法抛出异常时记录
     */
    @AfterThrowing(pointcut = "controllerLog()", throwing = "e")
    public void doAfterThrowing(Throwable e) {
        LogUtils.error(log, "接口异常 [traceId:{}]: {}", TRACE_ID.get(), e.getMessage(), e);
        
        // 清理ThreadLocal
        TRACE_ID.remove();
        START_TIME.remove();
    }
    
    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 