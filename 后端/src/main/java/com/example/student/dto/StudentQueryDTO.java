package com.example.student.dto;

import com.example.student.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生查询参数
 *
 * @author example
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "学生查询参数")
public class StudentQueryDTO extends PageRequest {

    @Schema(description = "学号", example = "S2020")
    private String studentNo;

    @Schema(description = "姓名", example = "张")
    private String name;

    @Schema(description = "班级ID", example = "1")
    private Long classId;

    @Schema(description = "性别 0:男 1:女", example = "0")
    private Integer gender;

    @Schema(description = "专业ID", example = "1")
    private Long majorId;

    @Schema(description = "院系ID", example = "1")
    private Long departmentId;

    @Schema(description = "手机号码", example = "13800000000")
    private String phone;

    @Schema(description = "状态 0:在读 1:毕业 2:休学 3:退学", example = "0")
    private Integer status;

    @Schema(description = "入学开始日期", example = "2020-09-01")
    private String startDate;

    @Schema(description = "入学结束日期", example = "2020-09-30")
    private String endDate;
} 