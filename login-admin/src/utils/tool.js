/**
 * 侧边栏菜单配置
 */
export const menuList = [
  {
    title: '账号管理',
    path: '/account/list',
    icon: 'User'
  }
]

/**
 * 账号状态：1-正常，0-禁用
 */
export const accountStatusMap = {
  1: { label: '正常', type: 'success' },
  0: { label: '禁用', type: 'danger' }
}

/**
 * 格式化日期时间
 */
export function formatDateTime(val) {
  if (!val) return '-'
  const date = new Date(val)
  if (Number.isNaN(date.getTime())) return val
  const pad = (num) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}
