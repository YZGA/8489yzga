package cn.edu.lingnan.studentsystemboot.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course")
public class Course {
    @TableId
    private Integer id;
    private String courseNo;
    private String courseName;
    private String teacher;
    private Integer credit;
}