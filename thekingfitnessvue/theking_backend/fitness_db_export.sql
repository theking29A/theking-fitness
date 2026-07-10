-- fitness_db 数据库导出
-- 导出时间: 2026-06-10
-- 用途: 导入到阿里云 MySQL

CREATE DATABASE IF NOT EXISTS fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitness_db;

-- users 表结构 + 数据
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 已注册的测试账号（密码明文存储，生产环境建议加密）
INSERT INTO `users` (`account`, `password`, `nickname`, `email`) VALUES
('testuser', '123456', 'testuser', 'test@test.com');

-- 如果你本地还有其他账号数据，请自行补充 INSERT 语句
-- 或者使用 MySQL 客户端导出完整数据：
-- mysqldump -u root -p fitness_db > fitness_db_full.sql
