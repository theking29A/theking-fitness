<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Card, Statistic, Row, Col, Spin, message } from 'ant-design-vue'
import { UserOutlined, FireOutlined, BookOutlined, TeamOutlined } from '@ant-design/icons-vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  GridComponent, TooltipComponent, LegendComponent,
  TitleComponent, ToolboxComponent, DataZoomComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import {
  getDashboardOverview, getUserGrowthTrend, getDailyActiveUsers,
  getActivityDistribution, getContentStats
} from '@/api/admin'

use([
  CanvasRenderer, LineChart, BarChart, PieChart,
  GridComponent, TooltipComponent, LegendComponent,
  TitleComponent, ToolboxComponent, DataZoomComponent
])

const ACard = Card
const AStatistic = Statistic
const ARow = Row
const ACol = Col
const ASpin = Spin

const loading = ref(true)
const overview = ref<any>({})
const userGrowthData = ref<any[]>([])
const dauData = ref<any[]>([])
const activityData = ref<any>({})
const contentData = ref<any>({})

// 用户增长趋势图配置
const userGrowthOption = ref({})
// DAU 图配置
const dauOption = ref({})
// 活动分布图配置
const activityPieOption = ref({})
// 内容分类图配置
const contentPieOption = ref({})

function updateUserGrowthChart() {
  const dates = userGrowthData.value.map((d: any) => d.date.slice(5))
  const values = userGrowthData.value.map((d: any) => d.newUsers)
  userGrowthOption.value = {
    title: { text: '用户增长趋势（30天）', left: 'center' },
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
    xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
    yAxis: { type: 'value', minInterval: 1 },
    dataZoom: [{ type: 'inside', start: 50, end: 100 }],
    series: [{
      name: '新增用户',
      type: 'line',
      data: values,
      smooth: true,
      areaStyle: {
        color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: 'rgba(24, 144, 255, 0.3)' }, { offset: 1, color: 'rgba(24, 144, 255, 0.05)' }]
        }
      },
      itemStyle: { color: '#1890ff' }
    }]
  }
}

function updateDauChart() {
  const dates = dauData.value.map((d: any) => d.date.slice(5))
  const values = dauData.value.map((d: any) => d.dau)
  dauOption.value = {
    title: { text: '日活跃用户 DAU（30天）', left: 'center' },
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
    xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
    yAxis: { type: 'value', minInterval: 1 },
    dataZoom: [{ type: 'inside', start: 50, end: 100 }],
    series: [{
      name: '活跃用户',
      type: 'bar',
      data: values,
      itemStyle: { color: '#52c41a', borderRadius: [4, 4, 0, 0] }
    }]
  }
}

function updateActivityChart() {
  const list = activityData.value?.activityTypes || []
  const data = list.map((item: any) => ({ name: item.type, value: item.count }))
  const typeMap: Record<string, string> = {
    LOGIN: '登录', VIEW_EXERCISE: '浏览动作', START_PLAN: '开始计划', COMPLETE_WORKOUT: '完成训练'
  }
  activityPieOption.value = {
    title: { text: '用户活动分布（7天）', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}: {c}' },
      data: data.map((d: any) => ({ ...d, name: typeMap[d.name] || d.name }))
    }]
  }
}

function updateContentChart() {
  const list = contentData.value?.exercisesByCategory || []
  const categoryMap: Record<string, string> = {
    chest: '胸部', back: '背部', legs: '腿部', shoulders: '肩部',
    arms: '手臂', core: '核心', cardio: '有氧'
  }
  const data = list.map((item: any) => ({
    name: categoryMap[item.category] || item.category,
    value: item.count
  }))
  contentPieOption.value = {
    title: { text: '动作分类分布', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: '60%',
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}: {c}' },
      data
    }]
  }
}

onMounted(() => {
  Promise.all([
    getDashboardOverview(),
    getUserGrowthTrend(30),
    getDailyActiveUsers(30),
    getActivityDistribution(),
    getContentStats()
  ]).then((results) => {
    const [overviewRes, growthRes, dauRes, activityRes, contentRes] = results
    if (overviewRes.code === 200) overview.value = overviewRes.data
    if (growthRes.code === 200) {
      userGrowthData.value = growthRes.data
      updateUserGrowthChart()
    }
    if (dauRes.code === 200) {
      dauData.value = dauRes.data
      updateDauChart()
    }
    if (activityRes.code === 200) {
      activityData.value = activityRes.data
      updateActivityChart()
    }
    if (contentRes.code === 200) {
      contentData.value = contentRes.data
      updateContentChart()
    }
  }).catch((err) => {
    message.error('数据加载失败')
  }).finally(() => {
    loading.value = false
  })
})
</script>

<template>
  <div class="dashboard-page">
    <h2>数据看板</h2>
    <ASpin :spinning="loading" tip="加载中...">
      <!-- 统计卡片 -->
      <ARow :gutter="[16, 16]">
        <ACol :span="6">
          <ACard>
            <AStatistic title="总用户" :value="overview.totalUsers || 0">
              <template #prefix>
                <UserOutlined style="color: #1890ff" />
              </template>
            </AStatistic>
          </ACard>
        </ACol>
        <ACol :span="6">
          <ACard>
            <AStatistic title="今日注册" :value="overview.todayRegister || 0">
              <template #prefix>
                <TeamOutlined style="color: #52c41a" />
              </template>
            </AStatistic>
          </ACard>
        </ACol>
        <ACol :span="6">
          <ACard>
            <AStatistic title="健身动作" :value="overview.totalExercises || contentData.totalExercises || 0">
              <template #prefix>
                <FireOutlined style="color: #fa8c16" />
              </template>
            </AStatistic>
          </ACard>
        </ACol>
        <ACol :span="6">
          <ACard>
            <AStatistic title="训练计划" :value="overview.totalPlans || contentData.totalPlans || 0">
              <template #prefix>
                <BookOutlined style="color: #722ed1" />
              </template>
            </AStatistic>
          </ACard>
        </ACol>
      </ARow>

      <!-- 图表区域 -->
      <ARow :gutter="[16, 16]" style="margin-top: 16px">
        <ACol :span="12">
          <ACard>
            <v-chart class="chart" :option="userGrowthOption" autoresize />
          </ACard>
        </ACol>
        <ACol :span="12">
          <ACard>
            <v-chart class="chart" :option="dauOption" autoresize />
          </ACard>
        </ACol>
      </ARow>

      <ARow :gutter="[16, 16]" style="margin-top: 16px">
        <ACol :span="12">
          <ACard>
            <v-chart class="chart" :option="activityPieOption" autoresize />
          </ACard>
        </ACol>
        <ACol :span="12">
          <ACard>
            <v-chart class="chart" :option="contentPieOption" autoresize />
          </ACard>
        </ACol>
      </ARow>
    </ASpin>
  </div>
</template>

<style scoped>
.dashboard-page {
  padding: 24px;
}
.chart {
  height: 320px;
}
</style>
