package com.theking.theking_backend.service;

import com.theking.theking_backend.entity.SystemSetting;
import com.theking.theking_backend.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SystemSettingService {

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Autowired
    private OperationLogService operationLogService;

    public List<SystemSetting> findAll() {
        return systemSettingRepository.findAllByOrderByCategoryAscSettingKeyAsc();
    }

    public List<SystemSetting> findByCategory(String category) {
        return systemSettingRepository.findByCategoryOrderBySettingKeyAsc(category);
    }

    public String getValue(String key) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(null);
    }

    public String getValue(String key, String defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(defaultValue);
    }

    public int getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getValue(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    public Map<String, Object> getMaintenanceStatus() {
        Map<String, Object> result = new HashMap<>();
        boolean enabled = getBooleanValue("maintenance.enabled", false);
        result.put("enabled", enabled);
        result.put("message", getValue("maintenance.message", "系统正在维护中，请稍后再试。"));
        return result;
    }

    @Transactional
    public SystemSetting updateValue(String key, String value, Long adminId, String adminAccount) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElseThrow(() -> new RuntimeException("配置项不存在: " + key));
        String oldValue = setting.getSettingValue();
        setting.setSettingValue(value);
        SystemSetting saved = systemSettingRepository.save(setting);
        operationLogService.log(adminId, adminAccount, "UPDATE", "SYSTEM",
                key, "修改系统设置: " + key + " = " + value + " (原值: " + oldValue + ")");
        return saved;
    }

    @Transactional
    public void batchUpdate(Map<String, String> settings, Long adminId, String adminAccount) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            updateValue(entry.getKey(), entry.getValue(), adminId, adminAccount);
        }
    }
}
