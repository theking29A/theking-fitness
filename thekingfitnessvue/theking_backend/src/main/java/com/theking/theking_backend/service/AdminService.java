package com.theking.theking_backend.service;

import com.theking.theking_backend.common.PageResult;
import com.theking.theking_backend.common.Result;
import com.theking.theking_backend.entity.User;
import com.theking.theking_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    private static final String ADMIN_TOKEN_PREFIX = "admin:token:";

    /**
     * 根据 token 查找管理员
     */
    public User findByToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String account = redisTemplate.opsForValue().get(ADMIN_TOKEN_PREFIX + token);
        if (account == null) {
            return null;
        }
        return userMapper.findByAccount(account).orElse(null);
    }

    /**
     * 管理员登录：验证账号密码，且必须是 ADMIN 角色
     */
    public User login(String account, String password) {
        Optional<User> userOpt = userMapper.findByAccount(account);
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
        long totalUsers = userMapper.count();
        long adminCount = userMapper.countByRole(User.Role.ADMIN);
        long todayRegister = userMapper.countByCreatedAtGreaterThanEqual(
                java.time.LocalDateTime.now().toLocalDate().atStartOfDay());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("adminCount", adminCount);
        stats.put("userCount", totalUsers - adminCount);
        stats.put("todayRegister", todayRegister);
        return stats;
    }

    /**
     * 用户列表分页
     */
    public PageResult<User> getUserList(int page, int size, String keyword) {
        int offset = (page - 1) * size;
        List<User> users;
        long total;
        if (keyword != null && !keyword.isEmpty()) {
            users = userMapper.searchByKeyword(keyword, offset, size);
            total = userMapper.countByKeyword(keyword);
        } else {
            users = userMapper.findAllWithPagination(offset, size);
            total = userMapper.count();
        }
        return PageResult.success(users, total, page, size);
    }

    /**
     * 切换用户状态（禁用/启用）
     * 由于没有 status 字段，这里用 role 来模拟：USER -> BANNED（如果有的话）
     * 目前先实现一个占位，后续加 status 字段
     */
    public Result<Void> toggleUserStatus(Long id) {
        Optional<User> userOpt = userMapper.findById(id);
        if (!userOpt.isPresent()) {
            return Result.error(404, "用户不存在");
        }
        // 当前没有 status 字段，后续可扩展
        return Result.success();
    }
}
