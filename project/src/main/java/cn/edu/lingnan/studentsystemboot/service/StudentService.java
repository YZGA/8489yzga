package cn.edu.lingnan.studentsystemboot.service;

import cn.edu.lingnan.studentsystemboot.pojo.Student;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface StudentService {
    // 新增学生
    int addStudent(Student student);

    // 修改学生
    boolean updateStudent(Student student);

    // 根据id删除
    boolean deleteById(Integer id);

    // 根据id查询单个
    Student getById(Integer id);

    // 分页条件查询
    Page<Student> pageList(String q, Integer pageNum, Integer pageSize);

    // 条件查询全部（用于导出Excel）
    List<Student> listAllByQ(String q);

    // 根据id集合批量查询（导出勾选数据）
    List<Student> listByIds(List<Integer> idList);
}