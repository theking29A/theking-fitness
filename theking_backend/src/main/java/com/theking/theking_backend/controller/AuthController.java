package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.dto.*;
import com.theking.theking_backend.service.AuthService;
import com.theking.theking_backend.util.CaptchaUtil;
import com.theking.theking_backend.util.CodeStore;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ========== 登录 ==========
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getAccount(), request.getPassword());

        if (token != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            return Result.success(data);
        } else {
            return Result.error(401, "账号或密码错误");
        }
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
