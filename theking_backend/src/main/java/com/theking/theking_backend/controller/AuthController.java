package com.theking.theking_backend.controller;

import com.theking.theking_backend.service.AuthService;
import com.theking.theking_backend.util.CaptchaUtil;
import com.theking.theking_backend.util.CodeStore;
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
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String account = loginData.get("account");
        String password = loginData.get("password");

        System.out.println("收到登录请求: 账号=" + account);

        Map<String, Object> response = new HashMap<>();
        String token = authService.login(account, password);

        if (token != null) {
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("token", token);
        } else {
            response.put("code", 401);
            response.put("message", "账号或密码错误");
        }
        return response;
    }

    // ========== 图形验证码（防机器注册） ==========
    @GetMapping("/captcha")
    public Map<String, Object> getCaptcha() {
        Map<String, Object> response = new HashMap<>();
        try {
            String code = CaptchaUtil.generateCode(4);
            String captchaId = UUID.randomUUID().toString();
            CodeStore.put("captcha", captchaId, code);
            System.out.println("【图形验证码】captchaId=" + captchaId + ", 验证码=" + code);
            String imgBase64 = CaptchaUtil.generateBase64Image(code);

            response.put("code", 200);
            response.put("captchaId", captchaId);
            response.put("image", imgBase64);
        } catch (IOException e) {
            response.put("code", 500);
            response.put("message", "验证码生成失败");
        }
        return response;
    }

    // ========== 发送邮箱验证码（忘记密码用） ==========
    @PostMapping("/send-code")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> data) {
        String account = data.get("account");
        String email = data.get("email");
        String type = data.get("type"); // "register" 或 "forgot"

        Map<String, Object> response = new HashMap<>();

        if (account == null || account.isEmpty() || email == null || email.isEmpty()) {
            response.put("code", 400);
            response.put("message", "账号和邮箱不能为空");
            return response;
        }

        // 生成6位数字验证码
        String code = String.format("%06d", (int)(Math.random() * 1000000));
        CodeStore.put(type, account, code);

        // 【注意】这里只是模拟发送，实际应该调用邮件服务
        System.out.println("【模拟发送验证码】类型=" + type + ", 账号=" + account + ", 邮箱=" + email + ", 验证码=" + code);

        response.put("code", 200);
        response.put("message", "验证码已发送（请查看控制台日志）");
        // 开发模式下把验证码返回给前端，方便测试
        response.put("debugCode", code);
        return response;
    }

    // ========== 注册 ==========
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> data) {
        String account = data.get("account");
        String password = data.get("password");
        String email = data.get("email");
        String captchaId = data.get("captchaId");
        String captchaCode = data.get("captchaCode");

        Map<String, Object> response = new HashMap<>();

        // 1. 校验图形验证码
        if (captchaId == null || captchaCode == null ||
            !CodeStore.verify("captcha", captchaId, captchaCode)) {
            response.put("code", 400);
            response.put("message", "图形验证码错误或已过期");
            return response;
        }

        // 2. 调用注册逻辑
        return authService.register(account, password, email);
    }

    // ========== 忘记密码 / 重置密码 ==========
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> data) {
        String account = data.get("account");
        String email = data.get("email");
        String code = data.get("code");
        String newPassword = data.get("newPassword");

        return authService.resetPassword(account, email, code, newPassword);
    }
}
