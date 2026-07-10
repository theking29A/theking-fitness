package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.entity.UserActivity;
import com.theking.theking_backend.entity.UserTrainingRecord;
import com.theking.theking_backend.service.AdminService;
import com.theking.theking_backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AdminService adminService;

    private User getAdmin(String token) {
        User admin = adminService.findByToken(token);
        if (admin == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        return admin;
    }

    @GetMapping("/{id}/profile")
    public Result<Map<String, Object>> getProfile(@RequestParam String token, @PathVariable Long id) {
        getAdmin(token);
        return Result.success(userProfileService.getUserProfile(id));
    }

    @GetMapping("/{id}/activities")
    public Result<List<UserActivity>> getActivities(@RequestParam String token, @PathVariable Long id) {
        getAdmin(token);
        return Result.success(userProfileService.getUserActivities(id, 50));
    }

    @GetMapping("/{id}/training-records")
    public Result<Page<UserTrainingRecord>> getTrainingRecords(
            @RequestParam String token, @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        getAdmin(token);
        Pageable pageable = PageRequest.of(page, size, Sort.by("completedAt").descending());
        return Result.success(userProfileService.getUserTrainingRecords(id, pageable));
    }

    @GetMapping("/{id}/daily-stats")
    public Result<List<Map<String, Object>>> getDailyStats(
            @RequestParam String token, @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days) {
        getAdmin(token);
        return Result.success(userProfileService.getUserDailyStats(id, days));
    }

    @GetMapping("/{id}/favorite-exercises")
    public Result<List<Map<String, Object>>> getFavoriteExercises(@RequestParam String token, @PathVariable Long id) {
        getAdmin(token);
        return Result.success(userProfileService.getUserFavoriteExercises(id));
    }
}
