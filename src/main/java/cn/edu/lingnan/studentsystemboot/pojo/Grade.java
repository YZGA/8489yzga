package cn.edu.lingnan.studentsystemboot.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("grade")
public class Grade {
    @TableId
    private Integer id;

    private String studentNo;

    // grade数据表无student_name字段，标记非数据库字段
    @TableField(exist = false)
    private String studentName;

    private String courseNo;

    // grade数据表无course_name字段，标记非数据库字段
    @TableField(exist = false)
    private String courseName;

    private Double score;
    private Double gpa;
    private String term;
    private String examDate;

    @TableField(exist = false)
    private String createTime;
}