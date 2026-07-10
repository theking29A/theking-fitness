package com.theking.theking_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class WeightPredictionRequest {
    private List<Double> history;  // 历史体重数据
}
