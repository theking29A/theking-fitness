package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.OperationLog;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.service.AdminService;
import com.theking.theking_backend.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private AdminService adminService;

    private User getAdmin(String token) {
        User admin = adminService.findByToken(token);
        if (admin == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        return admin;
    }

    @GetMapping("/list")
    public Result<Page<OperationLog>> listLogs(
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        getAdmin(token);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return Result.success(operationLogService.listLogs(pageable));
    }

    @GetMapping("/my")
    public Result<Page<OperationLog>> listMyLogs(
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User admin = getAdmin(token);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return Result.success(operationLogService.listLogsByAdmin(admin.getId(), pageable));
    }
}
