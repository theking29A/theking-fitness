package com.theking.theking_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theking.theking_backend.entity.*;
import com.theking.theking_backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CmsService {

    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired
    private TrainingPlanMapper trainingPlanMapper;

    @Autowired
    private PlanExerciseMapper planExerciseMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== Exercise 健身动作管理 ==========

    public List<Exercise> listExercises(int page, int size) {
        // 使用 PageHelper 或手动分页，这里返回全部由 Controller 层处理分页
        return exerciseMapper.findByStatusOrderBySortOrderAsc(1);
    }

    public List<Exercise> listAllExercises(int page, int size) {
        // 由 Controller 或分页插件处理 LIMIT
        return exerciseMapper.findByStatusOrderBySortOrderAsc(1);
    }

    public Optional<Exercise> getExercise(Long id) {
        return exerciseMapper.findById(id);
    }

    @Transactional
    public Exercise createExercise(Exercise exercise, Long adminId, String adminAccount) {
        exercise.setStatus(1);
        exerciseMapper.insert(exercise);
        // 记录操作日志
        operationLogService.log(adminId, adminAccount, "CREATE", "EXERCISE",
                exercise.getId().toString(), "创建健身动作: " + exercise.getName());
        return exercise;
    }

    @Transactional
    public Exercise updateExercise(Long id, Exercise exercise, Long adminId, String adminAccount) {
        Exercise existing = exerciseMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("动作不存在"));
        existing.setName(exercise.getName());
        existing.setDescription(exercise.getDescription());
        existing.setCategory(exercise.getCategory());
        existing.setTargetMuscles(exercise.getTargetMuscles());
        existing.setDifficulty(exercise.getDifficulty());
        existing.setEquipment(exercise.getEquipment());
        existing.setVideoUrl(exercise.getVideoUrl());
        existing.setImageUrl(exercise.getImageUrl());
        existing.setTips(exercise.getTips());
        existing.setCaloriesPerMin(exercise.getCaloriesPerMin());
        existing.setDurationSeconds(exercise.getDurationSeconds());
        existing.setSortOrder(exercise.getSortOrder());
        exerciseMapper.update(existing);
        operationLogService.log(adminId, adminAccount, "UPDATE", "EXERCISE",
                existing.getId().toString(), "更新健身动作: " + existing.getName());
        return existing;
    }

    @Transactional
    public void deleteExercise(Long id, Long adminId, String adminAccount) {
        Exercise exercise = exerciseMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("动作不存在"));
        exercise.setStatus(0);
        exerciseMapper.updateStatus(id, 0);
        operationLogService.log(adminId, adminAccount, "DELETE", "EXERCISE",
                id.toString(), "下架健身动作: " + exercise.getName());
    }

    @Transactional
    public void toggleExerciseStatus(Long id, Long adminId, String adminAccount) {
        Exercise exercise = exerciseMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("动作不存在"));
        int newStatus = exercise.getStatus() == 1 ? 0 : 1;
        exerciseMapper.updateStatus(id, newStatus);
        String action = newStatus == 1 ? "上架" : "下架";
        operationLogService.log(adminId, adminAccount, "UPDATE", "EXERCISE",
                id.toString(), action + "健身动作: " + exercise.getName());
    }

    // ========== Training Plan 训练计划管理 ==========

    public List<TrainingPlan> listPlans(int page, int size) {
        return trainingPlanMapper.findByStatusOrderBySortOrderAsc(1);
    }

    public List<TrainingPlan> listAllPlans(int page, int size) {
        return trainingPlanMapper.findByStatusOrderBySortOrderAsc(1);
    }

    public Optional<TrainingPlan> getPlan(Long id) {
        return trainingPlanMapper.findById(id);
    }

    @Transactional
    public TrainingPlan createPlan(TrainingPlan plan, Long adminId, String adminAccount) {
        plan.setStatus(1);
        trainingPlanMapper.insert(plan);
        operationLogService.log(adminId, adminAccount, "CREATE", "PLAN",
                plan.getId().toString(), "创建训练计划: " + plan.getName());
        return plan;
    }

    @Transactional
    public TrainingPlan updatePlan(Long id, TrainingPlan plan, Long adminId, String adminAccount) {
        TrainingPlan existing = trainingPlanMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("计划不存在"));
        existing.setName(plan.getName());
        existing.setDescription(plan.getDescription());
        existing.setGoal(plan.getGoal());
        existing.setLevel(plan.getLevel());
        existing.setDurationWeeks(plan.getDurationWeeks());
        existing.setDaysPerWeek(plan.getDaysPerWeek());
        existing.setCoverImage(plan.getCoverImage());
        existing.setEstimatedTimeMin(plan.getEstimatedTimeMin());
        existing.setSortOrder(plan.getSortOrder());
        trainingPlanMapper.update(existing);
        operationLogService.log(adminId, adminAccount, "UPDATE", "PLAN",
                existing.getId().toString(), "更新训练计划: " + existing.getName());
        return existing;
    }

    @Transactional
    public void deletePlan(Long id, Long adminId, String adminAccount) {
        TrainingPlan plan = trainingPlanMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("计划不存在"));
        trainingPlanMapper.updateStatus(id, 0);
        operationLogService.log(adminId, adminAccount, "DELETE", "PLAN",
                id.toString(), "下架训练计划: " + plan.getName());
    }

    @Transactional
    public void togglePlanStatus(Long id, Long adminId, String adminAccount) {
        TrainingPlan plan = trainingPlanMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("计划不存在"));
        int newStatus = plan.getStatus() == 1 ? 0 : 1;
        trainingPlanMapper.updateStatus(id, newStatus);
        String action = newStatus == 1 ? "上架" : "下架";
        operationLogService.log(adminId, adminAccount, "UPDATE", "PLAN",
                id.toString(), action + "训练计划: " + plan.getName());
    }

    // ========== Plan Exercise 关联管理 ==========

    public List<PlanExercise> getPlanExercises(Long planId) {
        return planExerciseMapper.findByPlanIdOrderByDayNumberAscOrderIndexAsc(planId);
    }

    public List<PlanExercise> getPlanDayExercises(Long planId, Integer dayNumber) {
        return planExerciseMapper.findByPlanIdAndDayNumberOrderByOrderIndexAsc(planId, dayNumber);
    }

    @Transactional
    public void setPlanExercises(Long planId, List<PlanExercise> exercises, Long adminId, String adminAccount) {
        // 删除旧的关联
        planExerciseMapper.deleteByPlanId(planId);
        // 插入新的关联
        for (PlanExercise pe : exercises) {
            pe.setPlanId(planId);
            planExerciseMapper.insert(pe);
        }
        TrainingPlan plan = trainingPlanMapper.findById(planId)
                .orElseThrow(() -> new RuntimeException("计划不存在"));
        operationLogService.log(adminId, adminAccount, "UPDATE", "PLAN",
                planId.toString(), "更新计划动作安排: " + plan.getName() + ", 共" + exercises.size() + "个动作");
    }

    // ========== 统计 ==========

    public Map<String, Object> getCmsStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalExercises", exerciseMapper.countByStatus(1));
        stats.put("totalPlans", trainingPlanMapper.countByStatus(1));
        stats.put("exercisesByCategory", exerciseMapper.countByCategory());
        return stats;
    }
}
