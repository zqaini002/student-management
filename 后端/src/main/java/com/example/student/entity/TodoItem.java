package com.example.student.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 待办事项实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItem {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 状态（0：未完成，1：已完成）
     */
    private Integer status;
    
    /**
     * 到期时间
     */
    private LocalDateTime dueDate;
    
    /**
     * 优先级（0：低，1：中，2：高）
     */
    private Integer priority;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 