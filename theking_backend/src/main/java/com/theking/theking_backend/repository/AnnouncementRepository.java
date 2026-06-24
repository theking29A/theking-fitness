package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    Page<Announcement> findByStatusOrderBySortOrderAscCreatedAtDesc(Integer status, Pageable pageable);

    Page<Announcement> findAllByOrderBySortOrderAscCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM Announcement a WHERE a.status = 1 AND a.target IN ('all', 'user') " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now) " +
           "ORDER BY a.sortOrder ASC, a.createdAt DESC")
    List<Announcement> findActiveForUser(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Announcement a WHERE a.status = 1 AND a.target IN ('all', 'admin') " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now) " +
           "ORDER BY a.sortOrder ASC, a.createdAt DESC")
    List<Announcement> findActiveForAdmin(@Param("now") LocalDateTime now);
}
