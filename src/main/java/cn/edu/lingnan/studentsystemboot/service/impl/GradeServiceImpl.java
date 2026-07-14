package cn.edu.lingnan.studentsystemboot.service.impl;

import cn.edu.lingnan.studentsystemboot.mapper.GradeMapper;
import cn.edu.lingnan.studentsystemboot.pojo.Grade;
import cn.edu.lingnan.studentsystemboot.service.GradeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GradeServiceImpl implements GradeService {

    // 修复：补上变量名 gradeMapper
    @Autowired
    private GradeMapper gradeMapper;

    /** 统一空值兜底 */
    private Grade fillDefault(Grade g) {
        if (g == null) return null;
        Grade res = new Grade();
        res.setId(g.getId());
        res.setStudentNo(g.getStudentNo() == null || g.getStudentNo().isBlank() ? "未知学号" : g.getStudentNo());
        res.setStudentName(g.getStudentName() == null || g.getStudentName().isBlank() ? "未知学生" : g.getStudentName());
        res.setCourseNo(g.getCourseNo() == null ? "" : g.getCourseNo());
        res.setCourseName(g.getCourseName() == null ? "" : g.getCourseName());
        res.setScore(g.getScore() == null ? 0.0 : g.getScore());
        res.setGpa(g.getGpa() == null ? 0.0 : g.getGpa());
        res.setTerm(g.getTerm() == null || g.getTerm().isBlank() ? "未知学期" : g.getTerm());
        res.setExamDate(g.getExamDate() == null || g.getExamDate().isBlank() ? "未填写" : g.getExamDate());
        res.setCreateTime("");
        return res;
    }

    private List<Grade> fillList(List<Grade> list) {
        List<Grade> target = new ArrayList<>();
        for (Grade g : list) {
            target.add(fillDefault(g));
        }
        return target;
    }

    @Override
    public int add(Grade grade) {
        gradeMapper.insert(grade);
        return grade.getId();
    }

    @Override
    public boolean update(Grade grade) {
        // 打印入参，调试更新失败问题
        System.out.println("【更新成绩入参】grade = " + grade);
        int affectedRows = gradeMapper.updateById(grade);
        System.out.println("【数据库受影响行数】" + affectedRows);
        return affectedRows > 0;
    }

    @Override
    public boolean deleteById(Integer id) {
        return gradeMapper.deleteById(id) > 0;
    }

    // 修改：使用联表查询，详情页带出学生、课程名称
    @Override
    public Grade getById(Integer id) {
        Grade g = gradeMapper.selectByIdJoin(id);
        return fillDefault(g);
    }

    /**
     * 重构分页方法：统一使用联表分页SQL，消除两套查询逻辑冲突
     * 单条SQL完成分页+左连接学生/课程+多字段模糊搜索，不再多次查询数据库
     */
    @Override
    public Page<Grade> pageList(String q, Integer pageNum, Integer pageSize) {
        // 第三个参数false：关闭MyBatisPlus自动执行count查询
        Page<Grade> page = new Page<>(pageNum, pageSize, false);
        // 查询分页数据
        Page<Grade> pageData = gradeMapper.selectPageJoin(page, q);
        // 手动查询总记录数
        long totalRecords = gradeMapper.selectPageCount(q);
        pageData.setTotal(totalRecords);
        // 统一空值填充处理
        pageData.setRecords(fillList(pageData.getRecords()));
        return pageData;
    }

    // 修改：联表查询全量数据，导出Excel携带姓名、课程名
    @Override
    public List<Grade> listAll() {
        Page<Grade> page = new Page<>(1, Integer.MAX_VALUE, false);
        Page<Grade> pageData = gradeMapper.selectPageJoin(page, null);
        return fillList(pageData.getRecords());
    }

    @Override
    public List<Grade> listByStudentNo(String studentNo) {
        LambdaQueryWrapper<Grade> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Grade::getStudentNo, studentNo)
                .orderByDesc(Grade::getExamDate, Grade::getId);
        return fillList(gradeMapper.selectList(wrapper));
    }

    @Override
    public List<Grade> listByCourseNo(String courseNo) {
        LambdaQueryWrapper<Grade> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Grade::getCourseNo, courseNo)
                .orderByDesc(Grade::getId);
        return fillList(gradeMapper.selectList(wrapper));
    }

    @Override
    public List<Grade> listByCourseNoFilter(String courseNo, String scoreType, Double scoreValue) {
        String cond = scoreType.equals("gte") ? ">=" : "<=";
        // 修复原代码缺失scoreVal传参bug
        List<Grade> raw = gradeMapper.selectByCourseNoScore(courseNo, cond, scoreValue);
        return fillList(raw);
    }

    @Override
    public List<Grade> listByCourseId(Integer courseId) {
        List<Grade> raw = gradeMapper.selectByCourseId(courseId);
        return fillList(raw);
    }

    // 废弃：单表搜索方法，前端统一使用pageList分页搜索接口
    /*
    @Override
    public List<Grade> search(String q) {
        LambdaQueryWrapper<Grade> wrapper = Wrappers.lambdaQuery();
        if (q != null && !q.isBlank()) {
            wrapper.like(Grade::getStudentNo, q)
                    .or().like(Grade::getCourseNo, q)
                    .or().like(Grade::getTerm, q)
                    .or().like(Grade::getCourseName, q);
        }
        wrapper.orderByDesc(Grade::getId);
        return fillList(gradeMapper.selectList(wrapper));
    }
    */

    // 废弃：单表统计总数，分页统一使用selectPageCount
    /*
    @Override
    public long countTotal(String q) {
        LambdaQueryWrapper<Grade> wrapper = Wrappers.lambdaQuery();
        if (q != null && !q.isBlank()) {
            wrapper.like(Grade::getStudentNo, q)
                    .or().like(Grade::getCourseNo, q)
                    .or().like(Grade::getTerm, q)
                    .or().like(Grade::getCourseName, q);
        }
        return gradeMapper.selectCount(wrapper);
    }
    */

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> termData = gradeMapper.statTermAvg();
        List<String> terms = new ArrayList<>();
        List<Double> termAvg = new ArrayList<>();
        for (Map<String, Object> m : termData) {
            terms.add(String.valueOf(m.get("term")));
            termAvg.add(Double.parseDouble(String.valueOf(m.get("avg_score"))));
        }
        result.put("terms", terms);
        result.put("term_avg", termAvg);

        List<Map<String, Object>> courseData = gradeMapper.statCourseAvg();
        List<String> courseNames = new ArrayList<>();
        List<Double> courseAvg = new ArrayList<>();
        for (Map<String, Object> m : courseData) {
            courseNames.add(String.valueOf(m.get("course_name")));
            courseAvg.add(Double.parseDouble(String.valueOf(m.get("avg_score"))));
        }
        result.put("course_names", courseNames);
        result.put("course_avg", courseAvg);

        Map<String, Integer> gpaMap = new HashMap<>();
        List<Map<String, Object>> gpaData = gradeMapper.statGpaDist();
        for (Map<String, Object> m : gpaData) {
            gpaMap.put(String.valueOf(m.get("g")), Integer.parseInt(String.valueOf(m.get("cnt"))));
        }
        result.put("gpa_map", gpaMap);

        List<Map<String, Object>> radar = new ArrayList<>();
        List<Map<String, Object>> radarData = gradeMapper.statRadarData();
        for (Map<String, Object> m : radarData) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", String.valueOf(m.get("course_name")));
            item.put("avg", Double.parseDouble(String.valueOf(m.get("avg_score"))));
            radar.add(item);
        }
        result.put("radar", radar);
        return result;
    }
}