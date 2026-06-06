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

/** 账号状态：1-正常，0-禁用 */
export const ACCOUNT_STATUS = {
  NORMAL: 1,
  DISABLED: 0
}

export const accountStatusOptions = [
  { label: '正常', value: ACCOUNT_STATUS.NORMAL },
  { label: '禁用', value: ACCOUNT_STATUS.DISABLED }
]

export function formatDateTime(value) {
  if (!value) {
    return '-'
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  const pad = (num) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

/**
 * 将后端状态值转为展示文案
 */
export function formatAccountStatus(status) {
  return status === ACCOUNT_STATUS.NORMAL ? '正常' : '禁用'
}

/**
 * 状态标签颜色
 */
export function accountStatusTagType(status) {
  return status === ACCOUNT_STATUS.NORMAL ? 'success' : 'danger'
}
