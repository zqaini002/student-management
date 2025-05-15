package com.example.student.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果类
 *
 * @param <T> 数据项类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "数据列表")
    private List<T> rows;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "当前页码")
    private int pageNum;

    @Schema(description = "每页大小")
    private int pageSize;

    /**
     * 构造函数
     *
     * @param rows 数据列表
     * @param total 总记录数
     */
    public PageResult(List<T> rows, long total) {
        this.rows = rows;
        this.total = total;
    }

    /**
     * 创建分页结果
     *
     * @param list     数据列表
     * @param total    总记录数
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param <T>      数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> build(List<T> list, long total, int pageNum, int pageSize) {
        return new PageResult<T>(list, total, pageNum, pageSize);
    }
    
    /**
     * 创建空的分页结果
     *
     * @param <T> 数据类型
     * @return 空的分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<T>(Collections.emptyList(), 0L, 1, 10);
    }
    
    /**
     * 创建指定类型的空分页结果
     *
     * @param <T> 数据类型
     * @param clazz 数据类型的Class对象
     * @return 空的分页结果
     */
    public static <T> PageResult<T> empty(Class<T> clazz) {
        return new PageResult<T>(Collections.emptyList(), 0L, 1, 10);
    }
    
    /**
     * 兼容方法 - 获取数据列表
     *
     * @return 数据列表
     */
    public List<T> getList() {
        return this.rows;
    }
} 