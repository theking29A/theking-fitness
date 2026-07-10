package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.dto.WeightPredictionRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final String AI_SERVICE_URL = "http://localhost:8000";
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/predict-weight")
    public Result predictWeight(@RequestBody WeightPredictionRequest request) {
        try {
            // 构建请求
            Map<String, Object> body = new HashMap<>();
            body.put("history", request.getHistory());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 调用 Django AI 服务
            ResponseEntity<Map> response = restTemplate.postForEntity(
                AI_SERVICE_URL + "/predict/weight/",
                entity,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map data = (Map) response.getBody().get("data");
                return Result.success(data);
            } else {
                return Result.error("AI 预测服务返回异常");
            }

        } catch (Exception e) {
            // AI 服务不可用，返回备用预测（简单线性回归）
            return fallbackPredict(request.getHistory());
        }
    }

    private Result fallbackPredict(java.util.List<Double> history) {
        // 简单线性回归作为备用
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
}
