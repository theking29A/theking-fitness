package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PublicController {

    @Autowired
    private SystemSettingService systemSettingService;

    @GetMapping("/maintenance-status")
    public Result<Map<String, Object>> getMaintenanceStatus() {
        return Result.success(systemSettingService.getMaintenanceStatus());
    }
}
