package cn.edu.lingnan.studentsystemboot.service;

import java.util.Map;

public interface DashboardService {
    /**
     * 获取仪表盘首页全部统计数据
     * @return 封装好的大屏所有图表数据
     */
    Map<String, Object> getDashboardData();
}