package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.PageResult;
import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.Announcement;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.service.AdminService;
import com.theking.theking_backend.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

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
    public Result<PageResult<Announcement>> list(
            @RequestParam String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        getAdmin(token);
        return Result.success(announcementService.listAll(page, size));
    }

    @GetMapping("/active")
    public Result<List<Announcement>> getActiveForAdmin(@RequestParam String token) {
        getAdmin(token);
        return Result.success(announcementService.getActiveForAdmin());
    }

    @PostMapping
    public Result<Announcement> create(@RequestParam String token, @Valid @RequestBody Announcement announcement) {
        User admin = getAdmin(token);
        return Result.success(announcementService.create(announcement, admin.getId(), admin.getAccount()));
    }

    @PutMapping("/{id}")
    public Result<Announcement> update(@RequestParam String token, @PathVariable Long id, @Valid @RequestBody Announcement announcement) {
        User admin = getAdmin(token);
        return Result.success(announcementService.update(id, announcement, admin.getId(), admin.getAccount()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestParam String token, @PathVariable Long id) {
        User admin = getAdmin(token);
        announcementService.delete(id, admin.getId(), admin.getAccount());
        return Result.success();
    }

    @PostMapping("/{id}/toggle")
    public Result<Void> toggleStatus(@RequestParam String token, @PathVariable Long id) {
        User admin = getAdmin(token);
        announcementService.toggleStatus(id, admin.getId(), admin.getAccount());
        return Result.success();
    }
}
