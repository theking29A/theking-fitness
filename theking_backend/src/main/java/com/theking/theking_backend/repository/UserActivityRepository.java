package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    @Query("SELECT COUNT(DISTINCT ua.userId) FROM UserActivity ua WHERE ua.activityType = 'LOGIN' AND ua.createdAt >= :startTime AND ua.createdAt <= :endTime")
    Long countDailyActiveUsers(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(DISTINCT ua.userId) FROM UserActivity ua WHERE ua.createdAt >= :startTime")
    Long countActiveUsersSince(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT DATE(ua.createdAt) as date, COUNT(DISTINCT ua.userId) as dau " +
           "FROM UserActivity ua WHERE ua.createdAt >= :startTime AND ua.createdAt <= :endTime " +
           "GROUP BY DATE(ua.createdAt) ORDER BY DATE(ua.createdAt)")
    List<Object[]> getDailyActiveUsers(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT ua.activityType, COUNT(ua) FROM UserActivity ua WHERE ua.createdAt >= :startTime GROUP BY ua.activityType")
    List<Object[]> countByActivityTypeSince(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(DISTINCT ua.userId) FROM UserActivity ua WHERE ua.createdAt >= :startTime AND ua.activityType = :activityType")
    Long countUsersByActivityType(@Param("startTime") LocalDateTime startTime, @Param("activityType") String activityType);
}
