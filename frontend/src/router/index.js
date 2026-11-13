import { createRouter, createWebHistory } from 'vue-router'

import WorkOrderList from '../views/WorkOrderList.vue'
import WorkOrderDetail from '../views/WorkOrderDetail.vue'
import Statistics from '../views/Statistics.vue'

const routes = [
  { path: '/', redirect: '/work-orders' },
  {
    path: '/work-orders',
    name: 'work-order-list',
    component: WorkOrderList,
    meta: { title: '工单列表' }
  },
  {
    path: '/work-orders/:id',
    name: 'work-order-detail',
    component: WorkOrderDetail,
    meta: { title: '工单详情' }
  },
  {
    path: '/statistics',
    name: 'statistics',
    component: Statistics,
    meta: { title: '工单统计' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  document.title = to.meta.title
    ? `${to.meta.title} - 设备故障智能工单助手`
    : '设备故障智能工单助手'
})

export default router
