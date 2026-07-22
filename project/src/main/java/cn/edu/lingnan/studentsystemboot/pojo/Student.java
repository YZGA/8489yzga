package cn.edu.lingnan.studentsystemboot.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student")
public class Student {
    @TableId
    private Integer id;
    private String studentNo;
    private String name;
    private String gender;
    private String idCard;
    private String phone;
    private String email;
    private String clazz;
    private String status;
    private String enrollDate;
    private String avatar;
}