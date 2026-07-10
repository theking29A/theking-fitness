package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.TrainingPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {

    Page<TrainingPlan> findByStatusOrderBySortOrderAsc(Integer status, Pageable pageable);

    List<TrainingPlan> findByGoalAndLevelAndStatus(String goal, String level, Integer status);

    Page<TrainingPlan> findByGoalAndStatus(String goal, Integer status, Pageable pageable);

    @Query("SELECT p FROM TrainingPlan p WHERE p.status = 1 AND " +
           "(p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<TrainingPlan> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    long countByStatus(Integer status);
}
