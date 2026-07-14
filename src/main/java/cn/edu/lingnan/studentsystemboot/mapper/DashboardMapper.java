package cn.edu.lingnan.studentsystemboot.mapper;

import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

public interface DashboardMapper {

    // 1. 四大总数：学生、课程、成绩、系统用户
    @Select("SELECT " +
            "(SELECT COUNT(*) FROM student) AS studentTotal," +
            "(SELECT COUNT(*) FROM course) AS courseTotal," +
            "(SELECT COUNT(*) FROM grade) AS gradeTotal," +
            "(SELECT COUNT(*) FROM user) AS userTotal")
    Map<String, Long> countAllTotal();

    // 2. 按学期分组 学期+平均分
    @Select("SELECT term, AVG(score) AS avgScore FROM grade GROUP BY term ORDER BY term")
    List<Map<String, Object>> statTermAvg();

    // 3. 按课程分组 课程名+课程平均分
    @Select("SELECT course_name, AVG(score) AS avgScore FROM grade GROUP BY course_name ORDER BY course_name")
    List<Map<String, Object>> statCourseAvg();

    // 4. GPA四舍五入1位小数，分组统计人数
    @Select("SELECT ROUND(gpa,1) AS gpa, COUNT(*) AS count FROM grade GROUP BY ROUND(gpa,1) ORDER BY gpa")
    List<Map<String, Object>> statGpaGroup();

    // 5. 雷达图：课程名称+对应平均分
    @Select("SELECT course_name, AVG(score) AS avg FROM grade GROUP BY course_name ORDER BY course_name")
    List<Map<String, Object>> statRadarData();
}