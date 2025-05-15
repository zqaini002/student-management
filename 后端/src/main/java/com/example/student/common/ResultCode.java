package com.example.student.common;

import lombok.Getter;

/**
 * 返回码枚举
 *
 * @author example
 */
@Getter
public enum ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数检验失败
     */
    VALIDATE_FAILED(400, "参数检验失败"),

    /**
     * 未登录或token已过期
     */
    UNAUTHORIZED(401, "未登录或token已过期"),

    /**
     * 没有相关权限
     */
    FORBIDDEN(403, "没有相关权限");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 提示信息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

} 