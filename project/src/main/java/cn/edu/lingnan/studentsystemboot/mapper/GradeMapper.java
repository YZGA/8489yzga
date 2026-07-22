package cn.edu.lingnan.studentsystemboot.mapper;

import cn.edu.lingnan.studentsystemboot.pojo.Grade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    // 分页列表联表查询
    Page<Grade> selectPageJoin(Page<Grade> page, @Param("q") String q);

    // 分页总数统计
    long selectPageCount(@Param("q") String q);

    // 新增：根据id联表查询单条成绩（详情页使用）
    Grade selectByIdJoin(@Param("id") Integer id);

    /**
     * 关联course表 根据课程id查询成绩
     */
    @Select("""
            SELECT
                g.id,
                g.student_no studentNo,
                s.name studentName,
                g.course_no courseNo,
                c.course_name courseName,
                g.score,
                g.gpa,
                g.term,
                g.exam_date examDate
            FROM grade g
            LEFT JOIN student s ON g.student_no = s.student_no
            LEFT JOIN course c ON g.course_no = c.course_no
            WHERE c.id = #{courseId}
            ORDER BY g.exam_date DESC,g.id DESC
            """)
    List<Grade> selectByCourseId(@Param("courseId") Integer courseId);

    /**
     * 课程号+分数筛选
     */
    @Select("""
            SELECT
                g.id,
                g.student_no studentNo,
                s.name studentName,
                g.course_no courseNo,
                c.course_name courseName,
                g.score,
                g.gpa,
                g.term,
                g.exam_date examDate
            FROM grade g
            LEFT JOIN student s ON g.student_no = s.student_no
            LEFT JOIN course c ON g.course_no = c.course_no
            WHERE g.course_no = #{courseNo} AND g.score ${condition} #{scoreVal}
            ORDER BY g.exam_date DESC,g.id DESC
            """)
    List<Grade> selectByCourseNoScore(
            @Param("courseNo") String courseNo,
            @Param("condition") String condition,
            @Param("scoreVal") Double scoreVal
    );

    @Select("SELECT term,AVG(score) avg_score FROM grade GROUP BY term ORDER BY term")
    List<java.util.Map<String,Object>> statTermAvg();

    @Select("SELECT course_name,AVG(score) avg_score FROM grade GROUP BY course_name")
    List<java.util.Map<String,Object>> statCourseAvg();

    @Select("SELECT ROUND(gpa,1) g,COUNT(*) cnt FROM grade GROUP BY ROUND(gpa,1) ORDER BY g")
    List<java.util.Map<String,Object>> statGpaDist();

    @Select("SELECT course_name,AVG(score) avg_score FROM grade GROUP BY course_name ORDER BY course_name")
    List<java.util.Map<String,Object>> statRadarData();
}