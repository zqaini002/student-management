package com.example.student.common.exception;

import com.example.student.common.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 * 
 * @author example
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误信息
     */
    private String message;
    
    /**
     * 构造方法
     * 
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
        this.message = message;
    }
    
    /**
     * 构造方法
     * 
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    /**
     * 构造方法
     * 
     * @param resultCode 返回码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    
    /**
     * 构造方法
     * 
     * @param resultCode 返回码枚举
     * @param message    错误信息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }
    
    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    @Override
    public String getMessage() {
        return message;
    }
} 