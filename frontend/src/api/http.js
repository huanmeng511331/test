import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8000/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

export function extractErrorMessage(error) {
  const detail = error?.response?.data?.detail
  if (Array.isArray(detail)) {
    return detail.map((item) => item.msg).filter(Boolean).join('；') || '请求参数校验失败'
  }
  if (detail && typeof detail === 'object') {
    return detail.message || '请求处理失败'
  }
  if (typeof detail === 'string' && detail) {
    return detail
  }
  if (error?.code === 'ECONNABORTED') {
    return '请求超时，请稍后重试'
  }
  if (!error?.response) {
    return '无法连接后端服务，请确认已在 backend 目录启动 uvicorn（http://localhost:8000）'
  }
  return '请求失败，请稍后重试'
}

export function extractFieldErrors(error) {
  const detail = error?.response?.data?.detail
  if (!Array.isArray(detail)) {
    return {}
  }
  return detail.reduce((result, item) => {
    const field = Array.isArray(item.loc) ? item.loc[item.loc.length - 1] : null
    if (field && item.msg) {
      result[field] = item.msg
    }
    return result
  }, {})
}

http.interceptors.response.use(
  (response) => response,
  (error) => {
    ElMessage.error({ message: extractErrorMessage(error), grouping: true })
    return Promise.reject(error)
  }
)

export default http
