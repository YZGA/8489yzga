package cn.edu.lingnan.studentsystemboot.service.impl;

import cn.edu.lingnan.studentsystemboot.mapper.DashboardMapper;
import cn.edu.lingnan.studentsystemboot.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();

        // 1. 四个总数量
        Map<String, Long> totalMap = dashboardMapper.countAllTotal();
        result.put("studentTotal", totalMap.get("studentTotal"));
        result.put("courseTotal", totalMap.get("courseTotal"));
        result.put("gradeTotal", totalMap.get("gradeTotal"));
        result.put("userTotal", totalMap.get("userTotal"));

        // 2. 学期数组 + 学期平均分数组
        List<Map<String, Object>> termList = dashboardMapper.statTermAvg();
        List<String> termArr = new ArrayList<>();
        List<Double> termAvgArr = new ArrayList<>();
        for (Map<String, Object> item : termList) {
            termArr.add(String.valueOf(item.get("term")));
            termAvgArr.add(Double.parseDouble(String.valueOf(item.get("avgScore"))));
        }
        result.put("terms", termArr);
        result.put("termAvg", termAvgArr);

        // 3. 课程名称数组 + 课程平均分数组
        List<Map<String, Object>> courseList = dashboardMapper.statCourseAvg();
        List<String> courseNameArr = new ArrayList<>();
        List<Double> courseAvgArr = new ArrayList<>();
        for (Map<String, Object> item : courseList) {
            courseNameArr.add(String.valueOf(item.get("course_name")));
            courseAvgArr.add(Double.parseDouble(String.valueOf(item.get("avgScore"))));
        }
        result.put("courseNames", courseNameArr);
        result.put("courseAvg", courseAvgArr);

        // 4. GPA分布键值对
        Map<String, Integer> gpaMap = new HashMap<>();
        List<Map<String, Object>> gpaList = dashboardMapper.statGpaGroup();
        for (Map<String, Object> item : gpaList) {
            String gpaKey = String.valueOf(item.get("gpa"));
            Integer cnt = Integer.parseInt(String.valueOf(item.get("count")));
            gpaMap.put(gpaKey, cnt);
        }
        result.put("gpaMap", gpaMap);

        // 5. 雷达图数据
        List<Map<String, Object>> radarSource = dashboardMapper.statRadarData();
        List<Map<String, Object>> radarList = new ArrayList<>();
        for (Map<String, Object> item : radarSource) {
            Map<String, Object> radarItem = new HashMap<>();
            radarItem.put("name", item.get("course_name"));
            radarItem.put("avg", Double.parseDouble(String.valueOf(item.get("avg"))));
            radarList.add(radarItem);
        }
        result.put("radar", radarList);

        return result;
    }
}