package cn.edu.lingnan.studentsystemboot.controller;

import cn.edu.lingnan.studentsystemboot.common.Result;
import cn.edu.lingnan.studentsystemboot.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public Result<Map<String, Object>> getDashboardInfo() {
        Map<String, Object> data = dashboardService.getDashboardData();
        return Result.success(data);
    }
}