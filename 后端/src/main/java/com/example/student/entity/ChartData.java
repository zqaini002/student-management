package com.example.student.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * 图表数据实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartData {
    
    /**
     * 标签数组
     */
    private List<String> labels;
    
    /**
     * 数据数组（单系列）
     */
    private List<Integer> data;
    
    /**
     * 多系列数据（键为系列名称，值为数据数组）
     */
    private Map<String, List<Integer>> seriesData;
    
    /**
     * 多系列浮点数据（键为系列名称，值为浮点数据数组）
     */
    private Map<String, List<Double>> seriesDataDouble;
    
    /**
     * 图表类型
     */
    private String chartType;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 单位
     */
    private String unit;
} 