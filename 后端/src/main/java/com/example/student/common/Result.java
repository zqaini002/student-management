package com.example.student.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @author example
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码")
    private Integer code;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "数据")
    private T data;

    /**
     * 构造方法私有化，不允许外部直接创建
     */
    private Result() {
    }

    /**
     * 成功返回结果
     */
    public static <T> Result<T> success() {
        return success("操作成功");
    }

    /**
     * 成功返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> success(String message) {
        return success(message, null);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param message 提示信息
     * @param data    返回数据
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> error() {
        return error("操作失败");
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> error(String message) {
        return error(ResultCode.ERROR.getCode(), message);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 提示信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败返回结果
     *
     * @param resultCode 状态码枚举
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 失败返回结果
     *
     * @param resultCode 状态码枚举
     * @param message    提示信息
     */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return error(resultCode.getCode(), message);
    }
} 