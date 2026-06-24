package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.SystemSetting;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.service.AdminService;
import com.theking.theking_backend.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings")
public class SystemSettingController {

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private AdminService adminService;

    private User getAdmin(String token) {
        User admin = adminService.findByToken(token);
        if (admin == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        return admin;
    }

    @GetMapping("/all")
    public Result<List<SystemSetting>> getAll(@RequestParam String token) {
        getAdmin(token);
        return Result.success(systemSettingService.findAll());
    }

    @GetMapping("/category/{category}")
    public Result<List<SystemSetting>> getByCategory(@RequestParam String token, @PathVariable String category) {
        getAdmin(token);
        return Result.success(systemSettingService.findByCategory(category));
    }

    @PostMapping("/update")
    public Result<SystemSetting> update(@RequestParam String token, @RequestBody Map<String, String> request) {
        User admin = getAdmin(token);
        String key = request.get("key");
        String value = request.get("value");
        return Result.success(systemSettingService.updateValue(key, value, admin.getId(), admin.getAccount()));
    }

    @PostMapping("/batch-update")
    public Result<Void> batchUpdate(@RequestParam String token, @RequestBody Map<String, String> settings) {
        User admin = getAdmin(token);
        systemSettingService.batchUpdate(settings, admin.getId(), admin.getAccount());
        return Result.success();
    }
}
