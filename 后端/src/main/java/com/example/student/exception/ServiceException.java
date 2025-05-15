package com.example.student.exception;

/**
 * 服务异常类
 * 
 * @author example
 */
public class ServiceException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ServiceException(String message) {
        this(500, message);
    }
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
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
     * 获取错误消息
     * 
     * @return 错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }
} 