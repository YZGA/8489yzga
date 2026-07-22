package cn.edu.lingnan.studentsystemboot.service.impl;

import cn.edu.lingnan.studentsystemboot.mapper.StudentMapper;
import cn.edu.lingnan.studentsystemboot.pojo.Student;
import cn.edu.lingnan.studentsystemboot.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public int addStudent(Student student) {
        studentMapper.insert(student);
        return student.getId();
    }

    @Override
    public boolean updateStudent(Student student) {
        return studentMapper.updateById(student) > 0;
    }

    @Override
    public boolean deleteById(Integer id) {
        return studentMapper.deleteById(id) > 0;
    }

    @Override
    public Student getById(Integer id) {
        return studentMapper.selectById(id);
    }

    @Override
    public Page<Student> pageList(String q, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Student> wrapper = Wrappers.lambdaQuery();
        if (q != null && !q.isBlank()) {
            wrapper.like(Student::getStudentNo, q)
                    .or()
                    .like(Student::getName, q)
                    .or()
                    .like(Student::getClazz, q);
        }
        wrapper.orderByAsc(Student::getStudentNo);
        Page<Student> page = new Page<>(pageNum, pageSize);
        return studentMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Student> listAllByQ(String q) {
        LambdaQueryWrapper<Student> wrapper = Wrappers.lambdaQuery();
        if (q != null && !q.isBlank()) {
            wrapper.like(Student::getStudentNo, q)
                    .or()
                    .like(Student::getName, q)
                    .or()
                    .like(Student::getClazz, q);
        }
        wrapper.orderByAsc(Student::getStudentNo);
        List<Student> list = studentMapper.selectList(wrapper);
        fillAvatar(list);
        return list;
    }

    @Override
    public List<Student> listByIds(List<Integer> idList) {
        List<Student> list = studentMapper.selectBatchIds(idList);
        fillAvatar(list);
        return list;
    }

    // 统一填充头像链接，对应原 mapRow 里头像生成逻辑
    private void fillAvatar(List<Student> list) {
        for (Student s : list) {
            if (s.getName() != null) {
                try {
                    String seed = java.net.URLEncoder.encode(s.getName(), java.nio.charset.StandardCharsets.UTF_8);
                    s.setAvatar("https://api.dicebear.com/7.x/initials/svg?seed=" + seed);
                } catch (Exception ignore) {
                }
            }
        }
    }
}