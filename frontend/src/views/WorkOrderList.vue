<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { listWorkOrders } from '../api/workOrder'
import { LEVEL_OPTIONS, LEVEL_TAG_TYPE, STATUS_OPTIONS, STATUS_TAG_TYPE, formatText } from '../utils/display'
import CreateWorkOrderDialog from '../components/CreateWorkOrderDialog.vue'

const router = useRouter()
const loading = ref(false)
const items = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const createVisible = ref(false)
const filters = reactive({
  status: '',
  fault_level: ''
})

async function load(resetPage = false) {
  if (resetPage) {
    page.value = 1
  }
  loading.value = true
  try {
    const params = { page: page.value, page_size: pageSize.value }
    if (filters.status) {
      params.status = filters.status
    }
    if (filters.fault_level) {
      params.fault_level = filters.fault_level
    }
    const { data } = await listWorkOrders(params)
    items.value = data.items
    total.value = data.total
  } catch {
    items.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function query() {
  load(true)
}

function resetFilters() {
  filters.status = ''
  filters.fault_level = ''
  load(true)
}

function handlePageChange(nextPage) {
  page.value = nextPage
  load()
}

function handleSizeChange(nextSize) {
  pageSize.value = nextSize
  page.value = 1
  load()
}

function openDetail(row) {
  router.push(`/work-orders/${row.id}`)
}

function handleCreated(order) {
  router.push(`/work-orders/${order.id}`)
}

onMounted(() => load())
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h2>工单列表</h2>
        <p>集中查看设备故障工单的处理状态，支持按状态与故障等级筛选。</p>
      </div>
      <el-button type="primary" @click="createVisible = true">新建工单</el-button>
    </div>

    <el-card class="card" shadow="never">
      <el-form :inline="true" class="filter-form" @submit.prevent="query">
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option v-for="status in STATUS_OPTIONS" :key="status" :label="status" :value="status" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障等级">
          <el-select v-model="filters.fault_level" placeholder="全部" clearable style="width: 130px">
            <el-option label="全部" value="" />
            <el-option v-for="level in LEVEL_OPTIONS" :key="level" :label="level" :value="level" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="card" shadow="never">
      <el-table v-loading="loading" :data="items" empty-text="暂无数据" row-key="id">
        <el-table-column prop="order_no" label="工单编号" width="160" />
        <el-table-column prop="device_name" label="设备名称" min-width="150" show-overflow-tooltip />
        <el-table-column label="故障等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="LEVEL_TAG_TYPE[row.fault_level]" effect="light">{{ row.fault_level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="当前状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="STATUS_TAG_TYPE[row.status]" effect="light">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="维修人员" width="110">
          <template #default="{ row }">{{ formatText(row.assignee) }}</template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="170" />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > 0" class="pager">
        <span class="pager-total">共 {{ total }} 条</span>
        <el-pagination
          :current-page="page"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="sizes, prev, pager, next"
          :total="total"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <CreateWorkOrderDialog v-model="createVisible" @created="handleCreated" />
  </div>
</template>

<style scoped>
.filter-form {
  margin-bottom: -18px;
}

.pager {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
}

.pager-total {
  font-size: 13px;
  color: #606266;
}
</style>
