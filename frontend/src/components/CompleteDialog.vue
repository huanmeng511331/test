<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

import { updateWorkOrderStatus } from '../api/workOrder'
import { CURRENT_OPERATOR } from '../utils/display'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  order: { type: Object, default: null },
  initialNote: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue', 'updated'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref(null)
const submitting = ref(false)
const form = reactive({
  solution: '',
  note: ''
})

const rules = {
  solution: [
    { required: true, message: '请填写处理方案', trigger: 'blur' },
    { max: 500, message: '处理方案不能超过 500 字', trigger: 'blur' }
  ]
}

watch(
  () => props.modelValue,
  (opened) => {
    if (opened) {
      form.solution = ''
      form.note = props.initialNote || ''
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
      status: '已完成',
      solution: form.solution.trim(),
      operator: CURRENT_OPERATOR
    }
    if (form.note.trim()) {
      payload.note = form.note.trim()
    }
    await updateWorkOrderStatus(props.order.id, payload)
    ElMessage.success('工单已完成')
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
    title="填写处理方案并完成"
    width="540px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-alert
      title="完成后工单状态将变为“已完成”，处理方案不可再修改。"
      type="info"
      :closable="false"
      show-icon
      class="dialog-alert"
    />
    <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
      <el-form-item label="处理方案" prop="solution">
        <el-input
          v-model="form.solution"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          placeholder="请填写故障原因、处理措施与复检结果"
        />
      </el-form-item>
      <el-form-item label="处理说明">
        <el-input
          v-model="form.note"
          type="textarea"
          :rows="3"
          maxlength="500"
          show-word-limit
          placeholder="选填，将追加一条维修处理记录"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit">确认完成</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.dialog-alert {
  margin-bottom: 18px;
}
</style>
