-- ============================================
-- fitness_db 数据库升级脚本
-- 新增：健身动作、训练计划、计划动作关联、操作日志、用户活动表
-- ============================================

USE fitness_db;

-- ============================================
-- 0. 给 users 表添加 created_at 列
-- ============================================
ALTER TABLE `users` ADD COLUMN IF NOT EXISTS `created_at` datetime DEFAULT CURRENT_TIMESTAMP;

-- ============================================
-- 1. 健身动作表 (exercises)
-- ============================================
CREATE TABLE IF NOT EXISTS `exercises` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '动作名称',
  `description` text COMMENT '动作描述',
  `category` varchar(50) NOT NULL COMMENT '部位分类：chest, back, legs, shoulders, arms, core, cardio',
  `target_muscles` varchar(255) COMMENT '目标肌群',
  `difficulty` varchar(20) NOT NULL DEFAULT 'beginner' COMMENT '难度：beginner, intermediate, advanced',
  `equipment` varchar(255) COMMENT '所需器械',
  `video_url` varchar(500) COMMENT '教学视频URL',
  `image_url` varchar(500) COMMENT '示范图片URL',
  `tips` text COMMENT '动作要点/注意事项',
  `calories_per_min` int DEFAULT 0 COMMENT '每分钟消耗卡路里（估算）',
  `duration_seconds` int DEFAULT 60 COMMENT '单次动作时长（秒）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `sort_order` int DEFAULT 0 COMMENT '排序权重',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_difficulty` (`difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身动作库';

-- ============================================
-- 2. 训练计划表 (training_plans)
-- ============================================
CREATE TABLE IF NOT EXISTS `training_plans` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '计划名称',
  `description` text COMMENT '计划描述',
  `goal` varchar(50) NOT NULL COMMENT '目标：muscle_building, fat_loss, strength, endurance, flexibility',
  `level` varchar(20) NOT NULL DEFAULT 'beginner' COMMENT '难度：beginner, intermediate, advanced',
  `duration_weeks` int NOT NULL DEFAULT 4 COMMENT '计划周期（周）',
  `days_per_week` int NOT NULL DEFAULT 3 COMMENT '每周训练天数',
  `cover_image` varchar(500) COMMENT '封面图片',
  `estimated_time_min` int DEFAULT 60 COMMENT '单次训练时长（分钟）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `sort_order` int DEFAULT 0 COMMENT '排序权重',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_goal` (`goal`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='训练计划';

-- ============================================
-- 3. 计划动作关联表 (plan_exercises)
-- ============================================
CREATE TABLE IF NOT EXISTS `plan_exercises` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint NOT NULL COMMENT '训练计划ID',
  `exercise_id` bigint NOT NULL COMMENT '动作ID',
  `day_number` int NOT NULL DEFAULT 1 COMMENT '第几天（1-7）',
  `sets` int NOT NULL DEFAULT 3 COMMENT '组数',
  `reps` varchar(50) DEFAULT '12' COMMENT '次数/时长（如：12, 30s, 力竭）',
  `rest_seconds` int DEFAULT 60 COMMENT '组间休息（秒）',
  `order_index` int NOT NULL DEFAULT 0 COMMENT '当天动作顺序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_exercise_day` (`plan_id`, `exercise_id`, `day_number`),
  KEY `idx_plan_id` (`plan_id`),
  KEY `idx_exercise_id` (`exercise_id`),
  KEY `idx_day_number` (`day_number`),
  CONSTRAINT `fk_plan_exercises_plan` FOREIGN KEY (`plan_id`) REFERENCES `training_plans` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_plan_exercises_exercise` FOREIGN KEY (`exercise_id`) REFERENCES `exercises` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='训练计划与动作关联';

-- ============================================
-- 4. 操作日志表 (operation_logs)
-- ============================================
CREATE TABLE IF NOT EXISTS `operation_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NOT NULL COMMENT '操作管理员ID',
  `admin_account` varchar(255) NOT NULL COMMENT '管理员账号',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型：CREATE, UPDATE, DELETE, LOGIN, LOGOUT, QUERY',
  `target_type` varchar(50) NOT NULL COMMENT '操作对象：USER, EXERCISE, PLAN, SYSTEM',
  `target_id` varchar(100) DEFAULT NULL COMMENT '被操作对象ID',
  `detail` text COMMENT '操作详情（JSON格式）',
  `ip_address` varchar(100) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT 'User-Agent',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_target_type` (`target_type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志';

-- ============================================
-- 5. 用户活动统计表 (user_activities) - 用于 DAU 统计
-- ============================================
CREATE TABLE IF NOT EXISTS `user_activities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `activity_type` varchar(50) NOT NULL COMMENT '活动类型：LOGIN, VIEW_EXERCISE, START_PLAN, COMPLETE_WORKOUT',
  `target_id` bigint DEFAULT NULL COMMENT '关联对象ID',
  `ip_address` varchar(100) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户活动记录';

-- ============================================
-- 6. 初始化示例数据：健身动作
-- ============================================
INSERT INTO `exercises` (`name`, `description`, `category`, `target_muscles`, `difficulty`, `equipment`, `tips`, `calories_per_min`, `duration_seconds`, `sort_order`) VALUES
('杠铃卧推', '经典的胸部训练动作，能有效刺激胸大肌、三角肌前束和肱三头肌。', 'chest', '胸大肌,三角肌前束,肱三头肌', 'intermediate', '杠铃,卧推凳', '肩胛骨后缩下沉，腰部保持自然拱起，杠铃下放至乳头位置', 8, 45, 1),
('哑铃飞鸟', '更好的胸部拉伸动作，增加胸肌的厚度和线条感。', 'chest', '胸大肌', 'intermediate', '哑铃,卧推凳', '肘部微屈，像拥抱大树一样张开双臂，感受胸肌拉伸', 6, 40, 2),
('引体向上', '背部训练的王牌动作，全面刺激背阔肌。', 'back', '背阔肌,肱二头肌', 'advanced', '单杠', '下沉肩胛骨，想象用肘部拉向身体，不要耸肩', 10, 60, 3),
('杠铃深蹲', '腿部训练之王，刺激股四头肌、臀大肌和腘绳肌。', 'legs', '股四头肌,臀大肌,腘绳肌', 'advanced', '杠铃,深蹲架', '双脚与肩同宽，膝盖方向与脚尖一致，下蹲至大腿与地面平行', 12, 60, 4),
('哑铃推举', '肩部训练基础动作，打造饱满三角肌。', 'shoulders', '三角肌前束,三角肌中束', 'intermediate', '哑铃', '核心收紧，避免腰部过度反弓，推至手臂伸直但不锁死', 7, 40, 5),
('平板支撑', '核心训练经典动作，增强腹部和背部稳定性。', 'core', '腹直肌,腹横肌,竖脊肌', 'beginner', '无', '身体成一条直线，收紧核心，不要塌腰或撅臀', 5, 60, 6),
('俯卧撑', '最经典的自重训练，无需器械即可训练胸部和三头肌。', 'chest', '胸大肌,肱三头肌,三角肌前束', 'beginner', '无', '身体成直线，胸部贴地，肘部不要过度外展', 8, 30, 7),
('哑铃弯举', '二头肌训练经典动作，打造饱满的手臂前侧。', 'arms', '肱二头肌', 'beginner', '哑铃', '肘部固定在身体两侧，不要借助身体晃动，顶峰收缩', 5, 30, 8),
('绳索面拉', '肩部后束和背部上部的优秀动作，改善圆肩体态。', 'back', '三角肌后束,斜方肌中部', 'intermediate', '绳索机', '肘部高于肩部，向后拉至脸部两侧，挤压肩胛骨', 6, 40, 9),
('卷腹', '腹部训练基础，刺激腹直肌上部。', 'core', '腹直肌', 'beginner', '无', '下背部贴地，用腹部力量卷起上半身，不要拉扯颈部', 6, 30, 10);

-- ============================================
-- 7. 初始化示例数据：训练计划
-- ============================================
INSERT INTO `training_plans` (`name`, `description`, `goal`, `level`, `duration_weeks`, `days_per_week`, `estimated_time_min`, `sort_order`) VALUES
('新手全身适应计划', '适合健身新手的全身训练计划，每周3次，帮助身体适应力量训练。', 'muscle_building', 'beginner', 4, 3, 45, 1),
('增肌5x5计划', '经典的5x5力量训练计划，以复合动作为主，快速提升力量。', 'strength', 'intermediate', 8, 3, 60, 2),
('减脂燃脂计划', '高强度间歇训练结合力量训练，最大化热量消耗，快速减脂。', 'fat_loss', 'intermediate', 6, 4, 50, 3);
