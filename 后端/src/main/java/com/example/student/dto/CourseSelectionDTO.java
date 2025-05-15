package com.example.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 选课数据传输对象
 */
@Data
@Schema(description = "选课信息")
public class CourseSelectionDTO {

    /**
     * 选课ID
     */
    @Schema(description = "选课ID")
    @NotNull(message = "选课ID不能为空")
    private Long id;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    private Long studentId;

    /**
     * 课程开设ID
     */
    @Schema(description = "课程开设ID")
    private Long courseOfferingId;

    /**
     * 选课状态：0-已选，1-已退，2-已修完
     */
    @Schema(description = "选课状态：0-已选，1-已退，2-已修完")
    private Integer status;

    /**
     * 选课时间
     */
    @Schema(description = "选课时间")
    private LocalDateTime selectionTime;

    @Schema(description = "分数")
    private BigDecimal score;

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