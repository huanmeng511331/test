<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

import { updateWorkOrderStatus } from '../api/workOrder'
import { ASSIGNEE_OPTIONS, CURRENT_OPERATOR, formatText } from '../utils/display'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  order: { type: Object, default: null }
})
const emit = defineEmits(['update:modelValue', 'updated'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref(null)
const submitting = ref(false)
const form = reactive({
  assignee: '',
  immediateStart: true,
  note: ''
})

const alreadyProcessing = computed(() => props.order?.status === '处理中')

const rules = {
  assignee: [{ required: true, message: '请选择维修人员', trigger: 'change' }]
}

watch(
  () => props.modelValue,
  (opened) => {
    if (opened) {
      form.assignee = ''
      form.immediateStart = !alreadyProcessing.value
      form.note = ''
      formRef.value?.clearValidate()
    }
  }
)

async function submit() {
  if (submitting.value) {
    return
  }
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    const payload = {
      assignee: form.assignee,
      operator: CURRENT_OPERATOR
    }
    if (form.immediateStart && !alreadyProcessing.value) {
      payload.status = '处理中'
    }
    if (form.note.trim()) {
      payload.note = form.note.trim()
    }
    await updateWorkOrderStatus(props.order.id, payload)
    ElMessage.success(form.immediateStart && !alreadyProcessing.value ? '分派并开始处理成功' : '分派成功')
    visible.value = false
    emit('updated')
  } catch {
    // 错误提示由 API 拦截器统一展示，弹窗保持打开以便修正。
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <el-dialog
    v-model="visible"
    title="分派工单"
    width="520px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <div v-if="order" class="order-brief">
      <span class="brief-label">工单编号</span>
      <strong>{{ order.order_no }}</strong>
      <span class="brief-label">设备名称</span>
      <span>{{ formatText(order.device_name) }}</span>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="104px">
      <el-form-item label="维修人员" prop="assignee">
        <el-select v-model="form.assignee" placeholder="请选择维修人员" style="width: 100%">
          <el-option v-for="name in ASSIGNEE_OPTIONS" :key="name" :label="name" :value="name" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="!alreadyProcessing" label="立即开始处理">
        <div class="switch-row">
          <el-switch v-model="form.immediateStart" />
          <span class="switch-tip">
            {{ form.immediateStart ? '分派后工单状态变为“处理中”' : '仅记录维修人员，状态保持“待处理”' }}
          </span>
        </div>
      </el-form-item>
      <el-form-item v-else label="说明">
        <span class="switch-tip">工单已在处理中，本次分派仅更换维修人员并追加记录。</span>
      </el-form-item>
      <el-form-item label="处理说明">
        <el-input
          v-model="form.note"
          type="textarea"
          :rows="3"
          maxlength="500"
          show-word-limit
          placeholder="选填，用于交接说明（不超过 500 字）"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.order-brief {
  display: grid;
  grid-template-columns: auto 1fr auto 1fr;
  gap: 8px 12px;
  padding: 12px 14px;
  margin-bottom: 18px;
  font-size: 13px;
  background: #f5f7fa;
  border-radius: 6px;
}

.brief-label {
  color: #909399;
}

.switch-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.switch-tip {
  font-size: 12px;
  line-height: 1.5;
  color: #909399;
}
</style>
