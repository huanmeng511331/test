<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

import { createWorkOrder } from '../api/workOrder'
import { extractFieldErrors } from '../api/http'
import { CURRENT_OPERATOR, LEVEL_OPTIONS } from '../utils/display'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})
const emit = defineEmits(['update:modelValue', 'created'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref(null)
const submitting = ref(false)
const serverErrors = reactive({})

const createEmptyForm = () => ({
  device_name: '',
  device_code: '',
  fault_level: '',
  fault_description: '',
  reporter: CURRENT_OPERATOR
})

const form = reactive(createEmptyForm())

const rules = {
  device_name: [
    { required: true, message: '请输入设备名称', trigger: 'blur' },
    { max: 50, message: '设备名称不能超过 50 字', trigger: 'blur' }
  ],
  device_code: [
    { required: true, message: '请输入设备编号', trigger: 'blur' },
    { max: 32, message: '设备编号不能超过 32 字', trigger: 'blur' },
    {
      pattern: /^[A-Za-z0-9_-]+$/,
      message: '设备编号仅允许字母、数字、- 和 _',
      trigger: 'blur'
    }
  ],
  fault_level: [{ required: true, message: '请选择故障等级', trigger: 'change' }],
  fault_description: [
    { required: true, message: '请输入故障现象', trigger: 'blur' },
    { max: 500, message: '故障现象不能超过 500 字', trigger: 'blur' }
  ],
  reporter: [
    { required: true, message: '请输入报修人', trigger: 'blur' },
    { max: 20, message: '报修人不能超过 20 字', trigger: 'blur' }
  ]
}

function clearServerError(field) {
  delete serverErrors[field]
}

function resetForm() {
  Object.assign(form, createEmptyForm())
  Object.keys(serverErrors).forEach((key) => delete serverErrors[key])
  formRef.value?.clearValidate()
}

watch(
  () => props.modelValue,
  (opened) => {
    if (opened) {
      resetForm()
    }
  }
)

function close() {
  visible.value = false
}

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
    const { data } = await createWorkOrder({
      ...form,
      operator: CURRENT_OPERATOR
    })
    ElMessage.success('创建成功')
    visible.value = false
    emit('created', data)
  } catch (error) {
    Object.assign(serverErrors, extractFieldErrors(error))
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <el-dialog
    v-model="visible"
    title="新建工单"
    width="560px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
      <el-form-item label="设备名称" prop="device_name" :error="serverErrors.device_name">
        <el-input
          v-model="form.device_name"
          maxlength="50"
          placeholder="例：压缩机 C-01"
          @input="clearServerError('device_name')"
        />
      </el-form-item>
      <el-form-item label="设备编号" prop="device_code" :error="serverErrors.device_code">
        <el-input
          v-model="form.device_code"
          maxlength="32"
          placeholder="例：EQ-C-01"
          @input="clearServerError('device_code')"
        />
      </el-form-item>
      <el-form-item label="故障等级" prop="fault_level" :error="serverErrors.fault_level">
        <el-radio-group v-model="form.fault_level" @change="clearServerError('fault_level')">
          <el-radio-button v-for="level in LEVEL_OPTIONS" :key="level" :value="level">
            {{ level }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="故障现象" prop="fault_description" :error="serverErrors.fault_description">
        <el-input
          v-model="form.fault_description"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          placeholder="请描述故障表现、发生时机等信息"
          @input="clearServerError('fault_description')"
        />
      </el-form-item>
      <el-form-item label="报修人" prop="reporter" :error="serverErrors.reporter">
        <el-input
          v-model="form.reporter"
          maxlength="20"
          placeholder="请输入报修人"
          @input="clearServerError('reporter')"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <span class="footer-tip">创建成功后将跳转到工单详情页</span>
        <div>
          <el-button @click="close">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer-tip {
  font-size: 12px;
  color: #909399;
}
</style>
