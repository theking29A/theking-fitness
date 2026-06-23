<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { adminLogin } from '@/api/admin'
import { Form, Input, Button, message } from 'ant-design-vue'

// 显式声明组件，Vue 3 <script setup> 会自动注册为局部组件
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
    // 验证失败，表单会自动显示红色提示
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.2);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-header img {
  width: 56px;
  height: 56px;
  margin-bottom: 12px;
}
.login-header h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 4px 0;
}
.login-header p {
  color: #999;
  margin: 0;
  font-size: 14px;
}
</style>
