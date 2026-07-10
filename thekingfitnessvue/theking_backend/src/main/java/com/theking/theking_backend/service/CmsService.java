package com.theking.theking_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theking.theking_backend.entity.*;
import com.theking.theking_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CmsService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private PlanExerciseRepository planExerciseRepository;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== Exercise 健身动作管理 ==========

    public Page<Exercise> listExercises(Pageable pageable) {
        return exerciseRepository.findByStatusOrderBySortOrderAsc(1, pageable);
    }

    public Page<Exercise> listAllExercises(Pageable pageable) {
        return exerciseRepository.findAll(pageable);
    }

    public Optional<Exercise> getExercise(Long id) {
        return exerciseRepository.findById(id);
    }

    @Transactional
    public Exercise createExercise(Exercise exercise, Long adminId, String adminAccount) {
        exercise.setStatus(1);
        Exercise saved = exerciseRepository.save(exercise);
        // 记录操作日志
        operationLogService.log(adminId, adminAccount, "CREATE", "EXERCISE",
                saved.getId().toString(), "创建健身动作: " + saved.getName());
        return saved;
    }

    @Transactional
    public Exercise updateExercise(Long id, Exercise exercise, Long adminId, String adminAccount) {
        Exercise existing = exerciseRepository.findById(id)
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
        Exercise saved = exerciseRepository.save(existing);
        operationLogService.log(adminId, adminAccount, "UPDATE", "EXERCISE",
                saved.getId().toString(), "更新健身动作: " + saved.getName());
        return saved;
    }

    @Transactional
    public void deleteExercise(Long id, Long adminId, String adminAccount) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("动作不存在"));
        exercise.setStatus(0);
        exerciseRepository.save(exercise);
        operationLogService.log(adminId, adminAccount, "DELETE", "EXERCISE",
                id.toString(), "下架健身动作: " + exercise.getName());
    }

    @Transactional
    public void toggleExerciseStatus(Long id, Long adminId, String adminAccount) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("动作不存在"));
        exercise.setStatus(exercise.getStatus() == 1 ? 0 : 1);
        exerciseRepository.save(exercise);
        String action = exercise.getStatus() == 1 ? "上架" : "下架";
        operationLogService.log(adminId, adminAccount, "UPDATE", "EXERCISE",
                id.toString(), action + "健身动作: " + exercise.getName());
    }

    // ========== Training Plan 训练计划管理 ==========

    public Page<TrainingPlan> listPlans(Pageable pageable) {
        return trainingPlanRepository.findByStatusOrderBySortOrderAsc(1, pageable);
    }

    public Page<TrainingPlan> listAllPlans(Pageable pageable) {
        return trainingPlanRepository.findAll(pageable);
    }

    public Optional<TrainingPlan> getPlan(Long id) {
        return trainingPlanRepository.findById(id);
    }

    @Transactional
    public TrainingPlan createPlan(TrainingPlan plan, Long adminId, String adminAccount) {
        plan.setStatus(1);
        TrainingPlan saved = trainingPlanRepository.save(plan);
        operationLogService.log(adminId, adminAccount, "CREATE", "PLAN",
                saved.getId().toString(), "创建训练计划: " + saved.getName());
        return saved;
    }

    @Transactional
    public TrainingPlan updatePlan(Long id, TrainingPlan plan, Long adminId, String adminAccount) {
        TrainingPlan existing = trainingPlanRepository.findById(id)
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
        TrainingPlan saved = trainingPlanRepository.save(existing);
        operationLogService.log(adminId, adminAccount, "UPDATE", "PLAN",
                saved.getId().toString(), "更新训练计划: " + saved.getName());
        return saved;
    }

    @Transactional
    public void deletePlan(Long id, Long adminId, String adminAccount) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计划不存在"));
        plan.setStatus(0);
        trainingPlanRepository.save(plan);
        operationLogService.log(adminId, adminAccount, "DELETE", "PLAN",
                id.toString(), "下架训练计划: " + plan.getName());
    }

    @Transactional
    public void togglePlanStatus(Long id, Long adminId, String adminAccount) {
        TrainingPlan plan = trainingPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计划不存在"));
        plan.setStatus(plan.getStatus() == 1 ? 0 : 1);
        trainingPlanRepository.save(plan);
        String action = plan.getStatus() == 1 ? "上架" : "下架";
        operationLogService.log(adminId, adminAccount, "UPDATE", "PLAN",
                id.toString(), action + "训练计划: " + plan.getName());
    }

    // ========== Plan Exercise 关联管理 ==========

    public List<PlanExercise> getPlanExercises(Long planId) {
        return planExerciseRepository.findByPlanIdOrderByDayNumberAscOrderIndexAsc(planId);
    }

    public List<PlanExercise> getPlanDayExercises(Long planId, Integer dayNumber) {
        return planExerciseRepository.findByPlanIdAndDayNumberOrderByOrderIndexAsc(planId, dayNumber);
    }

    @Transactional
    public void setPlanExercises(Long planId, List<PlanExercise> exercises, Long adminId, String adminAccount) {
        // 删除旧的关联
        planExerciseRepository.deleteByPlanId(planId);
        // 插入新的关联
        for (PlanExercise pe : exercises) {
            pe.setPlanId(planId);
            planExerciseRepository.save(pe);
        }
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("计划不存在"));
        operationLogService.log(adminId, adminAccount, "UPDATE", "PLAN",
                planId.toString(), "更新计划动作安排: " + plan.getName() + ", 共" + exercises.size() + "个动作");
    }

    // ========== 统计 ==========

    public Map<String, Object> getCmsStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalExercises", exerciseRepository.countByStatus(1));
        stats.put("totalPlans", trainingPlanRepository.countByStatus(1));
        stats.put("exercisesByCategory", exerciseRepository.countByCategory());
        return stats;
    }
}
