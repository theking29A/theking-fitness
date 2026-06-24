<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  getUserList, toggleUserStatus, getUserProfile, getUserActivities,
  getUserTrainingRecords, getUserDailyStats, getUserFavoriteExercises
} from '@/api/admin'
import {
  Table, Input, Button, Tag, Card, Modal, Descriptions, Statistic, Row, Col, message, Spin
} from 'ant-design-vue'
import { EyeOutlined } from '@ant-design/icons-vue'

const ATable = Table
const AInputSearch = Input.Search
const AButton = Button
const ATag = Tag
const ACard = Card
const AModal = Modal
const ADescriptions = Descriptions
const ADescriptionsItem = Descriptions.Item
const AStatistic = Statistic
const ARow = Row
const ACol = Col
const ASpin = Spin

const loading = ref(false)
const dataSource = ref([])
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const keyword = ref('')

const detailModalVisible = ref(false)
const detailLoading = ref(false)
const selectedUser = ref<any>({})
const userProfile = ref<any>({})
const userActivities = ref<any[]>([])
const userRecords = ref<any[]>([])
const userDailyStats = ref<any[]>([])
const userFavorites = ref<any[]>([])

const columns = [
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '账号', dataIndex: 'account' },
  { title: '昵称', dataIndex: 'nickname' },
  { title: '邮箱', dataIndex: 'email' },
  { title: '角色', dataIndex: 'role', key: 'role' },
  { title: '操作', key: 'action', width: 180 }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await getUserList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      keyword: keyword.value
    })
    if (res.code === 200) {
      dataSource.value = res.data
      pagination.value.total = res.total
    } else if (res.code === 401) {
      message.error('登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    } else {
      message.error(res.message || '获取用户列表失败')
    }
  } catch (e) {
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchData()
}

const handleTableChange = (pag: any) => {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleToggle = async (record: any) => {
  try {
    const res: any = await toggleUserStatus(record.id)
    if (res.code === 200) {
      message.success('操作成功')
      fetchData()
    } else {
      message.error(res.message)
    }
  } catch (e) {
    message.error('操作失败')
  }
}

const showUserDetail = async (record: any) => {
  selectedUser.value = record
  detailModalVisible.value = true
  detailLoading.value = true
  try {
    const [profileRes, activityRes, recordRes, dailyRes, favRes] = await Promise.all([
      getUserProfile(record.id),
      getUserActivities(record.id),
      getUserTrainingRecords(record.id, { page: 0, size: 10 }),
      getUserDailyStats(record.id, 30),
      getUserFavoriteExercises(record.id)
    ])
    if (profileRes.code === 200) userProfile.value = profileRes.data
    if (activityRes.code === 200) userActivities.value = activityRes.data
    if (recordRes.code === 200) userRecords.value = recordRes.data.content || []
    if (dailyRes.code === 200) userDailyStats.value = dailyRes.data
    if (favRes.code === 200) userFavorites.value = favRes.data
  } catch (e) {
    message.error('加载用户详情失败')
  } finally {
    detailLoading.value = false
  }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <h2 style="margin-bottom: 24px">用户管理</h2>

    <ACard style="margin-bottom: 24px">
      <AInputSearch
        v-model:value="keyword"
        placeholder="搜索账号或昵称"
        enter-button
        style="width: 300px"
        @search="handleSearch"
      />
    </ACard>

    <ACard>
      <ATable
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'role'">
            <ATag :color="record.role === 'ADMIN' ? 'gold' : 'blue'">
              {{ record.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </ATag>
          </template>
          <template v-if="column.key === 'action'">
            <AButton type="link" size="small" @click="showUserDetail(record)">
              <EyeOutlined /> 详情
            </AButton>
            <AButton type="link" size="small" @click="handleToggle(record)">
              切换状态
            </AButton>
          </template>
        </template>
      </ATable>
    </ACard>

    <!-- 用户详情弹窗 -->
    <AModal
      v-model:open="detailModalVisible"
      :title="'用户详情：' + (selectedUser.account || '')"
      width="900px"
      :footer="null"
    >
      <ASpin :spinning="detailLoading" tip="加载中...">
        <ARow :gutter="[16, 16]">
          <ACol :span="8">
            <ACard>
              <AStatistic title="总训练次数" :value="userProfile.totalWorkouts || 0" />
            </ACard>
          </ACol>
          <ACol :span="8">
            <ACard>
              <AStatistic title="消耗卡路里" :value="userProfile.totalCalories || 0" suffix="kcal" />
            </ACard>
          </ACol>
          <ACol :span="8">
            <ACard>
              <AStatistic title="训练时长" :value="Math.floor((userProfile.totalDuration || 0) / 60)" suffix="分钟" />
            </ACard>
          </ACol>
        </ARow>

        <ADescriptions style="margin-top: 16px" bordered>
          <ADescriptionsItem label="ID">{{ userProfile.id }}</ADescriptionsItem>
          <ADescriptionsItem label="账号">{{ userProfile.account }}</ADescriptionsItem>
          <ADescriptionsItem label="昵称">{{ userProfile.nickname }}</ADescriptionsItem>
          <ADescriptionsItem label="邮箱">{{ userProfile.email }}</ADescriptionsItem>
          <ADescriptionsItem label="角色">{{ userProfile.role }}</ADescriptionsItem>
          <ADescriptionsItem label="注册时间">{{ userProfile.createdAt ? userProfile.createdAt.replace('T', ' ').substring(0, 19) : '-' }}</ADescriptionsItem>
        </ADescriptions>

        <h3 style="margin-top: 24px">最近训练记录</h3>
        <ATable
          :data-source="userRecords"
          row-key="id"
          :pagination="false"
          size="small"
        >
          <ATable.Column title="动作ID" data-index="exerciseId" width="80" />
          <ATable.Column title="完成组数" data-index="setsCompleted" width="80" />
          <ATable.Column title="次数" data-index="repsCompleted" width="80" />
          <ATable.Column title="时长(秒)" data-index="durationSeconds" width="100" />
          <ATable.Column title="卡路里" data-index="caloriesBurned" width="80" />
          <ATable.Column title="完成时间" data-index="completedAt" width="160">
            <template #default="{ text }">
              {{ text ? text.replace('T', ' ').substring(0, 19) : '-' }}
            </template>
          </ATable.Column>
        </ATable>

        <h3 style="margin-top: 24px">最近活动</h3>
        <ATable
          :data-source="userActivities"
          row-key="id"
          :pagination="false"
          size="small"
        >
          <ATable.Column title="活动类型" data-index="activityType" width="120" />
          <ATable.Column title="对象ID" data-index="targetId" width="80" />
          <ATable.Column title="IP" data-index="ipAddress" width="120" />
          <ATable.Column title="时间" data-index="createdAt" width="160">
            <template #default="{ text }">
              {{ text ? text.replace('T', ' ').substring(0, 19) : '-' }}
            </template>
          </ATable.Column>
        </ATable>
      </ASpin>
    </AModal>
  </div>
</template>
