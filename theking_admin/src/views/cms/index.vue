<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Table, Button, Modal, Form, Input, Select, Tag, Space, Tabs, Card,
  message, Pagination, Popconfirm, Switch, Upload
} from 'ant-design-vue'
import {
  PlusOutlined, EditOutlined, DeleteOutlined, UploadOutlined
} from '@ant-design/icons-vue'
import {
  getExerciseList, createExercise, updateExercise, deleteExercise,
  toggleExerciseStatus, getPlanList, createPlan, updatePlan,
  deletePlan, togglePlanStatus, getPlanExercises, setPlanExercises,
  uploadFile
} from '@/api/admin'

const ATable = Table
const AButton = Button
const AModal = Modal
const AForm = Form
const AFormItem = Form.Item
const AInput = Input
const ASelect = Select
const ASelectOption = Select.Option
const ATag = Tag
const ASpace = Space
const ATabs = Tabs
const ATabPane = Tabs.TabPane
const ACard = Card
const APagination = Pagination
const ASwitch = Switch
const AUpload = Upload
const AImage = Image
const APopconfirm = Popconfirm

// ========== 通用状态 ==========
const activeTab = ref('exercises')
const loading = ref(false)

// ========== 动作管理 ==========
const exerciseList = ref<any[]>([])
const exercisePage = ref(1)
const exercisePageSize = ref(10)
const exerciseTotal = ref(0)
const exerciseModalVisible = ref(false)
const exerciseModalTitle = ref('新增动作')
const exerciseForm = ref({
  id: null as number | null,
  name: '',
  description: '',
  category: 'chest',
  targetMuscles: '',
  difficulty: 'beginner',
  equipment: '',
  videoUrl: '',
  imageUrl: '',
  tips: '',
  caloriesPerMin: 5,
  durationSeconds: 60,
  sortOrder: 0
})
const exerciseFormRef = ref<any>(null)

const exerciseColumns = [
  { title: 'ID', dataIndex: 'id', width: 60 },
  { title: '动作名称', dataIndex: 'name', width: 150 },
  { title: '分类', dataIndex: 'category', width: 80 },
  { title: '难度', dataIndex: 'difficulty', width: 80 },
  { title: '器械', dataIndex: 'equipment', width: 120 },
  { title: '状态', dataIndex: 'status', width: 80 },
  { title: '排序', dataIndex: 'sortOrder', width: 60 },
  { title: '操作', key: 'action', width: 180 }
]

const categoryMap: Record<string, string> = {
  chest: '胸部', back: '背部', legs: '腿部', shoulders: '肩部',
  arms: '手臂', core: '核心', cardio: '有氧'
}
const difficultyMap: Record<string, string> = {
  beginner: '初级', intermediate: '中级', advanced: '高级'
}
const difficultyColor: Record<string, string> = {
  beginner: 'green', intermediate: 'orange', advanced: 'red'
}

function fetchExercises() {
  loading.value = true
  getExerciseList({ page: exercisePage.value - 1, size: exercisePageSize.value }).then((res: any) => {
    if (res.code === 200) {
      exerciseList.value = res.data.content || []
      exerciseTotal.value = res.data.totalElements || 0
    }
  }).finally(() => loading.value = false)
}

function openExerciseModal(row?: any) {
  if (row) {
    exerciseModalTitle.value = '编辑动作'
    exerciseForm.value = { ...row }
  } else {
    exerciseModalTitle.value = '新增动作'
    exerciseForm.value = {
      id: null, name: '', description: '', category: 'chest',
      targetMuscles: '', difficulty: 'beginner', equipment: '',
      videoUrl: '', imageUrl: '', tips: '', caloriesPerMin: 5,
      durationSeconds: 60, sortOrder: 0
    }
  }
  exerciseModalVisible.value = true
}

function saveExercise() {
  exerciseFormRef.value?.validate().then(() => {
    const data = { ...exerciseForm.value }
    const promise = data.id
      ? updateExercise(data.id, data)
      : createExercise(data)
    promise.then((res: any) => {
      if (res.code === 200) {
        message.success(data.id ? '更新成功' : '创建成功')
        exerciseModalVisible.value = false
        fetchExercises()
      } else {
        message.error(res.message || '操作失败')
      }
    })
  })
}

function handleDeleteExercise(id: number) {
  deleteExercise(id).then((res: any) => {
    if (res.code === 200) {
      message.success('删除成功')
      fetchExercises()
    } else {
      message.error(res.message || '删除失败')
    }
  })
}

function handleToggleExercise(id: number) {
  toggleExerciseStatus(id).then((res: any) => {
    if (res.code === 200) {
      message.success('状态更新成功')
      fetchExercises()
    }
  })
}

// ========== 文件上传 ==========
const uploadingImage = ref(false)
const uploadingVideo = ref(false)

function handleUpload(file: File, type: 'image' | 'video', formRef: any, field: string) {
  if (type === 'image') uploadingImage.value = true
  else uploadingVideo.value = true

  uploadFile(file).then((res: any) => {
    if (res.code === 200) {
      message.success('上传成功')
      formRef[field] = res.data.url
    } else {
      message.error(res.message || '上传失败')
    }
  }).catch(() => {
    message.error('上传失败')
  }).finally(() => {
    if (type === 'image') uploadingImage.value = false
    else uploadingVideo.value = false
  })
  return false // 阻止默认上传
}
const planList = ref<any[]>([])
const planPage = ref(1)
const planPageSize = ref(10)
const planTotal = ref(0)
const planModalVisible = ref(false)
const planModalTitle = ref('新增计划')
const planForm = ref({
  id: null as number | null,
  name: '',
  description: '',
  goal: 'muscle_building',
  level: 'beginner',
  durationWeeks: 4,
  daysPerWeek: 3,
  coverImage: '',
  estimatedTimeMin: 60,
  sortOrder: 0
})
const planFormRef = ref<any>(null)

const planColumns = [
  { title: 'ID', dataIndex: 'id', width: 60 },
  { title: '计划名称', dataIndex: 'name', width: 180 },
  { title: '目标', dataIndex: 'goal', width: 100 },
  { title: '难度', dataIndex: 'level', width: 80 },
  { title: '周期', dataIndex: 'durationWeeks', width: 80 },
  { title: '状态', dataIndex: 'status', width: 80 },
  { title: '操作', key: 'action', width: 180 }
]

const goalMap: Record<string, string> = {
  muscle_building: '增肌', fat_loss: '减脂', strength: '力量',
  endurance: '耐力', flexibility: '柔韧'
}

function fetchPlans() {
  loading.value = true
  getPlanList({ page: planPage.value - 1, size: planPageSize.value }).then((res: any) => {
    if (res.code === 200) {
      planList.value = res.data.content || []
      planTotal.value = res.data.totalElements || 0
    }
  }).finally(() => loading.value = false)
}

function openPlanModal(row?: any) {
  if (row) {
    planModalTitle.value = '编辑计划'
    planForm.value = { ...row }
  } else {
    planModalTitle.value = '新增计划'
    planForm.value = {
      id: null, name: '', description: '', goal: 'muscle_building',
      level: 'beginner', durationWeeks: 4, daysPerWeek: 3,
      coverImage: '', estimatedTimeMin: 60, sortOrder: 0
    }
  }
  planModalVisible.value = true
}

function savePlan() {
  planFormRef.value?.validate().then(() => {
    const data = { ...planForm.value }
    const promise = data.id ? updatePlan(data.id, data) : createPlan(data)
    promise.then((res: any) => {
      if (res.code === 200) {
        message.success(data.id ? '更新成功' : '创建成功')
        planModalVisible.value = false
        fetchPlans()
      } else {
        message.error(res.message || '操作失败')
      }
    })
  })
}

function handleDeletePlan(id: number) {
  deletePlan(id).then((res: any) => {
    if (res.code === 200) {
      message.success('删除成功')
      fetchPlans()
    }
  })
}

function handleTogglePlan(id: number) {
  togglePlanStatus(id).then((res: any) => {
    if (res.code === 200) {
      message.success('状态更新成功')
      fetchPlans()
    }
  })
}

// ========== 初始化 ==========
onMounted(() => {
  fetchExercises()
  fetchPlans()
})
</script>

<template>
  <div class="cms-page">
    <h2>内容管理</h2>
    <ATabs v-model:activeKey="activeTab">
      <ATabPane key="exercises" tab="健身动作">
        <ACard>
          <div class="toolbar">
            <AButton type="primary" @click="openExerciseModal()">
              <PlusOutlined /> 新增动作
            </AButton>
          </div>
          <ATable
            :columns="exerciseColumns"
            :data-source="exerciseList"
            :loading="loading"
            row-key="id"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'category'">
                <ATag>{{ categoryMap[record.category] || record.category }}</ATag>
              </template>
              <template v-if="column.dataIndex === 'difficulty'">
                <ATag :color="difficultyColor[record.difficulty]">
                  {{ difficultyMap[record.difficulty] || record.difficulty }}
                </ATag>
              </template>
              <template v-if="column.dataIndex === 'status'">
                <ASwitch
                  :checked="record.status === 1"
                  @change="handleToggleExercise(record.id)"
                  checked-children="上架"
                  un-checked-children="下架"
                />
              </template>
              <template v-if="column.key === 'action'">
                <ASpace>
                  <AButton size="small" @click="openExerciseModal(record)">
                    <EditOutlined /> 编辑
                  </AButton>
                  <APopconfirm
                    title="确定删除此动作？"
                    @confirm="handleDeleteExercise(record.id)"
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
              v-model:current="exercisePage"
              v-model:page-size="exercisePageSize"
              :total="exerciseTotal"
              show-size-changer
              @change="fetchExercises"
            />
          </div>
        </ACard>
      </ATabPane>

      <ATabPane key="plans" tab="训练计划">
        <ACard>
          <div class="toolbar">
            <AButton type="primary" @click="openPlanModal()">
              <PlusOutlined /> 新增计划
            </AButton>
          </div>
          <ATable
            :columns="planColumns"
            :data-source="planList"
            :loading="loading"
            row-key="id"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'goal'">
                <ATag>{{ goalMap[record.goal] || record.goal }}</ATag>
              </template>
              <template v-if="column.dataIndex === 'level'">
                <ATag :color="difficultyColor[record.level]">
                  {{ difficultyMap[record.level] || record.level }}
                </ATag>
              </template>
              <template v-if="column.dataIndex === 'durationWeeks'">
                {{ record.durationWeeks }} 周
              </template>
              <template v-if="column.dataIndex === 'status'">
                <ASwitch
                  :checked="record.status === 1"
                  @change="handleTogglePlan(record.id)"
                  checked-children="上架"
                  un-checked-children="下架"
                />
              </template>
              <template v-if="column.key === 'action'">
                <ASpace>
                  <AButton size="small" @click="openPlanModal(record)">
                    <EditOutlined /> 编辑
                  </AButton>
                  <APopconfirm
                    title="确定删除此计划？"
                    @confirm="handleDeletePlan(record.id)"
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
              v-model:current="planPage"
              v-model:page-size="planPageSize"
              :total="planTotal"
              show-size-changer
              @change="fetchPlans"
            />
          </div>
        </ACard>
      </ATabPane>
    </ATabs>

    <!-- 动作编辑弹窗 -->
    <AModal
      v-model:open="exerciseModalVisible"
      :title="exerciseModalTitle"
      @ok="saveExercise"
      width="700px"
    >
      <AForm
        ref="exerciseFormRef"
        :model="exerciseForm"
        layout="vertical"
      >
        <AFormItem label="动作名称" name="name" :rules="[{ required: true, message: '请输入动作名称' }]">
          <AInput v-model:value="exerciseForm.name" />
        </AFormItem>
        <AFormItem label="描述" name="description">
          <AInput.TextArea v-model:value="exerciseForm.description" :rows="3" />
        </AFormItem>
        <AFormItem label="分类" name="category">
          <ASelect v-model:value="exerciseForm.category">
            <ASelectOption value="chest">胸部</ASelectOption>
            <ASelectOption value="back">背部</ASelectOption>
            <ASelectOption value="legs">腿部</ASelectOption>
            <ASelectOption value="shoulders">肩部</ASelectOption>
            <ASelectOption value="arms">手臂</ASelectOption>
            <ASelectOption value="core">核心</ASelectOption>
            <ASelectOption value="cardio">有氧</ASelectOption>
          </ASelect>
        </AFormItem>
        <AFormItem label="目标肌群" name="targetMuscles">
          <AInput v-model:value="exerciseForm.targetMuscles" />
        </AFormItem>
        <AFormItem label="难度" name="difficulty">
          <ASelect v-model:value="exerciseForm.difficulty">
            <ASelectOption value="beginner">初级</ASelectOption>
            <ASelectOption value="intermediate">中级</ASelectOption>
            <ASelectOption value="advanced">高级</ASelectOption>
          </ASelect>
        </AFormItem>
        <AFormItem label="所需器械" name="equipment">
          <AInput v-model:value="exerciseForm.equipment" />
        </AFormItem>
        <AFormItem label="视频URL" name="videoUrl">
          <AInput v-model:value="exerciseForm.videoUrl" />
          <AUpload
            :before-upload="(file: File) => handleUpload(file, 'video', exerciseForm, 'videoUrl')"
            :show-upload-list="false"
          >
            <AButton size="small" :loading="uploadingVideo">
              <UploadOutlined /> 上传视频
            </AButton>
          </AUpload>
        </AFormItem>
        <AFormItem label="图片URL" name="imageUrl">
          <AInput v-model:value="exerciseForm.imageUrl" />
          <AUpload
            :before-upload="(file: File) => handleUpload(file, 'image', exerciseForm, 'imageUrl')"
            :show-upload-list="false"
          >
            <AButton size="small" :loading="uploadingImage">
              <UploadOutlined /> 上传图片
            </AButton>
          </AUpload>
        </AFormItem>
        <AFormItem label="动作要点" name="tips">
          <AInput.TextArea v-model:value="exerciseForm.tips" :rows="3" />
        </AFormItem>
        <AFormItem label="卡路里/分钟" name="caloriesPerMin">
          <AInput v-model:value="exerciseForm.caloriesPerMin" type="number" />
        </AFormItem>
        <AFormItem label="时长（秒）" name="durationSeconds">
          <AInput v-model:value="exerciseForm.durationSeconds" type="number" />
        </AFormItem>
        <AFormItem label="排序" name="sortOrder">
          <AInput v-model:value="exerciseForm.sortOrder" type="number" />
        </AFormItem>
      </AForm>
    </AModal>

    <!-- 计划编辑弹窗 -->
    <AModal
      v-model:open="planModalVisible"
      :title="planModalTitle"
      @ok="savePlan"
      width="700px"
    >
      <AForm
        ref="planFormRef"
        :model="planForm"
        layout="vertical"
      >
        <AFormItem label="计划名称" name="name" :rules="[{ required: true, message: '请输入计划名称' }]">
          <AInput v-model:value="planForm.name" />
        </AFormItem>
        <AFormItem label="描述" name="description">
          <AInput.TextArea v-model:value="planForm.description" :rows="3" />
        </AFormItem>
        <AFormItem label="目标" name="goal">
          <ASelect v-model:value="planForm.goal">
            <ASelectOption value="muscle_building">增肌</ASelectOption>
            <ASelectOption value="fat_loss">减脂</ASelectOption>
            <ASelectOption value="strength">力量</ASelectOption>
            <ASelectOption value="endurance">耐力</ASelectOption>
            <ASelectOption value="flexibility">柔韧</ASelectOption>
          </ASelect>
        </AFormItem>
        <AFormItem label="难度" name="level">
          <ASelect v-model:value="planForm.level">
            <ASelectOption value="beginner">初级</ASelectOption>
            <ASelectOption value="intermediate">中级</ASelectOption>
            <ASelectOption value="advanced">高级</ASelectOption>
          </ASelect>
        </AFormItem>
        <AFormItem label="周期（周）" name="durationWeeks">
          <AInput v-model:value="planForm.durationWeeks" type="number" />
        </AFormItem>
        <AFormItem label="每周天数" name="daysPerWeek">
          <AInput v-model:value="planForm.daysPerWeek" type="number" />
        </AFormItem>
        <AFormItem label="封面图片" name="coverImage">
          <AInput v-model:value="planForm.coverImage" />
          <AUpload
            :before-upload="(file: File) => handleUpload(file, 'image', planForm, 'coverImage')"
            :show-upload-list="false"
          >
            <AButton size="small" :loading="uploadingImage">
              <UploadOutlined /> 上传图片
            </AButton>
          </AUpload>
        </AFormItem>
        <AFormItem label="单次时长（分钟）" name="estimatedTimeMin">
          <AInput v-model:value="planForm.estimatedTimeMin" type="number" />
        </AFormItem>
        <AFormItem label="排序" name="sortOrder">
          <AInput v-model:value="planForm.sortOrder" type="number" />
        </AFormItem>
      </AForm>
    </AModal>
  </div>
</template>

<style scoped>
.cms-page {
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
