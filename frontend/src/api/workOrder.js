import http from './http'

export function listWorkOrders(params) {
  return http.get('/work-orders', { params })
}

export function createWorkOrder(payload) {
  return http.post('/work-orders', payload)
}

export function getWorkOrder(id) {
  return http.get(`/work-orders/${id}`)
}

export function updateWorkOrderStatus(id, payload) {
  return http.patch(`/work-orders/${id}/status`, payload)
}

export function getStatistics() {
  return http.get('/work-orders/statistics')
}
