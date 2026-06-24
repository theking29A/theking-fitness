# Admin 后台部署错误记录与解决方案

> 记录部署过程中遇到的所有错误、排查过程和最终解决方案，供后续参考。

---

## 错误 1：MySQL 8 `Public Key Retrieval is not allowed`

### 现象
手机登录/注册失败，后端日志报错：
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Public Key Retrieval is not allowed
```

### 原因
MySQL 8 使用 `caching_sha2_password` 认证插件，JDBC 连接需要允许公钥检索。

### 解决
在 `application.properties` 的 JDBC URL 中添加 `allowPublicKeyRetrieval=true`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fitness_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8
```

### 耗时
约 30 分钟（排查 + 修改 + 重新部署验证）

---

## 错误 2：GitHub Actions 第三方 Action 限制

### 现象
运行失败，报错：
```
Error: .github/workflows/deploy.yml: Action 'appleboy/scp-action' not allowed
```

### 原因
GitHub 仓库的安全策略限制了未经认证的第三方 Action。

### 解决
将 `appleboy/scp-action` 和 `appleboy/ssh-action` 替换为纯 `run` 命令 + `sshpass`：
```yaml
# 失败的写法
- uses: appleboy/scp-action@v0.1.7

# 替换成
- run: sshpass -p "$PASSWORD" scp -o StrictHostKeyChecking=no ...
```

但后续发现 `sshpass` 在 GitHub Actions 网络到国内服务器的直连不稳定，最终又改回 `appleboy` Action。**结论是：如果仓库允许，优先用 `appleboy` Action，它有内置重试机制。**

### 耗时
约 1 小时（反复修改 deploy.yml 测试）

---

## 错误 3：`npm ci` 失败（没有 `package-lock.json`）

### 现象
GitHub Actions 中 `Build Admin Frontend` 步骤失败：
```
npm ci can only install packages when your package.json and package-lock.json are in sync
```

### 原因
`npm ci` 需要 `package-lock.json`，但 `theking_admin` 是新项目，没有这个文件。

### 解决
将 `npm ci` 改为 `npm install`：
```yaml
# 错误
- run: npm ci

# 正确
- run: npm install
```

### 耗时
约 20 分钟（多次失败后才想到）

---

## 错误 4：Node.js 20 deprecated 警告

### 现象
GitHub Actions 日志：
```
Node.js 20 is deprecated. The following actions target Node.js 20 but are being forced to run on Node.js 24
```

### 原因
GitHub Actions 运行器升级，Node.js 20 被标记为 deprecated。

### 解决
将 `actions/setup-node` 的 `node-version` 从 `'20'` 改为 `'22'`：
```yaml
- uses: actions/setup-node@v4
  with:
    node-version: '22'
```

### 耗时
约 10 分钟

---

## 错误 5：SSH 连接失败（exit code 255）

### 现象
使用 `sshpass` + 纯 `scp`/`ssh` 命令时，部署步骤失败：
```
Process completed with exit code 255
```

### 原因
- `secrets.PORT` 为空，导致 `scp -P ""` 无效
- GitHub Actions 国外运行器到国内阿里云服务器的直连不稳定

### 解决
1. 给 PORT 加默认值：`PORT=${PORT:-22}`
2. 最终改回 `appleboy/scp-action`（有内置重试和超时机制）

```yaml
# 最终方案：用 appleboy Action（最稳定）
- uses: appleboy/scp-action@v0.1.7
  with:
    host: ${{ secrets.HOST }}
    port: ${{ secrets.PORT }}
    ...
```

### 耗时
约 40 分钟（反复切换方案）

---

## 错误 6：Ant Design Vue 组件不渲染（空白页面）

### 现象
Admin 登录页面只显示标题和"登录"文字，没有输入框和按钮。

### 原因
**Vue 3 `<script setup>` 中组件名与原生 HTML 标签冲突**：
```vue
<!-- 错误：Form 被当作原生 <form> 元素 -->
<Form layout="vertical">
```

### 解决
**方案 A：显式导入并重命名组件**（推荐）
```vue
<script setup>
import { Form, Input, Button } from 'ant-design-vue'
const AForm = Form
const AFormItem = Form.Item
const AInput = Input
const AInputPassword = Input.Password
const AButton = Button
</script>

<template>
  <AForm layout="vertical">
    <AFormItem label="账号">
      <AInput />
    </AFormItem>
    <AButton>登录</AButton>
  </AForm>
</template>
```

**方案 B：使用 `unplugin-vue-components` 自动导入**（但构建时引入新依赖可能导致失败）

最终选择方案 A，稳定可靠。

### 耗时
约 2 小时（反复尝试全局注册、局部导入、unplugin 等多种方案）

---

## 错误 7：代码分割导致 JS 语法错误

### 现象
控制台报错：
```
Uncaught SyntaxError: Unexpected token 'export'
```

### 原因
Vite 默认将第三方库（如 Ant Design Vue）拆分到 `vendor.js` 中，这个 chunk 在某些浏览器环境下加载时报语法错误。

### 解决
禁用代码分割，打包为单文件：
```js
// vite.config.ts
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: undefined  // 禁用代码分割
      }
    }
  }
})
```

### 耗时
约 30 分钟

---

## 错误 8：JAR 部署路径不一致导致后端还是旧版本

### 现象
前端能打开，但登录时后端返回：
```
{"code":500,"message":"服务器内部错误: No static resource api/admin/login."}
```

### 排查过程
1. `curl` 测试后端 API → 返回 `No static resource`
2. 检查 `systemctl status theking` → 服务运行正常
3. 检查 `ss -tlnp | grep 8081` → 端口在监听
4. 检查 JAR 文件位置 → 发现部署到了 `/opt/theking/`，但服务文件指向 `/opt/app/`
5. 服务实际运行的是旧版本 JAR（没有 AdminController）

### 根本原因
deploy.yml 中 JAR 上传路径和服务文件路径不一致：
```yaml
# deploy.yml 上传到这里
source: "..." → target: "/opt/theking/"

# 但 systemd 服务文件从这里加载
ExecStart=/usr/bin/java -jar ... /opt/app/theking-backend-0.0.1-SNAPSHOT.jar
```

### 解决
**统一路径**：deploy.yml 直接上传到 `/opt/app/`，与 systemd 服务文件一致：
```yaml
# 修复后
target: "/opt/app/"
```

### 耗时
约 1.5 小时（SSH 登录服务器逐一排查文件路径、服务配置、JAR 版本）

---

## 错误 9：前端缓存导致旧版本不生效

### 现象
修改代码并部署后，浏览器打开页面还是旧版本。

### 解决
- 按 `Ctrl + F5` 强制刷新
- 或者按 `Ctrl + Shift + Delete` 清除缓存
- 或者换浏览器/无痕模式打开

### 耗时
每次部署后都需要做，累计约 20 分钟

---

## 错误 10：菜单点击没有路由跳转

### 现象
登录成功后，点击左侧"用户管理"、"系统设置"菜单没有反应，页面不切换。

### 原因
`<a-menu>` 没有绑定点击事件，菜单项只是 UI 展示，没有触发路由跳转。

### 解决
给 `<a-menu>` 添加 `@click` 事件监听：
```vue
<template>
  <a-menu @click="handleMenuClick">
    <a-menu-item v-for="item in menuItems" :key="item.key">
      ...
    </a-menu-item>
  </a-menu>
</template>

<script setup>
const router = useRouter()

const handleMenuClick = ({ key }) => {
  router.push(key)
}
</script>
```

### 耗时
约 15 分钟

---

## 总结：最大时间消耗点

| 排名 | 问题 | 耗时 | 根因 |
|------|------|------|------|
| 1 | Ant Design Vue 组件不渲染 | ~2 小时 | Vue 3 组件名与原生 HTML 标签冲突 |
| 2 | JAR 部署路径不一致 | ~1.5 小时 | deploy.yml 路径和 systemd 服务文件不匹配 |
| 3 | GitHub Actions 部署方式反复切换 | ~1 小时 | 第三方 Action 限制 + 网络不稳定 |
| 4 | MySQL 连接问题 | ~30 分钟 | MySQL 8 认证插件配置 |
| 5 | 代码分割导致 JS 错误 | ~30 分钟 | Vite 默认 chunk 拆分策略 |
| 6 | 其他（npm ci、缓存、菜单等） | ~1 小时 | 各种小问题 |

**总计**：约 6.5 小时的反复调试

---

## 关键经验教训

1. **Vue 3 `<script setup>` 中避免使用原生 HTML 标签名作为组件名** — 一定要用 `<AForm>`、`<AButton>` 等重命名
2. **部署路径必须统一** — deploy.yml 的上传路径 = systemd 服务文件中的 JAR 路径
3. **优先用经过验证的 Action** — `appleboy` 的 Action 比手写 `sshpass` 更稳定
4. **新项目的 package.json 没有 `package-lock.json` 时，用 `npm install` 而非 `npm ci`**
5. **部署后务必 `Ctrl + F5` 强制刷新浏览器**
6. **Vite 的代码分割在 CDN/子目录部署时可能出问题，考虑禁用**
