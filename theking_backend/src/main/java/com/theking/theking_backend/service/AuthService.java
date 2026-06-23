package com.theking.theking_backend.service;

import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.repository.UserRepository;
import com.theking.theking_backend.util.CodeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Result<Void> register(String account, String password, String email) {
        // 1. 账号已存在
        if (userRepository.existsByAccount(account)) {
            return Result.error(409, "该账号已被注册");
        }

        // 2. 邮箱已存在
        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            return Result.error(409, "该邮箱已被使用");
        }

        // 3. 保存用户
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setEmail(email);
        user.setNickname(account);
        userRepository.save(user);

        return Result.success();
    }

    /**
     * 忘记密码：验证邮箱验证码后重置密码
     */
    public Result<Void> resetPassword(String account, String email, String code, String newPassword) {
        Optional<User> userOpt = userRepository.findByAccount(account);
        if (!userOpt.isPresent()) {
            return Result.error(404, "账号不存在");
        }

        User user = userOpt.get();
        if (email == null || !email.equals(user.getEmail())) {
            return Result.error(400, "邮箱与账号绑定的不一致");
        }

        if (!CodeStore.verify("forgot", account, code)) {
            return Result.error(400, "验证码错误或已过期");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        return Result.success();
    }
}
