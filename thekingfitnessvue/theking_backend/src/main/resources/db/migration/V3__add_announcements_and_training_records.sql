-- ============================================
-- V3: Add announcements and user training records
-- ============================================

USE fitness_db;

-- 1. 系统公告表
CREATE TABLE IF NOT EXISTS `announcements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '公告标题',
  `content` text COMMENT '公告内容',
  `type` varchar(20) NOT NULL DEFAULT 'banner' COMMENT '类型：banner顶部通知, popup弹窗',
  `target` varchar(20) NOT NULL DEFAULT 'all' COMMENT '目标：all全部, user普通用户, admin管理员',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `start_time` datetime DEFAULT NULL COMMENT '生效时间',
  `end_time` datetime DEFAULT NULL COMMENT '失效时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告';

-- 2. 用户训练记录表
CREATE TABLE IF NOT EXISTS `user_training_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `plan_id` bigint DEFAULT NULL COMMENT '训练计划ID',
  `exercise_id` bigint NOT NULL COMMENT '动作ID',
  `day_number` int DEFAULT 1 COMMENT '第几天',
  `sets_completed` int DEFAULT 0 COMMENT '完成组数',
  `reps_completed` varchar(50) DEFAULT NULL COMMENT '完成次数',
  `duration_seconds` int DEFAULT 0 COMMENT '训练时长（秒）',
  `calories_burned` int DEFAULT 0 COMMENT '消耗卡路里',
  `notes` text COMMENT '备注',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_plan_id` (`plan_id`),
  KEY `idx_exercise_id` (`exercise_id`),
  KEY `idx_completed_at` (`completed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户训练记录';

-- 3. 初始化一条示例公告
INSERT INTO `announcements` (`title`, `content`, `type`, `target`, `status`, `sort_order`) VALUES
('欢迎访问 TheKing 健身', '感谢使用我们的健身平台，祝你训练愉快！', 'banner', 'all', 1, 1);
