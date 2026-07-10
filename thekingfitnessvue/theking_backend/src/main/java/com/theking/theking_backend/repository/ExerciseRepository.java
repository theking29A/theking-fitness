package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    Page<Exercise> findByStatusOrderBySortOrderAsc(Integer status, Pageable pageable);

    List<Exercise> findByCategoryAndStatusOrderBySortOrderAsc(String category, Integer status);

    Page<Exercise> findByCategoryAndStatus(String category, Integer status, Pageable pageable);

    @Query("SELECT e FROM Exercise e WHERE e.status = 1 AND " +
           "(e.name LIKE %:keyword% OR e.description LIKE %:keyword% OR e.targetMuscles LIKE %:keyword%)")
    Page<Exercise> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e.category, COUNT(e) FROM Exercise e WHERE e.status = 1 GROUP BY e.category")
    List<Object[]> countByCategory();

    long countByStatus(Integer status);
}
