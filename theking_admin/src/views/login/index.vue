<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { adminLogin } from '@/api/admin'
import { Form, Input, Button, message } from 'ant-design-vue'

const AForm = Form
const AFormItem = Form.Item
const AInput = Input
const AInputPassword = Input.Password
const AButton = Button

const router = useRouter()
const userStore = useUserStore()
const form = ref({ account: '', password: '' })
const loading = ref(false)

const rules = {
  account: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const formRef = ref<any>(null)

const handleLogin = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    try {
      const res: any = await adminLogin(form.value)
      if (res.code === 200) {
        userStore.setUser(res.data)
        message.success('登录成功')
        router.push('/dashboard')
      } else {
        message.error(res.message || '登录失败')
      }
    } catch (e) {
      message.error('网络错误')
    } finally {
      loading.value = false
    }
  } catch (e) {
    // 验证失败
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg" alt="logo" />
        <h2>TheKing Admin</h2>
        <p>健身管理后台</p>
      </div>
      <AForm ref="formRef" :model="form" :rules="rules" layout="vertical">
        <AFormItem label="管理员账号" name="account">
          <AInput v-model:value="form.account" size="large" placeholder="请输入管理员账号" @pressEnter="handleLogin" />
        </AFormItem>
        <AFormItem label="密码" name="password">
          <AInputPassword v-model:value="form.password" size="large" placeholder="请输入密码" @pressEnter="handleLogin" />
        </AFormItem>
        <AButton type="primary" size="large" block :loading="loading" @click="handleLogin">
          登录
        </AButton>
      </AForm>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 背景图片：完整显示，不裁切，居中 */
  background-image: url('/login-bg.png');
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #1a1a2e; /* 图片边缘外的深色兜底 */
}

.login-box {
  width: 400px;
  padding: 40px;
  border-radius: 16px;
  /* 毛玻璃效果 */
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header img {
  width: 56px;
  height: 56px;
  margin-bottom: 12px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
}

.login-header h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}

.login-header p {
  color: rgba(255, 255, 255, 0.75);
  margin: 0;
  font-size: 14px;
}

/* 覆盖 Ant Design 表单样式，适配深色毛玻璃背景 */
.login-box :deep(.ant-form-item-label > label) {
  color: rgba(255, 255, 255, 0.85);
}

.login-box :deep(.ant-input) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.login-box :deep(.ant-input::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.login-box :deep(.ant-input-password) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

.login-box :deep(.ant-input-password .ant-input) {
  color: #fff;
}

.login-box :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.login-box :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #768ef0 0%, #865bb2 100%);
}

/* 移动端适配 */
@media (max-width: 576px) {
  .login-box {
    width: 90%;
    padding: 28px 20px;
  }
  .login-container {
    background-size: cover; /* 小屏幕用 cover 填满 */
  }
}
</style>
