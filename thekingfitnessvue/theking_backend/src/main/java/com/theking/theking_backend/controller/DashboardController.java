package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.service.AdminService;
import com.theking.theking_backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AdminService adminService;

    private User getAdmin(String token) {
        User admin = adminService.findByToken(token);
        if (admin == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        return admin;
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview(@RequestParam String token) {
        getAdmin(token);
        return Result.success(dashboardService.getOverviewStats());
    }

    @GetMapping("/user-growth")
    public Result<List<Map<String, Object>>> getUserGrowth(
            @RequestParam String token,
            @RequestParam(defaultValue = "30") int days) {
        getAdmin(token);
        return Result.success(dashboardService.getUserGrowthTrend(days));
    }

    @GetMapping("/dau")
    public Result<List<Map<String, Object>>> getDailyActiveUsers(
            @RequestParam String token,
            @RequestParam(defaultValue = "30") int days) {
        getAdmin(token);
        return Result.success(dashboardService.getDailyActiveUsers(days));
    }

    @GetMapping("/activity-distribution")
    public Result<Map<String, Object>> getActivityDistribution(@RequestParam String token) {
        getAdmin(token);
        return Result.success(dashboardService.getActivityDistribution());
    }

    @GetMapping("/content-stats")
    public Result<Map<String, Object>> getContentStats(@RequestParam String token) {
        getAdmin(token);
        return Result.success(dashboardService.getContentStats());
    }
}
