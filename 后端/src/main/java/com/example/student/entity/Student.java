package com.example.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生实体
 *
 * @author example
 */
@Data
@TableName("student")
@Schema(description = "学生实体")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "学生ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "入学日期")
    private LocalDate admissionDate;

    @Schema(description = "毕业日期")
    private LocalDate graduationDate;

    @Schema(description = "性别 0:男 1:女")
    private Integer gender;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "出生日期")
    private LocalDate birthday;

    @Schema(description = "家庭住址")
    private String address;

    @Schema(description = "状态 0:在读 1:毕业 2:休学 3:退学")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    /**
     * 学生姓名
     */
    @TableField(exist = false)
    private String name;
    
    /**
     * 手机号码
     */
    @TableField(exist = false)
    private String phone;
    
    /**
     * 电子邮箱
     */
    @TableField(exist = false)
    private String email;
    
    /**
     * 班级名称（非数据库字段）
     */
    @TableField(exist = false)
    private String className;
    
    /**
     * 专业名称（非数据库字段）
     */
    @TableField(exist = false)
    private String majorName;
    
    /**
     * 院系名称（非数据库字段）
     */
    @TableField(exist = false)
    private String departmentName;
    
    /**
     * 获取入学年份
     */
    public String getAdmissionYear() {
        if (admissionDate != null) {
            return String.valueOf(admissionDate.getYear());
        }
        return null;
    }
} 