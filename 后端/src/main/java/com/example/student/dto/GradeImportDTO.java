package com.example.student.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 成绩导入DTO
 *
 * @author example
 */
@Data
public class GradeImportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("学号")
    private String studentNo;

    @ExcelProperty("学生姓名")
    private String studentName;

    @ExcelProperty("课程编码")
    private String courseCode;

    @ExcelProperty("课程名称")
    private String courseName;

    @ExcelProperty("学期")
    private String semester;

    @ExcelProperty("成绩")
    private Double score;

    @ExcelProperty("备注")
    private String comment;

    /**
     * 错误信息(导入失败时使用)
     */
    @ExcelProperty("错误信息")
    private String errorMsg;
}

