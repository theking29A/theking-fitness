<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getStats } from '@/api/admin'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  TeamOutlined,
  CrownOutlined,
  RiseOutlined
} from '@ant-design/icons-vue'

const stats = ref({ totalUsers: 0, userCount: 0, adminCount: 0, todayRegister: 0 })
const loading = ref(true)

const fetchStats = async () => {
  try {
    const res: any = await getStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (e) {
    message.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchStats)

const statCards = [
  { title: '总用户数', value: () => stats.value.totalUsers, icon: UserOutlined, color: '#1890ff' },
  { title: '普通用户', value: () => stats.value.userCount, icon: TeamOutlined, color: '#52c41a' },
  { title: '管理员', value: () => stats.value.adminCount, icon: CrownOutlined, color: '#faad14' },
  { title: '今日注册', value: () => stats.value.todayRegister, icon: RiseOutlined, color: '#eb2f96' }
]
</script>

<template>
  <div>
    <h2 style="margin-bottom: 24px">数据看板</h2>
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :sm="12" :lg="6" v-for="card in statCards" :key="card.title">
        <a-card :loading="loading">
          <div class="stat-card">
            <div class="stat-icon" :style="{ background: card.color + '15', color: card.color }">
              <component :is="card.icon" />
            </div>
            <div class="stat-info">
              <div class="stat-title">{{ card.title }}</div>
              <div class="stat-value" :style="{ color: card.color }">{{ card.value() }}</div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[24, 24]" style="margin-top: 24px">
      <a-col :span="24">
        <a-card title="欢迎使用 TheKing Admin">
          <p>这是一个基于 Vue 3 + Ant Design Vue 构建的健身管理后台。</p>
          <p>功能包括：用户管理、数据统计、系统设置等。</p>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}
.stat-info { flex: 1; }
.stat-title { color: #999; font-size: 14px; margin-bottom: 4px; }
.stat-value { font-size: 28px; font-weight: 700; }
</style>
