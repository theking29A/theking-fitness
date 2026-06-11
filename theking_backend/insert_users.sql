-- 插入用户账号密码到 fitness_db 数据库
-- 执行方式：在 MySQL 客户端或管理工具（如 Navicat、DBeaver）中执行

USE fitness_db;

-- 插入演示账号（密码是明文存储，生产环境建议加密）
INSERT INTO users (account, password, nickname) VALUES
('admin', '123456', '管理员'),
('theking', 'your_password', 'TheKing');

-- 如果你想添加更多账号，按上面格式继续添加
-- INSERT INTO users (account, password, nickname) VALUES ('你的账号', '你的密码', '昵称');

-- 查看已插入的数据
SELECT * FROM users;
