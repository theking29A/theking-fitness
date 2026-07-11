package com.theking.theking_backend.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.Exercise;
import com.theking.theking_backend.entity.PlanExercise;
import com.theking.theking_backend.entity.TrainingPlan;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.service.AdminService;
import com.theking.theking_backend.service.CmsService;
import com.theking.theking_backend.service.OperationLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/cms")
public class CmsController {

    @Autowired
    private CmsService cmsService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private OperationLogService operationLogService;

    private User getAdmin(String token) {
        User admin = adminService.findByToken(token);
        if (admin == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        return admin;
    }

    // ========== Exercise 健身动作 ==========

    @GetMapping("/exercises")
    public Result<PageInfo<Exercise>> listExercises(
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        getAdmin(token);
        PageHelper.startPage(page + 1, size);
        List<Exercise> list = cmsService.listAllExercises(page, size);
        return Result.success(new PageInfo<>(list));
    }

    @GetMapping("/exercises/{id}")
    public Result<Exercise> getExercise(@RequestParam String token, @PathVariable Long id) {
        getAdmin(token);
        Optional<Exercise> exercise = cmsService.getExercise(id);
        return exercise.map(Result::success).orElseGet(() -> Result.error(404, "动作不存在"));
    }

    @PostMapping("/exercises")
    public Result<Exercise> createExercise(@RequestParam String token, @Valid @RequestBody Exercise exercise) {
        User admin = getAdmin(token);
        return Result.success(cmsService.createExercise(exercise, admin.getId(), admin.getAccount()));
    }

    @PutMapping("/exercises/{id}")
    public Result<Exercise> updateExercise(@RequestParam String token, @PathVariable Long id, @Valid @RequestBody Exercise exercise) {
        User admin = getAdmin(token);
        return Result.success(cmsService.updateExercise(id, exercise, admin.getId(), admin.getAccount()));
    }

    @DeleteMapping("/exercises/{id}")
    public Result<Void> deleteExercise(@RequestParam String token, @PathVariable Long id) {
        User admin = getAdmin(token);
        cmsService.deleteExercise(id, admin.getId(), admin.getAccount());
        return Result.success();
    }

    @PostMapping("/exercises/{id}/toggle")
    public Result<Void> toggleExerciseStatus(@RequestParam String token, @PathVariable Long id) {
        User admin = getAdmin(token);
        cmsService.toggleExerciseStatus(id, admin.getId(), admin.getAccount());
        return Result.success();
    }

    // ========== Training Plan 训练计划 ==========

    @GetMapping("/plans")
    public Result<PageInfo<TrainingPlan>> listPlans(
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        getAdmin(token);
        PageHelper.startPage(page + 1, size);
        List<TrainingPlan> list = cmsService.listAllPlans(page, size);
        return Result.success(new PageInfo<>(list));
    }

    @GetMapping("/plans/{id}")
    public Result<TrainingPlan> getPlan(@RequestParam String token, @PathVariable Long id) {
        getAdmin(token);
        Optional<TrainingPlan> plan = cmsService.getPlan(id);
        return plan.map(Result::success).orElseGet(() -> Result.error(404, "计划不存在"));
    }

    @PostMapping("/plans")
    public Result<TrainingPlan> createPlan(@RequestParam String token, @Valid @RequestBody TrainingPlan plan) {
        User admin = getAdmin(token);
        return Result.success(cmsService.createPlan(plan, admin.getId(), admin.getAccount()));
    }

    @PutMapping("/plans/{id}")
    public Result<TrainingPlan> updatePlan(@RequestParam String token, @PathVariable Long id, @Valid @RequestBody TrainingPlan plan) {
        User admin = getAdmin(token);
        return Result.success(cmsService.updatePlan(id, plan, admin.getId(), admin.getAccount()));
    }

    @DeleteMapping("/plans/{id}")
    public Result<Void> deletePlan(@RequestParam String token, @PathVariable Long id) {
        User admin = getAdmin(token);
        cmsService.deletePlan(id, admin.getId(), admin.getAccount());
        return Result.success();
    }

    @PostMapping("/plans/{id}/toggle")
    public Result<Void> togglePlanStatus(@RequestParam String token, @PathVariable Long id) {
        User admin = getAdmin(token);
        cmsService.togglePlanStatus(id, admin.getId(), admin.getAccount());
        return Result.success();
    }

    // ========== Plan Exercise 关联 ==========

    @GetMapping("/plans/{planId}/exercises")
    public Result<List<PlanExercise>> getPlanExercises(@RequestParam String token, @PathVariable Long planId) {
        getAdmin(token);
        return Result.success(cmsService.getPlanExercises(planId));
    }

    @PostMapping("/plans/{planId}/exercises")
    public Result<Void> setPlanExercises(@RequestParam String token, @PathVariable Long planId, @RequestBody List<PlanExercise> exercises) {
        User admin = getAdmin(token);
        cmsService.setPlanExercises(planId, exercises, admin.getId(), admin.getAccount());
        return Result.success();
    }

    // ========== CMS 统计 ==========

    @GetMapping("/stats")
    public Result<Map<String, Object>> getCmsStats(@RequestParam String token) {
        getAdmin(token);
        return Result.success(cmsService.getCmsStats());
    }
}
