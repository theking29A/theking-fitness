package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.dto.*;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.entity.UserActivity;
import com.theking.theking_backend.mapper.UserActivityMapper;
import com.theking.theking_backend.mapper.UserMapper;
import com.theking.theking_backend.service.AuthService;
import com.theking.theking_backend.util.CaptchaUtil;
import com.theking.theking_backend.util.CodeStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserActivityMapper userActivityMapper;

    // ========== 登录 ==========
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String token = authService.login(request.getAccount(), request.getPassword());

        if (token != null) {
            // 查找用户并记录登录活动
            User user = userMapper.findByAccount(request.getAccount()).orElse(null);
            if (user != null) {
                UserActivity activity = new UserActivity();
                activity.setUserId(user.getId());
                activity.setActivityType("LOGIN");
                activity.setIpAddress(getClientIp(httpRequest));
                activity.setCreatedAt(LocalDateTime.now());
                userActivityMapper.insert(activity);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            return Result.success(data);
        } else {
            return Result.error(401, "账号或密码错误");
        }
    }

    // ========== 记录用户活动（前端埋点） ==========
    @PostMapping("/activity")
    public Result<Void> recordActivity(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String activityType = (String) request.get("activityType");
        Long targetId = request.get("targetId") != null ? Long.valueOf(request.get("targetId").toString()) : null;

        UserActivity activity = new UserActivity();
        activity.setUserId(userId);
        activity.setActivityType(activityType);
        activity.setTargetId(targetId);
        activity.setIpAddress(getClientIp(httpRequest));
        activity.setCreatedAt(LocalDateTime.now());
        userActivityMapper.insert(activity);

        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    // ========== 图形验证码（防机器注册） ==========
    @GetMapping("/captcha")
    public Result<Map<String, Object>> getCaptcha() {
        try {
            String code = CaptchaUtil.generateCode(4);
            String captchaId = UUID.randomUUID().toString();
            CodeStore.put("captcha", captchaId, code);
            System.out.println("【图形验证码】captchaId=" + captchaId + ", 验证码=" + code);
            String imgBase64 = CaptchaUtil.generateBase64Image(code);

            Map<String, Object> data = new HashMap<>();
            data.put("captchaId", captchaId);
            data.put("image", imgBase64);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error(500, "验证码生成失败");
        }
    }

    // ========== 发送邮箱验证码（忘记密码用） ==========
    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        // 生成6位数字验证码
        String code = String.format("%06d", (int)(Math.random() * 1000000));
        CodeStore.put(request.getType(), request.getAccount(), code);

        // 【注意】这里只是模拟发送，实际应该调用邮件服务
        System.out.println("【模拟发送验证码】类型=" + request.getType() + ", 账号=" + request.getAccount() + ", 邮箱=" + request.getEmail() + ", 验证码=" + code);

        // 开发模式下把验证码返回给前端，方便测试
        return Result.success();
    }

    // ========== 注册 ==========
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        // 1. 校验图形验证码
        if (!CodeStore.verify("captcha", request.getCaptchaId(), request.getCaptchaCode())) {
            return Result.error(400, "图形验证码错误或已过期");
        }

        // 2. 调用注册逻辑
        return authService.register(request.getAccount(), request.getPassword(), request.getEmail());
    }

    // ========== 忘记密码 / 重置密码 ==========
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request.getAccount(), request.getEmail(), request.getCode(), request.getNewPassword());
    }
}
