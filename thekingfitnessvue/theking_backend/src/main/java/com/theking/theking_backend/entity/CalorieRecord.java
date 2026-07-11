package com.theking.theking_backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CalorieRecord {

    private Long id;
    private Long userId;
    private Double weight;
    private Double height;
    private Integer age;
    private String gender;
    private String activity;
    private Double bmr;
    private Double tdee;
    private Double recommendation;
    private LocalDateTime createdAt;
}
