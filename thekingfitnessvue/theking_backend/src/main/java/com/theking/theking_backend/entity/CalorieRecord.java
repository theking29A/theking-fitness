package com.theking.theking_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "calorie_records")
@Data
public class CalorieRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Double weight;
    
    @Column(nullable = false)
    private Double height;
    
    @Column(nullable = false)
    private Integer age;
    
    @Column(nullable = false, length = 10)
    private String gender;
    
    @Column(nullable = false, length = 20)
    private String activity;
    
    @Column(nullable = false)
    private Double bmr;
    
    @Column(nullable = false)
    private Double tdee;
    
    @Column(nullable = false)
    private Double recommendation;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
