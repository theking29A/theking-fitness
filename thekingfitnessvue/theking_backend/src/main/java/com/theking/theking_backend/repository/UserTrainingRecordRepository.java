package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.UserTrainingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserTrainingRecordRepository extends JpaRepository<UserTrainingRecord, Long> {

    Page<UserTrainingRecord> findByUserIdOrderByCompletedAtDesc(Long userId, Pageable pageable);

    List<UserTrainingRecord> findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(
            Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(utr) FROM UserTrainingRecord utr WHERE utr.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(utr.caloriesBurned) FROM UserTrainingRecord utr WHERE utr.userId = :userId")
    Integer sumCaloriesByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(utr.durationSeconds) FROM UserTrainingRecord utr WHERE utr.userId = :userId")
    Integer sumDurationByUserId(@Param("userId") Long userId);

    @Query("SELECT DATE(utr.completedAt) as date, COUNT(utr) as count, SUM(utr.durationSeconds) as duration " +
           "FROM UserTrainingRecord utr WHERE utr.userId = :userId AND utr.completedAt >= :startTime " +
           "GROUP BY DATE(utr.completedAt) ORDER BY DATE(utr.completedAt) DESC")
    List<Object[]> getDailyStatsByUserId(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT utr.exerciseId, COUNT(utr) as count FROM UserTrainingRecord utr WHERE utr.userId = :userId GROUP BY utr.exerciseId ORDER BY count DESC")
    List<Object[]> getFavoriteExercisesByUserId(@Param("userId") Long userId);
}
