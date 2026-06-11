# THEKING FITNESS 项目部署指南

> 部署目标：阿里云 ECS（120.24.236.105）
> 服务器配置：2核2G / Alibaba Cloud Linux 3
> 部署时间：2026-06-10

---

## 一、本地操作（打包前后端）

### 1.1 后端打包

在 `theking_backend` 目录下执行：

```bash
# Windows Git Bash
JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot"
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw clean package -DskipTests
```

打包成功后，jar 文件在：
```
theking_backend/target/theking-backend-0.0.1-SNAPSHOT.jar
```

### 1.2 前端打包

在 `theking-fitness-vue` 目录下执行：

```bash
npm run build
```

打包成功后，静态文件在：
```
theking-fitness-vue/dist/
```

> 如果 `npm` 命令找不到，先确认 Node.js 已安装，或尝试用 `npx vite build`

---

## 二、上传文件到阿里云

### 2.1 上传后端 jar 包

在本地 PowerShell / Git Bash：

```bash
scp theking_backend/target/theking-backend-0.0.1-SNAPSHOT.jar root@120.24.236.105:/opt/app/
```

### 2.2 上传前端静态文件

```bash
scp -r theking-fitness-vue/dist/* root@120.24.236.105:/usr/share/nginx/html/
```

### 2.3 上传数据库 SQL

```bash
scp theking_backend/fitness_db_export.sql root@120.24.236.105:/opt/app/
```

---

## 三、阿里云服务器操作

### 3.1 SSH 连接服务器

```bash
ssh root@120.24.236.105
```

### 3.2 导入数据库

```bash
mysql -u root -p'chenxu0517A!' fitness_db < /opt/app/fitness_db_export.sql
```

> 如果提示数据库不存在，先创建：
> ```sql
> CREATE DATABASE fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
> ```

### 3.3 配置后端 systemd 自启

创建服务文件：

```bash
cat > /etc/systemd/system/theking.service << 'EOF'
[Unit]
Description=TheKing Fitness Spring Boot App
After=syslog.target network.target

[Service]
User=root
ExecStart=/usr/bin/java -jar -Xms256m -Xmx512m /opt/app/theking-backend-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=10
Environment="JAVA_OPTS=-Dfile.encoding=UTF-8"

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl start theking
systemctl enable theking
```

检查状态：
```bash
systemctl status theking
```

### 3.4 配置 Nginx

编辑 Nginx 配置文件：

```bash
cat > /etc/nginx/conf.d/theking.conf << 'EOF'
server {
    listen 80;
    server_name 120.24.236.105;

    # 前端静态文件
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

nginx -t
systemctl restart nginx
```

---

## 四、验证部署

### 4.1 检查后端是否运行

```bash
curl http://localhost:8081/api/captcha
```

应该返回 JSON 包含 `code: 200` 和 `image`。

### 4.2 浏览器访问

打开浏览器访问：
```
http://120.24.236.105
```

应该看到前端页面，点击登录弹窗可以正常注册/登录。

### 4.3 检查日志

后端日志：
```bash
journalctl -u theking -f
```

Nginx 日志：
```bash
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

---

## 五、常见问题

### Q1: 后端启动失败，端口被占用
```bash
# 查看 8081 端口占用
netstat -tlnp | grep 8081
# 杀掉占用进程
kill -9 <PID>
# 重启服务
systemctl restart theking
```

### Q2: 前端页面空白或 404
确认 Nginx 的 `root` 指向正确，且 `dist` 文件已上传：
```bash
ls /usr/share/nginx/html/
# 应该看到 index.html 和 assets/ 目录
```

### Q3: 数据库连接失败
检查阿里云安全组是否放行 3306 端口（如果后端和 MySQL 在同一台服务器，不需要放行公网 3306）。

检查后端日志中的数据库连接错误，确认密码正确。

### Q4: 跨域问题
Nginx 反向代理已经解决了跨域，前端请求 `/api/xxx` 会被转发到后端。如果还有问题，检查前端 `API_BASE` 是否配置为服务器 IP。

---

## 六、文件清单

| 文件 | 路径 | 说明 |
|------|------|------|
| 后端 jar | `theking_backend/target/theking-backend-0.0.1-SNAPSHOT.jar` | Spring Boot 可执行 jar |
| 前端静态文件 | `theking-fitness-vue/dist/` | Nginx 托管的 HTML/CSS/JS |
| 数据库 SQL | `theking_backend/fitness_db_export.sql` | MySQL 数据导入文件 |
| 部署脚本 | `theking_backend/DEPLOY.md` | 本文件 |

---

## 七、后续优化建议

1. **HTTPS**：申请免费 SSL 证书，配置 Nginx 443 端口
2. **域名**：购买域名并解析到 120.24.236.105
3. **数据库密码**：生产环境建议用 BCrypt 加密存储用户密码
4. **日志轮转**：配置 logrotate 防止日志文件过大
5. **监控**：安装 pm2 或 supervisor 管理进程
