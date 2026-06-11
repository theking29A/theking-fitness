package com.theking.theking_backend.service;

import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.repository.UserRepository;
import com.theking.theking_backend.util.CodeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 登录：验证账号密码，成功返回 token，失败返回 null
     */
    public String login(String account, String password) {
        Optional<User> userOpt = userRepository.findByAccount(account);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return UUID.randomUUID().toString();
            }
        }
        return null;
    }

    /**
     * 注册
     */
    public Map<String, Object> register(String account, String password, String email) {
        Map<String, Object> result = new HashMap<>();

        // 1. 账号已存在
        if (userRepository.existsByAccount(account)) {
            result.put("code", 409);
            result.put("message", "该账号已被注册");
            return result;
        }

        // 2. 邮箱已存在
        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            result.put("code", 409);
            result.put("message", "该邮箱已被使用");
            return result;
        }

        // 3. 密码长度校验
        if (password == null || password.length() < 6) {
            result.put("code", 400);
            result.put("message", "密码长度至少6位");
            return result;
        }

        // 4. 保存用户
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setEmail(email);
        user.setNickname(account);
        userRepository.save(user);

        result.put("code", 200);
        result.put("message", "注册成功");
        return result;
    }

    /**
     * 忘记密码：验证邮箱验证码后重置密码
     */
    public Map<String, Object> resetPassword(String account, String email, String code, String newPassword) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> userOpt = userRepository.findByAccount(account);
        if (!userOpt.isPresent()) {
            result.put("code", 404);
            result.put("message", "账号不存在");
            return result;
        }

        User user = userOpt.get();
        if (email == null || !email.equals(user.getEmail())) {
            result.put("code", 400);
            result.put("message", "邮箱与账号绑定的不一致");
            return result;
        }

        if (!CodeStore.verify("forgot", account, code)) {
            result.put("code", 400);
            result.put("message", "验证码错误或已过期");
            return result;
        }

        if (newPassword == null || newPassword.length() < 6) {
            result.put("code", 400);
            result.put("message", "新密码长度至少6位");
            return result;
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        result.put("code", 200);
        result.put("message", "密码重置成功");
        return result;
    }
}
