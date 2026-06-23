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
    private StringRedisTemplate redisTemplate;

    private static final String ADMIN_TOKEN_PREFIX = "admin:token:";
    private static final long TOKEN_EXPIRE_DAYS = 7;

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

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("account", admin.getAccount());
        data.put("nickname", admin.getNickname());
        return Result.success(data);
    }

    // ========== 验证 token ==========
    private boolean isValidToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(ADMIN_TOKEN_PREFIX + token));
    }

    // ========== 统计数据 ==========
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(@RequestParam String token) {
        if (!isValidToken(token)) {
            return Result.error(401, "未登录或登录已过期");
        }
        return Result.success(adminService.getStats());
    }

    // ========== 用户列表（分页） ==========
    @GetMapping("/users")
    public PageResult<User> users(@RequestParam String token,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {
        if (!isValidToken(token)) {
            PageResult<User> r = new PageResult<>();
            r.setCode(401);
            r.setMessage("未登录或登录已过期");
            return r;
        }
        return adminService.getUserList(page, size, keyword);
    }

    // ========== 用户详情 ==========
    @GetMapping("/users/{id}")
    public Result<User> userDetail(@RequestParam String token, @PathVariable Long id) {
        if (!isValidToken(token)) {
            return Result.error(401, "未登录或登录已过期");
        }
        return userRepository.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "用户不存在"));
    }

    // ========== 修改用户状态（禁用/启用） ==========
    @PutMapping("/users/{id}/status")
    public Result<Void> toggleStatus(@RequestParam String token, @PathVariable Long id) {
        if (!isValidToken(token)) {
            return Result.error(401, "未登录或登录已过期");
        }
        return adminService.toggleUserStatus(id);
    }

    // ========== 注销 ==========
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam String token) {
        redisTemplate.delete(ADMIN_TOKEN_PREFIX + token);
        return Result.success();
    }
}
