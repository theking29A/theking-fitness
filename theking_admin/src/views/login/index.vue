<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { adminLogin } from '@/api/admin'
import { message } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ account: '', password: '' })
const loading = ref(false)

const handleLogin = async () => {
  if (!form.value.account || !form.value.password) {
    message.error('请输入账号和密码')
    return
  }
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
      <a-form layout="vertical">
        <a-form-item label="管理员账号">
          <a-input v-model:value="form.account" size="large" placeholder="请输入管理员账号" @pressEnter="handleLogin" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-model:value="form.password" size="large" placeholder="请输入密码" @pressEnter="handleLogin" />
        </a-form-item>
        <a-button type="primary" size="large" block :loading="loading" @click="handleLogin">
          登录
        </a-button>
      </a-form>
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
