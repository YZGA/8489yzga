package cn.edu.lingnan.studentsystemboot.service.impl;

import cn.edu.lingnan.studentsystemboot.mapper.CourseMapper;
import cn.edu.lingnan.studentsystemboot.pojo.Course;
import cn.edu.lingnan.studentsystemboot.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public int addCourse(Course course) {
        courseMapper.insert(course);
        return course.getId();
    }

    @Override
    public boolean updateCourse(Course course) {
        return courseMapper.updateById(course) > 0;
    }

    @Override
    public boolean deleteById(Integer id) {
        return courseMapper.deleteById(id) > 0;
    }

    @Override
    public Course getById(Integer id) {
        return courseMapper.selectById(id);
    }

    @Override
    public List<Course> listAll() {
        LambdaQueryWrapper<Course> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByDesc(Course::getId);
        return courseMapper.selectList(wrapper);
    }

    @Override
    public List<Course> listByKeyword(String keyword) {
        LambdaQueryWrapper<Course> wrapper = Wrappers.lambdaQuery();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Course::getCourseNo, keyword)
                    .or()
                    .like(Course::getCourseName, keyword)
                    .or()
                    .like(Course::getTeacher, keyword);
        }
        wrapper.orderByDesc(Course::getId);
        return courseMapper.selectList(wrapper);
    }
}