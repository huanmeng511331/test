<script setup>
import { computed, onMounted, ref } from 'vue'

import { getStatistics } from '../api/workOrder'
import { LEVEL_TAG_TYPE, STATUS_OPTIONS } from '../utils/display'

const loading = ref(false)
const errorMessage = ref('')
const statistics = ref({
  total: 0,
  by_status: {},
  by_fault_level: {}
})

const statusCards = computed(() =>
  STATUS_OPTIONS.map((status) => ({
    status,
    count: statistics.value.by_status?.[status] || 0
  }))
)
const levelRows = computed(() =>
  ['高', '中', '低'].map((level) => {
    const count = statistics.value.by_fault_level?.[level] || 0
    const total = statistics.value.total || 0
    return {
      level,
      count,
      percent: total > 0 ? Math.round((count / total) * 100) : 0
    }
  })
)
const levelTotal = computed(() =>
  levelRows.value.reduce((sum, row) => sum + row.count, 0)
)

async function load() {
  loading.value = true
  errorMessage.value = ''
  try {
    const { data } = await getStatistics()
    statistics.value = data
  } catch {
    errorMessage.value = '统计数据加载失败，请检查后端服务后重试。'
  } finally {
    loading.value = false
  }
}

onMounted(() => load())
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h2>工单统计</h2>
        <p>按全量工单实时聚合，不受列表筛选条件影响。</p>
      </div>
      <el-button :loading="loading" @click="load">刷新数据</el-button>
    </div>

    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      show-icon
      :closable="false"
      class="card"
    >
      <template #default>
        <el-button size="small" type="primary" @click="load">重新加载</el-button>
      </template>
    </el-alert>

    <div v-loading="loading">
      <div class="status-grid">
        <div
          v-for="card in statusCards"
          :key="card.status"
          class="status-card"
          :class="`status-${card.status}`"
        >
          <div class="status-name">{{ card.status }}</div>
          <div class="status-count">{{ card.count }}</div>
          <div class="status-caption">当前工单数</div>
        </div>
      </div>

      <el-card class="card" shadow="never">
        <template #header>
          <div class="chart-header">
            <span>按故障等级分布</span>
            <span class="muted">合计 {{ levelTotal }} 条 / 全量 {{ statistics.total }} 条</span>
          </div>
        </template>
        <div class="level-chart">
          <div v-for="row in levelRows" :key="row.level" class="level-row">
            <el-tag :type="LEVEL_TAG_TYPE[row.level]" effect="light" class="level-tag">
              {{ row.level }}
            </el-tag>
            <div class="level-track">
              <div
                v-if="row.count > 0"
                class="level-fill"
                :class="`fill-${row.level}`"
                :style="{ width: `${row.percent}%` }"
              />
            </div>
            <span class="level-count">{{ row.count }} 条</span>
            <span class="level-percent">{{ row.percent }}%</span>
          </div>
        </div>
        <el-empty
          v-if="statistics.total === 0"
          description="暂无工单数据，新建工单后统计将实时更新"
          :image-size="80"
        />
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.status-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.status-card {
  padding: 18px 20px;
  background: #fff;
  border-radius: 8px;
  border-left: 5px solid #dcdfe6;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.status-待处理 {
  border-left-color: #e6a23c;
}

.status-处理中 {
  border-left-color: #409eff;
}

.status-已完成 {
  border-left-color: #67c23a;
}

.status-已关闭 {
  border-left-color: #909399;
}

.status-name {
  font-size: 14px;
  color: #606266;
}

.status-count {
  margin: 8px 0 4px;
  font-size: 34px;
  font-weight: 600;
  line-height: 1;
}

.status-caption {
  font-size: 12px;
  color: #909399;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.level-chart {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 8px 4px 16px;
}

.level-row {
  display: grid;
  grid-template-columns: 56px 1fr 70px 56px;
  gap: 12px;
  align-items: center;
}

.level-tag {
  justify-self: start;
}

.level-track {
  height: 18px;
  overflow: hidden;
  background: #f0f2f5;
  border-radius: 9px;
}

.level-fill {
  height: 100%;
  min-width: 2px;
  border-radius: 9px;
  transition: width 0.3s ease;
}

.fill-高 {
  background: #f56c6c;
}

.fill-中 {
  background: #e6a23c;
}

.fill-低 {
  background: #909399;
}

.level-count,
.level-percent {
  font-size: 13px;
  color: #606266;
  text-align: right;
}

.level-percent {
  color: #909399;
}
</style>
