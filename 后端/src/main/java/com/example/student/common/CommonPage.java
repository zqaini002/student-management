package com.example.student.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 分页数据封装类
 *
 * @author example
 */
public class CommonPage<T> {
    /**
     * 当前页码
     */
    private Long pageNum;
    
    /**
     * 每页数量
     */
    private Long pageSize;
    
    /**
     * 总页数
     */
    private Long totalPage;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 分页数据
     */
    private List<T> list;

    /**
     * 将MyBatis Plus分页结果转化为通用结果
     */
    public static <T> CommonPage<T> restPage(IPage<T> pageResult) {
        CommonPage<T> result = new CommonPage<T>();
        result.setPageNum(pageResult.getCurrent());
        result.setPageSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setTotalPage(pageResult.getPages());
        result.setList(pageResult.getRecords());
        return result;
    }

    /**
     * 将MyBatis Plus分页结果转化为通用结果
     */
    public static <T> CommonPage<T> restPage(Page<T> pageResult) {
        CommonPage<T> result = new CommonPage<T>();
        result.setPageNum(pageResult.getCurrent());
        result.setPageSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setTotalPage(pageResult.getPages());
        result.setList(pageResult.getRecords());
        return result;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
} 