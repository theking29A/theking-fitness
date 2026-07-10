<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Table, Button, Modal, Form, Input, Select, DatePicker, Tag, Space, Card,
  message, Pagination, Popconfirm
} from 'ant-design-vue'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import {
  getAnnouncementList, createAnnouncement, updateAnnouncement,
  deleteAnnouncement, toggleAnnouncementStatus
} from '@/api/admin'

const ATable = Table
const AButton = Button
const AModal = Modal
const AForm = Form
const AFormItem = Form.Item
const AInput = Input
const ASelect = Select
const ASelectOption = Select.Option
const ADatePicker = DatePicker
const ATag = Tag
const ASpace = Space
const ACard = Card
const APagination = Pagination
const APopconfirm = Popconfirm

const loading = ref(false)
const list = ref<any[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const modalVisible = ref(false)
const modalTitle = ref('新增公告')
const form = ref({
  id: null as number | null,
  title: '',
  content: '',
  type: 'banner',
  target: 'all',
  status: 1,
  sortOrder: 0,
  startTime: null as any,
  endTime: null as any
})
const formRef = ref<any>(null)

const columns = [
  { title: 'ID', dataIndex: 'id', width: 60 },
  { title: '标题', dataIndex: 'title', width: 200 },
  { title: '类型', dataIndex: 'type', width: 80 },
  { title: '目标', dataIndex: 'target', width: 80 },
  { title: '状态', dataIndex: 'status', width: 80 },
  { title: '排序', dataIndex: 'sortOrder', width: 60 },
  { title: '生效时间', dataIndex: 'startTime', width: 160 },
  { title: '操作', key: 'action', width: 180 }
]

const typeMap: Record<string, string> = { banner: '顶部通知', popup: '弹窗' }
const targetMap: Record<string, string> = { all: '全部', user: '用户', admin: '管理员' }

function fetchList() {
  loading.value = true
  getAnnouncementList({ page: page.value - 1, size: pageSize.value }).then((res: any) => {
    if (res.code === 200) {
      list.value = res.data.content || []
      total.value = res.data.totalElements || 0
    }
  }).finally(() => loading.value = false)
}

function openModal(row?: any) {
  if (row) {
    modalTitle.value = '编辑公告'
    form.value = {
      ...row,
      startTime: row.startTime ? row.startTime : null,
      endTime: row.endTime ? row.endTime : null
    }
  } else {
    modalTitle.value = '新增公告'
    form.value = {
      id: null, title: '', content: '', type: 'banner', target: 'all',
      status: 1, sortOrder: 0, startTime: null, endTime: null
    }
  }
  modalVisible.value = true
}

function save() {
  formRef.value?.validate().then(() => {
    const data = { ...form.value }
    if (data.startTime && typeof data.startTime === 'object') {
      data.startTime = data.startTime.format('YYYY-MM-DD HH:mm:ss')
    }
    if (data.endTime && typeof data.endTime === 'object') {
      data.endTime = data.endTime.format('YYYY-MM-DD HH:mm:ss')
    }
    const promise = data.id ? updateAnnouncement(data.id, data) : createAnnouncement(data)
    promise.then((res: any) => {
      if (res.code === 200) {
        message.success(data.id ? '更新成功' : '创建成功')
        modalVisible.value = false
        fetchList()
      } else {
        message.error(res.message || '操作失败')
      }
    })
  })
}

function handleDelete(id: number) {
  deleteAnnouncement(id).then((res: any) => {
    if (res.code === 200) {
      message.success('删除成功')
      fetchList()
    }
  })
}

function handleToggle(id: number) {
  toggleAnnouncementStatus(id).then((res: any) => {
    if (res.code === 200) {
      message.success('状态更新成功')
      fetchList()
    }
  })
}

onMounted(() => {
  fetchList()
})
</script>

<template>
  <div class="announcements-page">
    <h2>公告管理</h2>
    <ACard>
      <div class="toolbar">
        <AButton type="primary" @click="openModal()">
          <PlusOutlined /> 新增公告
        </AButton>
      </div>
      <ATable
        :columns="columns"
        :data-source="list"
        :loading="loading"
        row-key="id"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'type'">
            <ATag>{{ typeMap[record.type] || record.type }}</ATag>
          </template>
          <template v-if="column.dataIndex === 'target'">
            <ATag>{{ targetMap[record.target] || record.target }}</ATag>
          </template>
          <template v-if="column.dataIndex === 'status'">
            <ATag :color="record.status === 1 ? 'green' : 'red'">
              {{ record.status === 1 ? '上架' : '下架' }}
            </ATag>
          </template>
          <template v-if="column.dataIndex === 'startTime'">
            {{ record.startTime ? record.startTime.replace('T', ' ').substring(0, 16) : '-' }}
          </template>
          <template v-if="column.key === 'action'">
            <ASpace>
              <AButton size="small" @click="openModal(record)">
                <EditOutlined /> 编辑
              </AButton>
              <AButton size="small" @click="handleToggle(record.id)">
                {{ record.status === 1 ? '下架' : '上架' }}
              </AButton>
              <APopconfirm
                title="确定删除此公告？"
                @confirm="handleDelete(record.id)"
              >
                <AButton size="small" danger>
                  <DeleteOutlined /> 删除
                </AButton>
              </APopconfirm>
            </ASpace>
          </template>
        </template>
      </ATable>
      <div class="pagination-wrap">
        <APagination
          v-model:current="page"
          v-model:page-size="pageSize"
          :total="total"
          show-size-changer
          @change="fetchList"
        />
      </div>
    </ACard>

    <AModal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="save"
      width="700px"
    >
      <AForm ref="formRef" :model="form" layout="vertical">
        <AFormItem label="标题" name="title" :rules="[{ required: true, message: '请输入标题' }]">
          <AInput v-model:value="form.title" />
        </AFormItem>
        <AFormItem label="内容" name="content">
          <AInput.TextArea v-model:value="form.content" :rows="5" />
        </AFormItem>
        <AFormItem label="类型" name="type">
          <ASelect v-model:value="form.type">
            <ASelectOption value="banner">顶部通知</ASelectOption>
            <ASelectOption value="popup">弹窗</ASelectOption>
          </ASelect>
        </AFormItem>
        <AFormItem label="目标" name="target">
          <ASelect v-model:value="form.target">
            <ASelectOption value="all">全部</ASelectOption>
            <ASelectOption value="user">普通用户</ASelectOption>
            <ASelectOption value="admin">管理员</ASelectOption>
          </ASelect>
        </AFormItem>
        <AFormItem label="生效时间" name="startTime">
          <ADatePicker v-model:value="form.startTime" show-time style="width: 100%" />
        </AFormItem>
        <AFormItem label="失效时间" name="endTime">
          <ADatePicker v-model:value="form.endTime" show-time style="width: 100%" />
        </AFormItem>
        <AFormItem label="排序" name="sortOrder">
          <AInput v-model:value="form.sortOrder" type="number" />
        </AFormItem>
      </AForm>
    </AModal>
  </div>
</template>

<style scoped>
.announcements-page {
  padding: 24px;
}
.toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
