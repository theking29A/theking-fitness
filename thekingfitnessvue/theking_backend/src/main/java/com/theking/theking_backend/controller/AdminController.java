package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.common.PageResult;
import com.theking.theking_backend.dto.admin.AdminLoginRequest;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.service.AdminService;
import com.theking.theking_backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.theking.theking_backend.service.OperationLogService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String ADMIN_TOKEN_PREFIX = "admin:token:";
    private static final long TOKEN_EXPIRE_DAYS = 7;

    private User getAdmin(String token) {
        User admin = adminService.findByToken(token);
        if (admin == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        return admin;
    }

    // ========== 管理员登录 ==========
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody AdminLoginRequest request) {
        User admin = adminService.login(request.getAccount(), request.getPassword());
        if (admin == null) {
            return Result.error(401, "账号或密码错误，或无管理员权限");
        }
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
            ADMIN_TOKEN_PREFIX + token,
            admin.getAccount(),
            TOKEN_EXPIRE_DAYS,
            TimeUnit.DAYS
        );

        // 记录登录日志
        operationLogService.log(admin.getId(), admin.getAccount(), "LOGIN", "SYSTEM", null, "管理员登录");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("account", admin.getAccount());
        data.put("nickname", admin.getNickname());
        return Result.success(data);
    }

    // ========== 统计数据 ==========
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(@RequestParam String token) {
        getAdmin(token);
        return Result.success(adminService.getStats());
    }

    // ========== 用户列表（分页） ==========
    @GetMapping("/users")
    public PageResult<User> users(@RequestParam String token,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {
        User admin = getAdmin(token);
        PageResult<User> result = adminService.getUserList(page, size, keyword);
        operationLogService.log(admin.getId(), admin.getAccount(), "QUERY", "USER", null, "查询用户列表，页码:" + page);
        return result;
    }

    // ========== 用户详情 ==========
    @GetMapping("/users/{id}")
    public Result<User> userDetail(@RequestParam String token, @PathVariable Long id) {
        User admin = getAdmin(token);
        operationLogService.log(admin.getId(), admin.getAccount(), "QUERY", "USER", id.toString(), "查看用户详情");
        return userRepository.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "用户不存在"));
    }

    // ========== 修改用户状态（禁用/启用） ==========
    @PutMapping("/users/{id}/status")
    public Result<Void> toggleStatus(@RequestParam String token, @PathVariable Long id) {
        User admin = getAdmin(token);
        Result<Void> result = adminService.toggleUserStatus(id);
        operationLogService.log(admin.getId(), admin.getAccount(), "UPDATE", "USER", id.toString(), "修改用户状态");
        return result;
    }

    // ========== 注销 ==========
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam String token) {
        User admin = adminService.findByToken(token);
        redisTemplate.delete(ADMIN_TOKEN_PREFIX + token);
        if (admin != null) {
            operationLogService.log(admin.getId(), admin.getAccount(), "LOGOUT", "SYSTEM", null, "管理员登出");
        }
        return Result.success();
    }
}
