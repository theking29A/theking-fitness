package com.theking.theking_backend.dto;

import lombok.Data;

@Data
public class WeightPredictionResponse {
    private Double currentWeight;
    private Double predictedWeight;
    private String trend;
    private Double change;
}
