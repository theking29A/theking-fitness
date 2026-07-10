package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.PlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Long> {

    List<PlanExercise> findByPlanIdOrderByDayNumberAscOrderIndexAsc(Long planId);

    List<PlanExercise> findByPlanIdAndDayNumberOrderByOrderIndexAsc(Long planId, Integer dayNumber);

    void deleteByPlanId(Long planId);

    long countByPlanId(Long planId);
}
