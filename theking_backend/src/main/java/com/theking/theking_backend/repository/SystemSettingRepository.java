package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    Optional<SystemSetting> findBySettingKey(String settingKey);

    List<SystemSetting> findByCategoryOrderBySettingKeyAsc(String category);

    List<SystemSetting> findAllByOrderByCategoryAscSettingKeyAsc();
}
