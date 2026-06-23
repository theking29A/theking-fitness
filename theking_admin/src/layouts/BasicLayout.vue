<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  UserOutlined,
  SettingOutlined,
  LogoutOutlined,
  AppstoreOutlined,
  FileTextOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const collapsed = ref(false)
const selectedKeys = ref([route.path])
const openKeys = ref(['/'])

watch(() => route.path, (path) => {
  selectedKeys.value = [path]
})

const logout = () => {
  userStore.logout()
  router.push('/login')
}

const handleMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}

const menuItems = [
  { key: '/dashboard', icon: DashboardOutlined, label: '数据看板' },
  { key: '/users', icon: UserOutlined, label: '用户管理' },
  { key: '/cms', icon: AppstoreOutlined, label: '内容管理' },
  { key: '/logs', icon: FileTextOutlined, label: '操作日志' },
  { key: '/settings', icon: SettingOutlined, label: '系统设置' }
]
</script>

<template>
  <a-layout style="min-height: 100vh">
    <a-layout-sider v-model:collapsed="collapsed" collapsible theme="dark" width="220">
      <div class="logo">
        <img v-if="!collapsed" src="https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg" alt="logo" />
        <h1 v-if="!collapsed">TheKing Admin</h1>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline" @click="handleMenuClick">
        <a-menu-item v-for="item in menuItems" :key="item.key">
          <component :is="item.icon" />
          <span>{{ item.label }}</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <a-layout-header style="background: #fff; padding: 0 24px; display: flex; align-items: center; justify-content: space-between">
        <div>
          <menu-fold-outlined v-if="!collapsed" class="trigger" @click="() => (collapsed = !collapsed)" />
          <menu-unfold-outlined v-else class="trigger" @click="() => (collapsed = !collapsed)" />
        </div>
        <div style="display: flex; align-items: center; gap: 16px">
          <span style="color: rgba(0,0,0,0.65)">欢迎，{{ userStore.nickname }}</span>
          <a-button type="link" danger @click="logout">
            <logout-outlined /> 退出
          </a-button>
        </div>
      </a-layout-header>

      <a-layout-content style="margin: 24px; padding: 24px; background: #fff; border-radius: 8px; min-height: 280px">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<style scoped>
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  gap: 8px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo img { height: 32px; }
.logo h1 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  white-space: nowrap;
}
.trigger {
  font-size: 18px;
  line-height: 64px;
  padding: 0 16px;
  cursor: pointer;
  transition: color 0.3s;
}
.trigger:hover { color: #1890ff; }
</style>
