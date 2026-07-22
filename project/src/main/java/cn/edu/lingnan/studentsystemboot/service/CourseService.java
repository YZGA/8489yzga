package cn.edu.lingnan.studentsystemboot.service;

import cn.edu.lingnan.studentsystemboot.pojo.Course;
import java.util.List;

public interface CourseService {
    // 新增课程
    int addCourse(Course course);
    // 修改课程
    boolean updateCourse(Course course);
    // 根据id删除
    boolean deleteById(Integer id);
    // 根据id查询单个
    Course getById(Integer id);
    // 查询全部课程
    List<Course> listAll();
    // 关键词模糊查询
    List<Course> listByKeyword(String keyword);
}