package com.theking.theking_backend.service;

import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.entity.UserActivity;
import com.theking.theking_backend.entity.UserTrainingRecord;
import com.theking.theking_backend.mapper.UserActivityMapper;
import com.theking.theking_backend.mapper.UserMapper;
import com.theking.theking_backend.mapper.UserTrainingRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private UserMapper userMapper;

    @Autowired
    private UserActivityMapper userActivityMapper;

    @Autowired
    private UserTrainingRecordMapper userTrainingRecordMapper;

    public Map<String, Object> getUserProfile(Long userId) {
        Map<String, Object> profile = new HashMap<>();
        
        // 基本信息
        Optional<User> userOpt = userMapper.findById(userId);
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
        Long totalWorkouts = userTrainingRecordMapper.countByUserId(userId);
        Integer totalCalories = userTrainingRecordMapper.sumCaloriesByUserId(userId);
        Integer totalDuration = userTrainingRecordMapper.sumDurationByUserId(userId);
        
        profile.put("totalWorkouts", totalWorkouts != null ? totalWorkouts : 0);
        profile.put("totalCalories", totalCalories != null ? totalCalories : 0);
        profile.put("totalDuration", totalDuration != null ? totalDuration : 0);
        
        return profile;
    }

    public List<UserActivity> getUserActivities(Long userId, int limit) {
        // 从 Mapper 获取全部活动，然后在内存中过滤、排序、限制
        List<UserActivity> allActivities = userActivityMapper.selectAll();
        return allActivities.stream()
                .filter(a -> a.getUserId() != null && a.getUserId().equals(userId))
                .sorted((a, b) -> {
                    if (b.getCreatedAt() == null) return -1;
                    if (a.getCreatedAt() == null) return 1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(limit)
                .toList();
    }

    public Page<UserTrainingRecord> getUserTrainingRecords(Long userId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int size = pageable.getPageSize();
        List<UserTrainingRecord> list = userTrainingRecordMapper.findByUserIdOrderByCompletedAtDesc(userId, offset, size);
        long total = userTrainingRecordMapper.countByUserId(userId);
        return new PageImpl<>(list, pageable, total);
    }

    public List<Map<String, Object>> getUserDailyStats(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<Map<String, Object>> stats = userTrainingRecordMapper.getDailyStatsByUserId(userId, startTime);
        
        return stats.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", row.get("date"));
            item.put("count", row.get("count"));
            item.put("duration", row.get("duration"));
            return item;
        }).toList();
    }

    public List<Map<String, Object>> getUserFavoriteExercises(Long userId) {
        List<Map<String, Object>> favorites = userTrainingRecordMapper.getFavoriteExercisesByUserId(userId);
        return favorites.stream().map(row -> {
            Map<String, Object> item = new HashMap<>();
            item.put("exerciseId", row.get("exerciseId"));
            item.put("count", row.get("count"));
            return item;
        }).toList();
    }
}
