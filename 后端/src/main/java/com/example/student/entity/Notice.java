package com.example.student.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 系统公告实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 类型（0：普通公告，1：重要公告，2：紧急公告）
     */
    private Integer type;
    
    /**
     * 状态（0：草稿，1：已发布，2：已下线）
     */
    private Integer status;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 发布人ID
     */
    private Long publisherId;
    
    /**
     * 发布人姓名
     */
    private String publisherName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 