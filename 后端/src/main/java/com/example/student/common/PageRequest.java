package com.example.student.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页请求参数
 *
 * @author example
 */
@Data
@Schema(description = "分页请求参数")
public class PageRequest {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "10")
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 1000, message = "每页数量最大为1000")
    private Integer pageSize = 10;
} 