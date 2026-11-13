export const STATUS_OPTIONS = ['待处理', '处理中', '已完成', '已关闭']
export const LEVEL_OPTIONS = ['高', '中', '低']
export const ASSIGNEE_OPTIONS = ['李强', '王敏', '赵磊', '陈晨']
export const CURRENT_OPERATOR = '张伟'

export const STATUS_TAG_TYPE = {
  待处理: 'warning',
  处理中: 'primary',
  已完成: 'success',
  已关闭: 'info'
}

export const LEVEL_TAG_TYPE = {
  高: 'danger',
  中: 'warning',
  低: 'info'
}

export function formatText(value) {
  if (value === null || value === undefined) {
    return '—'
  }
  const text = String(value).trim()
  return text === '' ? '—' : text
}

export function formatLogTitle(log) {
  const content = log.content || '状态变更'
  if (log.from_status && log.to_status && log.from_status !== log.to_status) {
    return `${content}，状态：${log.from_status} → ${log.to_status}`
  }
  return content
}
