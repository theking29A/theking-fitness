<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'

const saving = ref(false)

const handleSave = () => {
  saving.value = true
  setTimeout(() => {
    saving.value = false
    message.success('保存成功（演示）')
  }, 800)
}
</script>

<template>
  <div>
    <h2 style="margin-bottom: 24px">系统设置</h2>
    <a-card title="基础设置">
      <a-form layout="vertical">
        <a-form-item label="系统名称">
          <a-input value="TheKing Admin" />
        </a-form-item>
        <a-form-item label="每页显示条数">
          <a-input-number :min="5" :max="100" :value="10" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="saving" @click="handleSave">保存设置</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>
