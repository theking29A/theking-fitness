<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserList, toggleUserStatus } from '@/api/admin'
import { message } from 'ant-design-vue'

const loading = ref(false)
const dataSource = ref([])
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const keyword = ref('')

const columns = [
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '账号', dataIndex: 'account' },
  { title: '昵称', dataIndex: 'nickname' },
  { title: '邮箱', dataIndex: 'email' },
  { title: '角色', dataIndex: 'role', key: 'role' },
  { title: '操作', key: 'action', width: 120 }
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

onMounted(fetchData)
</script>

<template>
  <div>
    <h2 style="margin-bottom: 24px">用户管理</h2>

    <a-card style="margin-bottom: 24px">
      <a-input-search
        v-model:value="keyword"
        placeholder="搜索账号或昵称"
        enter-button
        style="width: 300px"
        @search="handleSearch"
      />
    </a-card>

    <a-card>
      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'role'">
            <a-tag :color="record.role === 'ADMIN' ? 'gold' : 'blue'">
              {{ record.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleToggle(record)">
              切换状态
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>
