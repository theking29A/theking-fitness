package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.CalorieRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalorieRecordRepository extends JpaRepository<CalorieRecord, Long> {
    
    List<CalorieRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<CalorieRecord> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, LocalDateTime after);
}
