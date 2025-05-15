package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 选课实体类
 *
 * @author example
 */
@Data
@TableName("course_selection")
public class CourseSelection implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选课ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程开设ID
     */
    private Long courseOfferingId;

    /**
     * 选课状态：0-已选，1-已退，2-已修完
     */
    private Integer status;

    /**
     * 成绩
     */
    private BigDecimal score;

    /**
     * 选课时间
     */
    private LocalDateTime selectionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 兼容方法 - 设置选课状态
     */
    public void setSelectionStatus(Integer selectionStatus) {
        this.status = selectionStatus;
    }
    
    /**
     * 兼容方法 - 获取选课状态
     */
    public Integer getSelectionStatus() {
        return this.status;
    }
} 