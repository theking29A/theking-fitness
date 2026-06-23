package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    Page<OperationLog> findByAdminIdOrderByCreatedAtDesc(Long adminId, Pageable pageable);

    Page<OperationLog> findByOperationTypeOrderByCreatedAtDesc(String operationType, Pageable pageable);

    Page<OperationLog> findByTargetTypeOrderByCreatedAtDesc(String targetType, Pageable pageable);

    @Query("SELECT o FROM OperationLog o WHERE " +
           "(:adminId IS NULL OR o.adminId = :adminId) AND " +
           "(:operationType IS NULL OR o.operationType = :operationType) AND " +
           "(:targetType IS NULL OR o.targetType = :targetType) AND " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime)" +
           "ORDER BY o.createdAt DESC")
    Page<OperationLog> findWithFilters(
            @Param("adminId") Long adminId,
            @Param("operationType") String operationType,
            @Param("targetType") String targetType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    @Query("SELECT o.operationType, COUNT(o) FROM OperationLog o WHERE o.createdAt >= :startTime GROUP BY o.operationType")
    List<Object[]> countByOperationTypeSince(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT o.targetType, COUNT(o) FROM OperationLog o WHERE o.createdAt >= :startTime GROUP BY o.targetType")
    List<Object[]> countByTargetTypeSince(@Param("startTime") LocalDateTime startTime);
}
