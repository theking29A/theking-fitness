<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Card, Form, Input, InputNumber, Switch, Select, Button, Space, Tag, message, Spin
} from 'ant-design-vue'
import { SaveOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { getAllSettings, batchUpdateSettings } from '@/api/admin'

const ACard = Card
const AForm = Form
const AFormItem = Form.Item
const AInput = Input
const AInputNumber = InputNumber
const ASwitch = Switch
const ASelect = Select
const ASelectOption = Select.Option
const AButton = Button
const ASpace = Space
const ATag = Tag
const ASpin = Spin

const loading = ref(false)
const saving = ref(false)

// 表单数据
const securityForm = ref({
  'security.token_expire_days': '7',
  'security.login_fail_lock_enabled': 'true',
  'security.login_fail_max_attempts': '5',
  'security.login_fail_lock_minutes': '30',
  'security.password_min_length': '6'
})

const maintenanceForm = ref({
  'maintenance.enabled': 'false',
  'maintenance.message': '系统正在维护中，请稍后再试。'
})

const displayForm = ref({
  'display.page_size_default': '10',
  'display.time_format': 'YYYY-MM-DD HH:mm:ss'
})

const uploadForm = ref({
  'upload.max_file_size_mb': '50',
  'upload.allowed_extensions': 'jpg,png,gif,mp4'
})

const categoryMap: Record<string, string> = {
  security: '安全设置',
  maintenance: '维护模式',
  display: '显示设置',
  upload: '文件上传设置'
}

function fetchSettings() {
  loading.value = true
  getAllSettings().then((res: any) => {
    if (res.code === 200) {
      const list = res.data || []
      list.forEach((item: any) => {
        if (item.category === 'security') {
          securityForm.value[item.settingKey] = item.settingValue
        } else if (item.category === 'maintenance') {
          maintenanceForm.value[item.settingKey] = item.settingValue
        } else if (item.category === 'display') {
          displayForm.value[item.settingKey] = item.settingValue
        } else if (item.category === 'upload') {
          uploadForm.value[item.settingKey] = item.settingValue
        }
      })
    }
  }).finally(() => loading.value = false)
}

function saveCategory(form: Record<string, string>) {
  saving.value = true
  batchUpdateSettings(form).then((res: any) => {
    if (res.code === 200) {
      message.success('保存成功')
    } else {
      message.error(res.message || '保存失败')
    }
  }).finally(() => saving.value = false)
}

onMounted(() => {
  fetchSettings()
})
</script>

<template>
  <div class="settings-page">
    <h2 style="margin-bottom: 24px">系统设置</h2>
    <ASpin :spinning="loading" tip="加载中...">
      <!-- 安全设置 -->
      <ACard title="安全设置" style="margin-bottom: 24px">
        <AForm layout="vertical" :model="securityForm">
          <AFormItem label="Token 过期时间（天）">
            <AInputNumber
              v-model:value="securityForm['security.token_expire_days']"
              :min="1" :max="365" style="width: 200px"
            />
          </AFormItem>
          <AFormItem label="登录失败锁定">
            <ASwitch
              v-model:checked="securityForm['security.login_fail_lock_enabled']"
              checked-value="true" unchecked-value="false"
            />
          </AFormItem>
          <AFormItem label="最大失败次数">
            <AInputNumber
              v-model:value="securityForm['security.login_fail_max_attempts']"
              :min="1" :max="20" style="width: 200px"
            />
          </AFormItem>
          <AFormItem label="锁定时间（分钟）">
            <AInputNumber
              v-model:value="securityForm['security.login_fail_lock_minutes']"
              :min="1" :max="1440" style="width: 200px"
            />
          </AFormItem>
          <AFormItem label="密码最小长度">
            <AInputNumber
              v-model:value="securityForm['security.password_min_length']"
              :min="4" :max="32" style="width: 200px"
            />
          </AFormItem>
          <AFormItem>
            <AButton type="primary" :loading="saving" @click="saveCategory(securityForm)">
              <SaveOutlined /> 保存安全设置
            </AButton>
          </AFormItem>
        </AForm>
      </ACard>

      <!-- 维护模式 -->
      <ACard title="维护模式" style="margin-bottom: 24px">
        <AForm layout="vertical" :model="maintenanceForm">
          <AFormItem label="开启维护模式">
            <ASwitch
              v-model:checked="maintenanceForm['maintenance.enabled']"
              checked-value="true" unchecked-value="false"
            />
            <span style="margin-left: 8px; color: #999;">
              {{ maintenanceForm['maintenance.enabled'] === 'true' ? '维护中' : '正常运行' }}
            </span>
          </AFormItem>
          <AFormItem label="维护提示语">
            <AInput.TextArea
              v-model:value="maintenanceForm['maintenance.message']"
              :rows="3"
              placeholder="用户看到的维护提示"
            />
          </AFormItem>
          <AFormItem>
            <AButton type="primary" :loading="saving" @click="saveCategory(maintenanceForm)">
              <SaveOutlined /> 保存维护设置
            </AButton>
          </AFormItem>
        </AForm>
      </ACard>

      <!-- 显示设置 -->
      <ACard title="显示设置" style="margin-bottom: 24px">
        <AForm layout="vertical" :model="displayForm">
          <AFormItem label="默认分页条数">
            <ASelect v-model:value="displayForm['display.page_size_default']" style="width: 200px">
              <ASelectOption value="10">10</ASelectOption>
              <ASelectOption value="20">20</ASelectOption>
              <ASelectOption value="50">50</ASelectOption>
              <ASelectOption value="100">100</ASelectOption>
            </ASelect>
          </AFormItem>
          <AFormItem label="时间格式">
            <ASelect v-model:value="displayForm['display.time_format']" style="width: 280px">
              <ASelectOption value="YYYY-MM-DD HH:mm:ss">YYYY-MM-DD HH:mm:ss</ASelectOption>
              <ASelectOption value="YYYY-MM-DD">YYYY-MM-DD</ASelectOption>
              <ASelectOption value="MM-DD HH:mm">MM-DD HH:mm</ASelectOption>
            </ASelect>
          </AFormItem>
          <AFormItem>
            <AButton type="primary" :loading="saving" @click="saveCategory(displayForm)">
              <SaveOutlined /> 保存显示设置
            </AButton>
          </AFormItem>
        </AForm>
      </ACard>

      <!-- 文件上传设置 -->
      <ACard title="文件上传设置">
        <AForm layout="vertical" :model="uploadForm">
          <AFormItem label="最大文件大小（MB）">
            <AInputNumber
              v-model:value="uploadForm['upload.max_file_size_mb']"
              :min="1" :max="500" style="width: 200px"
            />
          </AFormItem>
          <AFormItem label="允许的文件类型">
            <AInput
              v-model:value="uploadForm['upload.allowed_extensions']"
              placeholder="逗号分隔，如：jpg,png,gif,mp4"
            />
          </AFormItem>
          <AFormItem>
            <AButton type="primary" :loading="saving" @click="saveCategory(uploadForm)">
              <SaveOutlined /> 保存上传设置
            </AButton>
          </AFormItem>
        </AForm>
      </ACard>
    </ASpin>
  </div>
</template>

<style scoped>
.settings-page {
  padding: 24px;
}
</style>
