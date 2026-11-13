<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { getWorkOrder, updateWorkOrderStatus } from '../api/workOrder'
import {
  CURRENT_OPERATOR,
  LEVEL_TAG_TYPE,
  STATUS_TAG_TYPE,
  formatLogTitle,
  formatText
} from '../utils/display'
import AssignDialog from '../components/AssignDialog.vue'
import CompleteDialog from '../components/CompleteDialog.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const actionLoading = ref(false)
const notFound = ref(false)
const errorMessage = ref('')
const order = ref(null)
const handlingNote = ref('')
const assignVisible = ref(false)
const completeVisible = ref(false)

const statusLogs = computed(() =>
  (order.value?.logs || []).filter((log) => log.log_type === 'status')
)
const handlingLogs = computed(() =>
  (order.value?.logs || []).filter((log) => log.log_type === 'handling')
)
const canAssign = computed(() => ['待处理', '处理中'].includes(order.value?.status))
const canStart = computed(() => order.value?.status === '待处理')
const canComplete = computed(() => order.value?.status === '处理中')
const canClose = computed(() => order.value?.status === '已完成')
const isClosed = computed(() => order.value?.status === '已关闭')

async function load() {
  loading.value = true
  notFound.value = false
  errorMessage.value = ''
  try {
    const { data } = await getWorkOrder(route.params.id)
    order.value = data
  } catch (error) {
    order.value = null
    notFound.value = error?.response?.status === 404
    if (!notFound.value) {
      errorMessage.value = '工单加载失败，请检查后端服务后重试。'
    }
  } finally {
    loading.value = false
  }
}

async function startProcessing() {
  actionLoading.value = true
  try {
    await updateWorkOrderStatus(order.value.id, {
      status: '处理中',
      operator: CURRENT_OPERATOR
    })
    ElMessage.success('已开始处理')
    await load()
  } catch {
    // 错误提示由 API 拦截器统一展示。
  } finally {
    actionLoading.value = false
  }
}

async function saveNote() {
  const note = handlingNote.value.trim()
  if (!note) {
    ElMessage.warning('请先填写处理说明')
    return
  }
  actionLoading.value = true
  try {
    await updateWorkOrderStatus(order.value.id, {
      note,
      operator: CURRENT_OPERATOR
    })
    ElMessage.success('处理说明已保存')
    handlingNote.value = ''
    await load()
  } catch {
    // 错误提示由 API 拦截器统一展示。
  } finally {
    actionLoading.value = false
  }
}

async function closeOrder() {
  try {
    await ElMessageBox.confirm('关闭后工单不可再变更状态，是否确认关闭？', '关闭工单', {
      type: 'warning',
      confirmButtonText: '确认关闭',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }
  actionLoading.value = true
  try {
    await updateWorkOrderStatus(order.value.id, {
      status: '已关闭',
      operator: CURRENT_OPERATOR
    })
    ElMessage.success('工单已关闭')
    await load()
  } catch {
    // 错误提示由 API 拦截器统一展示。
  } finally {
    actionLoading.value = false
  }
}

async function handleUpdated() {
  handlingNote.value = ''
  await load()
}

function backToList() {
  router.push('/work-orders')
}

watch(() => route.params.id, () => load())
onMounted(() => load())
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <el-button link type="primary" class="back-link" @click="backToList">← 返回列表</el-button>
        <h2>工单详情</h2>
        <p>查看工单信息与处理历史，并按状态规则推进工单。</p>
      </div>
    </div>

    <el-result
      v-if="notFound"
      icon="warning"
      title="工单不存在"
      sub-title="该工单可能已被删除，请返回列表查看。"
    >
      <template #extra>
        <el-button type="primary" @click="backToList">返回列表</el-button>
      </template>
    </el-result>

    <el-result
      v-else-if="errorMessage"
      icon="error"
      title="工单加载失败"
      :sub-title="errorMessage"
    >
      <template #extra>
        <el-button type="primary" @click="load">重新加载</el-button>
        <el-button @click="backToList">返回列表</el-button>
      </template>
    </el-result>

    <div v-else v-loading="loading">
      <template v-if="order">
        <el-card class="card" shadow="never">
          <div class="detail-head">
            <div class="head-main">
              <span class="order-no">{{ order.order_no }}</span>
              <el-tag :type="LEVEL_TAG_TYPE[order.fault_level]" effect="light">
                {{ order.fault_level }}
              </el-tag>
              <el-tag :type="STATUS_TAG_TYPE[order.status]" effect="light">
                {{ order.status }}
              </el-tag>
            </div>
            <span class="muted">创建于 {{ order.created_at }}</span>
          </div>
          <el-descriptions :column="3" border size="default">
            <el-descriptions-item label="设备名称">{{ order.device_name }}</el-descriptions-item>
            <el-descriptions-item label="设备编号">{{ order.device_code }}</el-descriptions-item>
            <el-descriptions-item label="故障等级">{{ order.fault_level }}</el-descriptions-item>
            <el-descriptions-item label="当前状态">{{ order.status }}</el-descriptions-item>
            <el-descriptions-item label="报修人">{{ order.reporter }}</el-descriptions-item>
            <el-descriptions-item label="维修人员">{{ formatText(order.assignee) }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ order.created_at }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ order.updated_at }}</el-descriptions-item>
            <el-descriptions-item label="工单编号">{{ order.order_no }}</el-descriptions-item>
          </el-descriptions>
          <div class="fault-block">
            <div class="block-label">故障现象</div>
            <div class="fault-text">{{ order.fault_description }}</div>
          </div>
          <div v-if="order.solution" class="solution-block">
            <div class="block-label">处理方案</div>
            <div class="solution-text">{{ order.solution }}</div>
          </div>
        </el-card>

        <el-row :gutter="16" class="card">
          <el-col :span="12">
            <el-card shadow="never" class="record-card">
              <template #header>
                <div class="record-header">
                  <span>状态流转记录</span>
                  <span class="muted">{{ statusLogs.length }} 条</span>
                </div>
              </template>
              <el-timeline v-if="statusLogs.length">
                <el-timeline-item
                  v-for="log in statusLogs"
                  :key="log.id"
                  :timestamp="log.created_at"
                  placement="top"
                  color="#409eff"
                >
                  <div class="log-title">{{ formatLogTitle(log) }}</div>
                  <div class="log-meta">
                    操作人：{{ log.operator }}
                    <span v-if="log.assignee"> · 维修人员：{{ log.assignee }}</span>
                  </div>
                </el-timeline-item>
              </el-timeline>
              <el-empty v-else description="暂无处理记录" :image-size="60" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="record-card">
              <template #header>
                <div class="record-header">
                  <span>维修处理记录</span>
                  <span class="muted">{{ handlingLogs.length }} 条</span>
                </div>
              </template>
              <div v-if="handlingLogs.length" class="handling-list">
                <div v-for="log in handlingLogs" :key="log.id" class="handling-item">
                  <div class="handling-content">{{ log.content }}</div>
                  <div class="log-meta">
                    {{ log.operator }}
                    <span v-if="log.assignee"> · {{ log.assignee }}</span>
                    · {{ log.created_at }}
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无处理记录" :image-size="60" />
            </el-card>
          </el-col>
        </el-row>

        <el-card class="card" shadow="never">
          <template #header>
            <span>工单操作</span>
          </template>
          <el-alert
            v-if="isClosed"
            title="工单已关闭，仅供查看，不可再变更状态"
            type="info"
            :closable="false"
            show-icon
          />
          <div v-else class="action-area">
            <div class="action-note">
              <div v-if="canComplete" class="note-editor">
                <el-input
                  v-model="handlingNote"
                  type="textarea"
                  :rows="2"
                  maxlength="500"
                  show-word-limit
                  placeholder="填写本次处理说明（选填），保存后追加一条维修处理记录"
                />
                <el-button :loading="actionLoading" @click="saveNote">保存处理说明</el-button>
              </div>
              <span v-else class="muted">
                {{ canStart ? '完成分派后可开始处理；分派时可选择立即开始。' : '工单已处理完成，可关闭工单。' }}
              </span>
            </div>
            <div class="action-buttons">
              <el-button v-if="canAssign" type="primary" plain @click="assignVisible = true">
                分派
              </el-button>
              <el-tooltip
                v-if="canStart"
                :disabled="!!order.assignee"
                content="请先分派维修人员"
                placement="top"
              >
                <span>
                  <el-button
                    :disabled="!order.assignee"
                    :loading="actionLoading"
                    @click="startProcessing"
                  >
                    开始处理
                  </el-button>
                </span>
              </el-tooltip>
              <el-button v-if="canComplete" type="primary" @click="completeVisible = true">
                填写处理方案并完成
              </el-button>
              <el-button v-if="canClose" type="primary" :loading="actionLoading" @click="closeOrder">
                关闭工单
              </el-button>
            </div>
          </div>
        </el-card>
      </template>
    </div>

    <AssignDialog v-model="assignVisible" :order="order" @updated="handleUpdated" />
    <CompleteDialog
      v-model="completeVisible"
      :order="order"
      :initial-note="handlingNote"
      @updated="handleUpdated"
    />
  </div>
</template>

<style scoped>
.back-link {
  padding: 0;
  margin-bottom: 6px;
}

.detail-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.head-main {
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-no {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.fault-block,
.solution-block {
  padding: 14px 16px;
  margin-top: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.solution-block {
  background: #f0f9eb;
}

.block-label {
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #606266;
}

.fault-text,
.solution-text {
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.record-card {
  height: 100%;
  min-height: 280px;
}

.record-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.log-title {
  font-size: 14px;
  line-height: 1.6;
}

.log-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.handling-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.handling-item {
  padding: 12px 14px;
  background: #f5f7fa;
  border-left: 3px solid #409eff;
  border-radius: 4px;
}

.handling-content {
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.action-area {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
  justify-content: space-between;
}

.action-note {
  flex: 1 1 420px;
}

.note-editor {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.note-editor :deep(.el-textarea) {
  flex: 1;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
