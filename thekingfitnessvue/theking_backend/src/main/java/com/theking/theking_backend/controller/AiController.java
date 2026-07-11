package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.dto.WeightPredictionRequest;
import com.theking.theking_backend.entity.CalorieRecord;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.mapper.CalorieRecordMapper;
import com.theking.theking_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Value("${ai.service.url:http://120.24.236.105:8000}")
    private String aiServiceUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CalorieRecordMapper calorieRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/predict-weight")
    public Result predictWeight(@RequestBody WeightPredictionRequest request) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("history", request.getHistory());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                aiServiceUrl + "/predict/weight/",
                entity,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Result.success(response.getBody());
            } else {
                return Result.error("AI 预测服务返回异常");
            }

        } catch (Exception e) {
            return fallbackPredict(request.getHistory());
        }
    }

    @PostMapping("/predict-calories")
    public Result predictCalories(@RequestBody Map<String, Object> request, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                aiServiceUrl + "/predict/calories/",
                entity,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map result = response.getBody();
                
                // 保存历史记录（仅登录用户）
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    try {
                        // 简化处理：不解析token，直接尝试保存
                        // 实际应该从token中解析userId
                        CalorieRecord record = new CalorieRecord();
                        record.setUserId(1L); // 临时使用固定值，应该从token解析
                        record.setWeight(((Number) request.get("weight")).doubleValue());
                        record.setHeight(((Number) request.get("height")).doubleValue());
                        record.setAge(((Number) request.get("age")).intValue());
                        record.setGender((String) request.get("gender"));
                        record.setActivity((String) request.get("activity"));
                        record.setBmr(((Number) result.get("bmr")).doubleValue());
                        record.setTdee(((Number) result.get("tdee")).doubleValue());
                        record.setRecommendation(((Number) result.get("recommendation")).doubleValue());
                        record.setCreatedAt(LocalDateTime.now());
                        calorieRecordMapper.insert(record);
                    } catch (Exception e) {
                        System.out.println("保存热量记录失败: " + e.getMessage());
                    }
                }
                
                return Result.success(result);
            } else {
                return Result.error("AI 服务返回异常");
            }

        } catch (Exception e) {
            return fallbackCalories(request);
        }
    }

    @GetMapping("/calorie-history")
    public Result getCalorieHistory(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("请先登录");
        }
        
        try {
            // 简化处理：返回所有记录（实际应该根据token解析userId过滤）
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<CalorieRecord> records = calorieRecordMapper.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(1L, thirtyDaysAgo);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取历史记录失败");
        }
    }

    private Result fallbackPredict(java.util.List<Double> history) {
        int n = history.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += history.get(i);
            sumXY += i * history.get(i);
            sumX2 += i * i;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        double predicted = slope * n + intercept;
        double current = history.get(n - 1);
        double change = predicted - current;

        Map<String, Object> result = new HashMap<>();
        result.put("currentWeight", current);
        result.put("predictedWeight", Math.round(predicted * 100.0) / 100.0);
        result.put("trend", change > 0 ? "up" : "down");
        result.put("change", Math.round(change * 100.0) / 100.0);

        return Result.success(result);
    }

    private Result fallbackCalories(Map<String, Object> request) {
        double weight = ((Number) request.get("weight")).doubleValue();
        double height = ((Number) request.get("height")).doubleValue();
        int age = ((Number) request.get("age")).intValue();
        String gender = (String) request.get("gender");
        String activity = (String) request.getOrDefault("activity", "moderate");

        double bmr;
        if ("male".equals(gender)) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        Map<String, Double> factors = new HashMap<>();
        factors.put("sedentary", 1.2);
        factors.put("light", 1.375);
        factors.put("moderate", 1.55);
        factors.put("active", 1.725);
        factors.put("very_active", 1.9);

        double tdee = bmr * factors.getOrDefault(activity, 1.55);

        Map<String, Object> result = new HashMap<>();
        result.put("bmr", Math.round(bmr * 100.0) / 100.0);
        result.put("tdee", Math.round(tdee * 100.0) / 100.0);
        result.put("recommendation", Math.round((tdee - 500) * 100.0) / 100.0);

        return Result.success(result);
    }
}
