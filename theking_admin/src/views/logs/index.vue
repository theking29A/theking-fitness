<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Table, Tag, Card, Pagination, Space } from 'ant-design-vue'
import { getLogList } from '@/api/admin'

const ATable = Table
const ATag = Tag
const ACard = Card
const APagination = Pagination
const ASpace = Space

const loading = ref(false)
const logList = ref<any[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const columns = [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: '管理员', dataIndex: 'adminAccount', width: 120 },
  { title: '操作类型', dataIndex: 'operationType', width: 100 },
  { title: '操作对象', dataIndex: 'targetType', width: 100 },
  { title: '对象ID', dataIndex: 'targetId', width: 100 },
  { title: '详情', dataIndex: 'detail', ellipsis: true },
  { title: 'IP地址', dataIndex: 'ipAddress', width: 130 },
  { title: '时间', dataIndex: 'createdAt', width: 170 }
]

const operationTypeColor: Record<string, string> = {
  CREATE: 'green', UPDATE: 'blue', DELETE: 'red', LOGIN: 'purple', LOGOUT: 'orange', QUERY: 'cyan'
}

const targetTypeMap: Record<string, string> = {
  USER: '用户', EXERCISE: '动作', PLAN: '计划', SYSTEM: '系统'
}

function fetchLogs() {
  loading.value = true
  getLogList({ page: page.value - 1, size: pageSize.value }).then((res: any) => {
    if (res.code === 200) {
      logList.value = res.data.content || []
      total.value = res.data.totalElements || 0
    }
  }).finally(() => loading.value = false)
}

onMounted(() => {
  fetchLogs()
})
</script>

<template>
  <div class="logs-page">
    <h2>操作日志</h2>
    <ACard>
      <ATable
        :columns="columns"
        :data-source="logList"
        :loading="loading"
        row-key="id"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'operationType'">
            <ATag :color="operationTypeColor[record.operationType] || 'default'">
              {{ record.operationType }}
            </ATag>
          </template>
          <template v-if="column.dataIndex === 'targetType'">
            <ATag>{{ targetTypeMap[record.targetType] || record.targetType }}</ATag>
          </template>
          <template v-if="column.dataIndex === 'createdAt'">
            {{ record.createdAt ? record.createdAt.replace('T', ' ').substring(0, 19) : '-' }}
          </template>
        </template>
      </ATable>
      <div class="pagination-wrap">
        <APagination
          v-model:current="page"
          v-model:page-size="pageSize"
          :total="total"
          show-size-changer
          @change="fetchLogs"
        />
      </div>
    </ACard>
  </div>
</template>

<style scoped>
.logs-page {
  padding: 24px;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
