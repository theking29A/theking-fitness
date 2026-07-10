-- ============================================
-- V4: System Settings table
-- ============================================

USE fitness_db;

CREATE TABLE IF NOT EXISTS `system_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(255) NOT NULL COMMENT '配置键',
  `setting_value` text COMMENT '配置值',
  `category` varchar(50) NOT NULL DEFAULT 'general' COMMENT '分类：security, maintenance, display, upload',
  `description` varchar(500) COMMENT '描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`setting_key`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置';

-- 安全设置
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `category`, `description`) VALUES
('security.token_expire_days', '7', 'security', 'Token过期时间（天）'),
('security.login_fail_lock_enabled', 'true', 'security', '登录失败锁定：true=开启'),
('security.login_fail_max_attempts', '5', 'security', '最大失败次数'),
('security.login_fail_lock_minutes', '30', 'security', '锁定时间（分钟）'),
('security.password_min_length', '6', 'security', '密码最小长度');

-- 维护模式
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `category`, `description`) VALUES
('maintenance.enabled', 'false', 'maintenance', '维护模式开关：true=开启'),
('maintenance.message', '系统正在维护中，请稍后再试。', 'maintenance', '维护提示语');

-- 显示设置
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `category`, `description`) VALUES
('display.page_size_default', '10', 'display', '默认分页条数'),
('display.time_format', 'YYYY-MM-DD HH:mm:ss', 'display', '时间格式');

-- 文件上传设置
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `category`, `description`) VALUES
('upload.max_file_size_mb', '50', 'upload', '最大文件大小（MB）'),
('upload.allowed_extensions', 'jpg,png,gif,mp4', 'upload', '允许的文件扩展名');
