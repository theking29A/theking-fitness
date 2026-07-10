package com.theking.theking_backend.service;

import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.entity.UserActivity;
import com.theking.theking_backend.entity.UserTrainingRecord;
import com.theking.theking_backend.repository.UserActivityRepository;
import com.theking.theking_backend.repository.UserRepository;
import com.theking.theking_backend.repository.UserTrainingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private UserTrainingRecordRepository userTrainingRecordRepository;

    public Map<String, Object> getUserProfile(Long userId) {
        Map<String, Object> profile = new HashMap<>();
        
        // 基本信息
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            profile.put("id", user.getId());
            profile.put("account", user.getAccount());
            profile.put("nickname", user.getNickname());
            profile.put("email", user.getEmail());
            profile.put("role", user.getRole());
            profile.put("createdAt", user.getCreatedAt());
        }
        
        // 训练统计
        Long totalWorkouts = userTrainingRecordRepository.countByUserId(userId);
        Integer totalCalories = userTrainingRecordRepository.sumCaloriesByUserId(userId);
        Integer totalDuration = userTrainingRecordRepository.sumDurationByUserId(userId);
        
        profile.put("totalWorkouts", totalWorkouts != null ? totalWorkouts : 0);
        profile.put("totalCalories", totalCalories != null ? totalCalories : 0);
        profile.put("totalDuration", totalDuration != null ? totalDuration : 0);
        
        return profile;
    }

    public List<UserActivity> getUserActivities(Long userId, int limit) {
        // 这里需要按时间倒序，但 Repository 方法没有限制数量的，用 Pageable 处理
        return userActivityRepository.findAll().stream()
                .filter(a -> a.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .toList();
    }

    public Page<UserTrainingRecord> getUserTrainingRecords(Long userId, Pageable pageable) {
        return userTrainingRecordRepository.findByUserIdOrderByCompletedAtDesc(userId, pageable);
    }

    public List<Map<String, Object>> getUserDailyStats(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Object[]> stats = userTrainingRecordRepository.getDailyStatsByUserId(userId, startTime);
        
        return stats.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", row[0]);
            item.put("count", row[1]);
            item.put("duration", row[2]);
            return item;
        }).toList();
    }

    public List<Map<String, Object>> getUserFavoriteExercises(Long userId) {
        List<Object[]> favorites = userTrainingRecordRepository.getFavoriteExercisesByUserId(userId);
        return favorites.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("exerciseId", row[0]);
            item.put("count", row[1]);
            return item;
        }).toList();
    }
}
