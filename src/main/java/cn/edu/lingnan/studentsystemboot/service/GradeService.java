package cn.edu.lingnan.studentsystemboot.service;

import cn.edu.lingnan.studentsystemboot.pojo.Grade;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

public interface GradeService {
    // 新增
    int add(Grade grade);
    // 修改
    boolean update(Grade grade);
    // 删除
    boolean deleteById(Integer id);
    // 单条查询
    Grade getById(Integer id);
    // 分页列表+搜索
    Page<Grade> pageList(String q, Integer pageNum, Integer pageSize);
    // 无分页全部
    List<Grade> listAll();
    // 根据学号查
    List<Grade> listByStudentNo(String studentNo);
    // 根据课程号查
    List<Grade> listByCourseNo(String courseNo);
    // 课程号+分数筛选
    List<Grade> listByCourseNoFilter(String courseNo, String scoreType, Double scoreValue);
    // 课程ID关联查询
    List<Grade> listByCourseId(Integer courseId);
    // 统计大屏数据
    Map<String,Object> getStatistics();
    // 关键词搜索

}