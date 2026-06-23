package com.theking.theking_backend.service;

import com.theking.theking_backend.common.PageResult;
import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 管理员登录：验证账号密码，且必须是 ADMIN 角色
     */
    public User login(String account, String password) {
        Optional<User> userOpt = userRepository.findByAccount(account);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password) && user.getRole() == User.Role.ADMIN) {
                return user;
            }
        }
        return null;
    }

    /**
     * 获取统计数据
     */
    public Map<String, Object> getStats() {
        long totalUsers = userRepository.count();
        long adminCount = userRepository.countByRole(User.Role.ADMIN);

        // 今日注册（假设 createdAt 不存在，用 id 近似或全量返回）
        // 由于 User 没有 createdAt 字段，先简单统计
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("adminCount", adminCount);
        stats.put("userCount", totalUsers - adminCount);
        stats.put("todayRegister", 0); // 后续加 createdAt 字段后可完善
        return stats;
    }

    /**
     * 用户列表分页
     */
    public PageResult<User> getUserList(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<User> userPage;
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.findByAccountContainingOrNicknameContaining(keyword, keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return PageResult.success(userPage.getContent(), userPage.getTotalElements(), page, size);
    }

    /**
     * 切换用户状态（禁用/启用）
     * 由于没有 status 字段，这里用 role 来模拟：USER -> BANNED（如果有的话）
     * 目前先实现一个占位，后续加 status 字段
     */
    public Result<Void> toggleUserStatus(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return Result.error(404, "用户不存在");
        }
        // 当前没有 status 字段，后续可扩展
        return Result.success();
    }
}
