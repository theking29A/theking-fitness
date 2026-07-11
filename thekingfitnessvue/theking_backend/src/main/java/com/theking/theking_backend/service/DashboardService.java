package com.theking.theking_backend.service;

import com.theking.theking_backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired
    private TrainingPlanMapper trainingPlanMapper;

    @Autowired
    private UserActivityMapper userActivityMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;

    // ========== 核心统计 ==========

    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime weekStart = todayStart.minusDays(6);
        LocalDateTime monthStart = todayStart.minusDays(29);

        stats.put("totalUsers", userMapper.count());
        stats.put("totalExercises", exerciseMapper.countByStatus(1));
        stats.put("totalPlans", trainingPlanMapper.countByStatus(1));
        
        // 今日新增
        long todayRegister = userMapper.countByCreatedAtGreaterThanEqual(todayStart);
        stats.put("todayRegister", todayRegister);

        // 周活跃
        Long weekActive = userActivityMapper.countActiveUsersSince(weekStart);
        stats.put("weekActiveUsers", weekActive != null ? weekActive : 0);

        // 月活跃
        Long monthActive = userActivityMapper.countActiveUsersSince(monthStart);
        stats.put("monthActiveUsers", monthActive != null ? monthActive : 0);

        return stats;
    }

    // ========== 用户增长趋势（最近30天） ==========

    public List<Map<String, Object>> getUserGrowthTrend(int days) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = now.toLocalDate().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            long dailyNew = userMapper.countByCreatedAtBetween(dayStart, dayEnd);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("newUsers", dailyNew);
            result.add(item);
        }
        return result;
    }

    // ========== DAU 日活跃用户（最近30天） ==========

    public List<Map<String, Object>> getDailyActiveUsers(int days) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = now.toLocalDate().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            Long dau = userActivityMapper.countDailyActiveUsers(dayStart, dayEnd);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("dau", dau != null ? dau : 0);
            result.add(item);
        }
        return result;
    }

    // ========== 用户活动分布（最近7天） ==========

    public Map<String, Object> getActivityDistribution() {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        
        List<Map<String, Object>> activityData = userActivityMapper.countByActivityTypeSince(weekAgo);
        List<Map<String, Object>> activityList = new ArrayList<>();
        for (Map<String, Object> row : activityData) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", row.get("type"));
            item.put("count", row.get("count"));
            activityList.add(item);
        }
        result.put("activityTypes", activityList);

        // 操作日志类型分布
        List<Map<String, Object>> logData = operationLogMapper.countByOperationTypeSince(weekAgo);
        List<Map<String, Object>> logList = new ArrayList<>();
        for (Map<String, Object> row : logData) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", row.get("type"));
            item.put("count", row.get("count"));
            logList.add(item);
        }
        result.put("operationTypes", logList);
        
        return result;
    }

    // ========== 内容统计 ==========

    public Map<String, Object> getContentStats() {
        Map<String, Object> result = new HashMap<>();
        
        List<Object[]> categoryData = exerciseMapper.countByCategory();
        List<Map<String, Object>> categoryList = new ArrayList<>();
        for (Object[] row : categoryData) {
            Map<String, Object> item = new HashMap<>();
            item.put("category", row[0]);
            item.put("count", row[1]);
            categoryList.add(item);
        }
        result.put("exercisesByCategory", categoryList);
        result.put("totalExercises", exerciseMapper.countByStatus(1));
        result.put("totalPlans", trainingPlanMapper.countByStatus(1));
        
        return result;
    }
}
