# TheKing Admin 后台部署全过程总结

## 项目概述

为健身网站 `http://120.24.236.105` 添加了一个独立的管理后台，技术栈：

- **后端**：Spring Boot 3 + JPA + MySQL 8
- **前端**：Vue 3 + TypeScript + Vite + Ant Design Vue
- **部署**：GitHub Actions → 阿里云 ECS + Nginx

---

## 一、后端开发

### 1.1 基础架构
- Spring Boot 3.5.14 + JPA (Hibernate)
- MySQL 8 数据库连接
- 端口：8081
- 用户表 `users`（id, account, password, nickname, email, role）

### 1.2 已有功能
- 用户登录/注册（参数校验 `@Valid`）
- 图形验证码
- 邮箱验证码（模拟）
- 忘记密码/重置密码
- 统一响应格式 `Result<T>`
- 全局异常处理 `GlobalExceptionHandler`

### 1.3 新增 Admin 接口
- `POST /api/admin/login` — 管理员登录（需 `role = ADMIN`）
- `GET /api/admin/stats` — 数据统计（总用户、管理员数等）
- `GET /api/admin/users` — 用户列表分页
- `GET /api/admin/users/{id}` — 用户详情
- `PUT /api/admin/users/{id}/status` — 切换用户状态

### 1.4 数据库变更
```sql
ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER';
UPDATE users SET role = 'ADMIN' WHERE account = 'theking';
```

---

## 二、前端开发（Admin 后台）

### 2.1 项目结构
```
theking_admin/
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
└── src/
    ├── main.ts
    ├── App.vue
    ├── vite-env.d.ts
    ├── api/
    │   ├── request.ts
    │   └── admin.ts
    ├── stores/
    │   └── user.ts
    ├── router/
    │   └── index.ts
    ├── layouts/
    │   └── BasicLayout.vue
    └── views/
        ├── login/
        │   └── index.vue
        ├── dashboard/
        │   └── index.vue
        ├── users/
        │   └── index.vue
        └── settings/
            └── index.vue
```

### 2.2 技术栈
- Vue 3 + `<script setup>`
- Vue Router 4（hash 模式）
- Pinia 状态管理
- Ant Design Vue 4（全局注册）
- Axios HTTP 请求

### 2.3 页面功能
- **登录页**：渐变背景 + 表单验证
- **数据看板**：统计卡片（总用户、普通用户、管理员、今日注册）
- **用户管理**：分页表格 + 搜索 + 角色标签
- **系统设置**：基础配置页面

---

## 三、Nginx 配置

```nginx
server {
    listen 80 default_server;
    server_name 120.24.236.105;

    # 主站前端
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# Admin 后台静态文件
location /admin {
    alias /usr/share/nginx/html/admin;
    index index.html;
    try_files $uri $uri/ =404;
}
```

---

## 四、服务器配置

### 4.1 systemd 服务（`/etc/systemd/system/theking.service`）
```ini
[Unit]
Description=TheKing Fitness Spring Boot App
After=network.target

[Service]
Type=simple
User=root
ExecStart=/usr/bin/java -jar -Xms256m -Xmx512m /opt/app/theking-backend-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 4.2 常用命令
```bash
# 查看后端状态
systemctl status theking

# 重启后端
systemctl restart theking

# 查看日志
journalctl -u theking -f

# 检查端口监听
ss -tlnp | grep 8081
```

---

## 五、GitHub Actions CI/CD 部署流程

### 5.1 工作流步骤
1. **Checkout** 拉取代码
2. **Build Backend** — Maven 打包 Spring Boot JAR
3. **Build Frontend** — npm 构建主站 Vue
4. **Build Admin Frontend** — npm 构建 Admin Vue
5. **Tar Frontend** — 打包前端为 tar.gz
6. **Deploy Backend JAR** — SCP 上传到 `/opt/app/`
7. **Deploy Frontend Tar** — SCP 上传主站 tar.gz
8. **Deploy Admin Tar** — SCP 上传 admin tar.gz
9. **Restart & Deploy Server** — SSH 执行：
   - 重启后端服务
   - 解压主站前端到 `/usr/share/nginx/html/`
   - 解压 admin 前端到 `/usr/share/nginx/html/admin/`
   - 更新 Nginx 配置并重载

### 5.2 部署关键
- 使用 `appleboy/scp-action` 和 `appleboy/ssh-action` 上传/执行
- 前端用 `tar.gz` 打包后上传，避免 SCP 通配符问题
- JAR 直接上传到 `/opt/app/`（和服务文件路径一致）

---

## 六、最终访问地址

| 项目 | 地址 | 说明 |
|------|------|------|
| 主站（用户端） | `http://120.24.236.105` | 用户登录/注册 |
| Admin 后台 | `http://120.24.236.105/admin` | 管理员登录 |
| 后端 API | `http://120.24.236.105/api/xxx` | 通过 Nginx 代理 |

---

## 七、Admin 后台功能清单

| 功能 | 状态 | 说明 |
|------|------|------|
| 管理员登录 | ✅ | 需 `role = ADMIN` |
| 数据看板 | ✅ | 统计卡片展示 |
| 用户管理 | ✅ | 分页列表、搜索 |
| 系统设置 | ✅ | 基础配置页面 |
| 左侧菜单 | ✅ | 可折叠 |
| 退出登录 | ✅ | 清除 Token 跳转 |

---

## 八、文件清单（新增/修改）

### 后端新增
- `theking_backend/common/PageResult.java`
- `theking_backend/controller/AdminController.java`
- `theking_backend/dto/admin/AdminLoginRequest.java`
- `theking_backend/service/AdminService.java`
- `theking_backend/entity/User.java`（添加 `role` 字段）
- `theking_backend/repository/UserRepository.java`（添加统计方法）

### 前端新增
- `theking_admin/` 整个目录

### 部署配置修改
- `.github/workflows/deploy.yml`（添加 admin 构建和部署）
