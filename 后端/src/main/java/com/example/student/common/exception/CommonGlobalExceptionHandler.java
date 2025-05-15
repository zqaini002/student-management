package com.example.student.common.exception;

import com.example.student.common.utils.LogUtils;
import com.example.student.common.Result;
import com.example.student.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;

/**
 * 通用全局异常处理器
 *
 * @author example
 */
@RestControllerAdvice
public class CommonGlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CommonGlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        LogUtils.error(log, "业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError error : fieldErrors) {
            errorMsg.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(", ");
        }
        String message = errorMsg.toString();
        LogUtils.warn(log, "参数校验异常: {}", message);
        return Result.error(message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            errorMsg.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(", ");
        }
        String message = errorMsg.toString();
        LogUtils.warn(log, "绑定异常: {}", message);
        return Result.error(message);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = "参数类型不匹配: " + e.getName() + " 应该为 " + e.getRequiredType().getName();
        LogUtils.warn(log, message);
        return Result.error(message);
    }

    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = "缺少必要参数: " + e.getParameterName();
        LogUtils.warn(log, message);
        return Result.error(message);
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = "不支持的请求方法: " + e.getMethod();
        LogUtils.warn(log, message);
        return Result.error(message);
    }

    /**
     * 处理访问权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDeniedException(AccessDeniedException e) {
        String message = "没有访问权限: " + e.getMessage();
        LogUtils.warn(log, message);
        return Result.error(403, message);
    }

    /**
     * 处理唯一键冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException e) {
        String message = "数据已存在，请勿重复添加";
        LogUtils.error(log, "唯一键冲突: {}", e.getMessage());
        return Result.error(message);
    }

    /**
     * 处理SQL语法错误异常
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Result<?> handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String message = "SQL语法错误";
        LogUtils.error(log, "SQL语法错误: {}", e.getMessage());
        return Result.error(message);
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        LogUtils.error(log, "系统异常: ", e);
        return Result.error("系统异常，请联系管理员");
    }
} 