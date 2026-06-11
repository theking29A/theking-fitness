# CI/CD 配置指南

> 实现效果：代码 push 到 GitHub → 自动打包前后端 → 自动上传到阿里云 → 自动重启服务

---

## 第一步：推送代码到 GitHub

### 1.1 在 GitHub 创建仓库

1. 打开 [github.com](https://github.com)
2. 点击右上角 **+** → **New repository**
3. 仓库名填：`theking-fitness`
4. 选择 **Public**（免费）或 **Private**（需要 GitHub Pro 才能用 Actions，学生可以用 GitHub Student Pack 免费）
5. 点击 **Create repository**

### 1.2 本地推送代码

在 PowerShell 里执行：

```powershell
cd E:\练习\个人网站练习

# 添加远程仓库（把下面的 URL 换成你实际的 GitHub 仓库地址）
git remote add origin https://github.com/你的用户名/theking-fitness.git

# 推送到 GitHub
git branch -M main
git push -u origin main
```

> 第一次 push 会提示输入 GitHub 用户名和密码（或 Token）。

---

## 第二步：配置 GitHub Secrets

Secrets 是 GitHub 的加密存储，用来放服务器密码等敏感信息，**不会泄露到代码里**。

### 2.1 进入 Secrets 设置

1. 打开你的 GitHub 仓库页面
2. 点击 **Settings** → 左侧 **Secrets and variables** → **Actions**
3. 点击 **New repository secret**

### 2.2 添加 4 个 Secret

| Secret 名称 | 值 | 说明 |
|-------------|-----|------|
| `HOST` | `120.24.236.105` | 阿里云服务器公网 IP |
| `USERNAME` | `root` | SSH 登录用户名 |
| `PASSWORD` | `你的阿里云root密码` | SSH 登录密码 |
| `PORT` | `22` | SSH 端口 |

逐个添加，添加完应该是这样：

```
HOST        = 120.24.236.105
USERNAME    = root
PASSWORD    = chenxu0517A!   (你的实际密码)
PORT        = 22
```

---

## 第三步：测试 CI/CD

### 3.1 触发自动部署

在本地改一行代码（比如改个注释），然后 push：

```powershell
cd E:\练习\个人网站练习

# 随便改点什么，比如给 Home.vue 加个注释
git add .
git commit -m "test: CI/CD 自动部署测试"
git push origin main
```

### 3.2 查看部署进度

1. 打开 GitHub 仓库页面
2. 点击 **Actions** 标签
3. 你会看到正在运行的 workflow（黄色 = 进行中，绿色 = 成功，红色 = 失败）
4. 点进去可以看每一步的日志

### 3.3 验证结果

等 Actions 全部变绿色后，浏览器访问：
```
http://120.24.236.105
```

网站应该已经更新了。

---

## 常见问题

### Q1: GitHub Actions 显示 "Permission denied"（密码错误）
- 检查 Secrets 里的 `PASSWORD` 是否和阿里云 root 密码一致
- 注意大小写和特殊字符

### Q2: 前端打包失败（npm ci 报错）
- 确保 `theking-fitness-vue/package-lock.json` 已提交到 Git
- 如果没有，本地执行 `npm install` 生成后再 push

### Q3: 后端打包失败（mvnw 权限不足）
- 工作流里已经加了 `chmod +x mvnw`，一般没问题
- 如果还报错，检查 `pom.xml` 是否有语法错误

### Q4: 部署成功但网站没更新
- 可能是浏览器缓存，按 `Ctrl+F5` 强制刷新
- 或者 Nginx 缓存了静态文件，在服务器执行 `systemctl restart nginx`

---

## 文件说明

| 文件 | 作用 |
|------|------|
| `.github/workflows/deploy.yml` | GitHub Actions 工作流定义 |
| `theking_backend/target/*.jar` | 后端打包产物（自动上传） |
| `theking-fitness-vue/dist/` | 前端打包产物（自动上传） |

---

## 后续优化方向

1. **分支部署**：只有 `main` 分支 push 才触发部署，`dev` 分支只打包不上传
2. **通知**：部署成功/失败时发邮件或微信通知
3. **回滚**：保留旧版本 jar 包，部署失败自动回滚
4. **数据库迁移**：用 Flyway 或 Liquibase 管理数据库版本变更

---

> 配完 CI/CD 后，你的开发流程变成：**写代码 → git push → 喝口水 → 网站已更新**。这才是现代开发该有的效率。
