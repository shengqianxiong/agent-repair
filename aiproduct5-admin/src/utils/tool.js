import {
  DataAnalysis,
  Document,
  MagicStick,
  Odometer,
  Stamp
} from '@element-plus/icons-vue'

/** 侧边栏菜单配置 */
export const menuList = [
  {
    path: '/seller/dashboard',
    title: '工作台',
    icon: Odometer
  },
  {
    path: '/seller/activity/list',
    title: '活动管理',
    icon: Document
  },
  {
    path: '/seller/verify/list',
    title: '核销管理',
    icon: Stamp
  },
  {
    path: '/seller/ai/history',
    title: 'AI 生成记录',
    icon: MagicStick
  }
]

/** 活动状态 */
export const activityStatusMap = {
  0: { label: '草稿', type: 'info' },
  1: { label: '进行中', type: 'success' },
  2: { label: '已下架', type: 'warning' },
  3: { label: '已过期', type: 'danger' }
}

/** 审核状态 */
export const verifyStatusMap = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已拒绝', type: 'danger' }
}

/** AI 生成状态 */
export const aiStatusMap = {
  0: { label: '生成中', type: 'warning' },
  1: { label: '成功', type: 'success' },
  2: { label: '失败', type: 'danger' }
}

export function formatMoney(value) {
  const num = Number(value)
  if (Number.isNaN(num)) return '0.00'
  return num.toFixed(2)
}

export function formatDateTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

export function downloadUrl(url, filename) {
  const link = document.createElement('a')
  link.href = url
  link.download = filename || 'download'
  link.target = '_blank'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

export { DataAnalysis }
