package com.example.student.handler;

import com.example.student.common.Result;
import com.example.student.common.ResultCode;
import com.example.student.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author example
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @return 响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     *
     * @param e 异常
     * @return 结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常: {}", message);
        return Result.error(ResultCode.VALIDATE_FAILED, message);
    }

    /**
     * 处理绑定异常
     *
     * @param e 异常
     * @return 结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定异常: {}", message);
        return Result.error(ResultCode.VALIDATE_FAILED, message);
    }

    /**
     * 处理约束违反异常
     *
     * @param e 异常
     * @return 结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("约束违反异常: {}", e.getMessage());
        return Result.error(ResultCode.VALIDATE_FAILED, e.getMessage());
    }

    /**
     * 处理认证异常
     *
     * @param e 异常
     * @return 结果
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常: {}", e.getMessage());
        return Result.error(ResultCode.UNAUTHORIZED, "认证失败，请重新登录");
    }

    /**
     * 处理凭证异常
     *
     * @param e 异常
     * @return 结果
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.error("凭证异常: {}", e.getMessage());
        return Result.error(ResultCode.UNAUTHORIZED, "用户名或密码错误");
    }

    /**
     * 处理访问拒绝异常
     *
     * @param e 异常
     * @return 结果
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("访问拒绝异常: {}", e.getMessage());
        return Result.error(ResultCode.FORBIDDEN, "没有权限访问该资源");
    }

    /**
     * 处理其他异常
     *
     * @param e 异常
     * @return 结果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ResultCode.ERROR, "系统异常，请联系管理员");
    }
} 