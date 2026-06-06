export function formatDateTime(value) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

export function formatMoney(value) {
  if (value === null || value === undefined || value === '') return '0.00'
  return Number(value).toFixed(2)
}

export const ORDER_STATUS_MAP = {
  待支付: 'warning',
  已支付: 'primary',
  制作中: '',
  已完成: 'success',
  已取消: 'info',
}

export const BOOKING_STATUS_MAP = {
  待确认: 'warning',
  已确认: 'primary',
  已到店: 'success',
  已取消: 'info',
}

export const TABLE_STATUS_MAP = {
  空闲: 'success',
  占用: 'danger',
  预约中: 'warning',
}

/**
 * 侧边栏菜单配置
 */
export const menuList = [
  {
    title: '数据看板',
    path: '/dashboard',
    icon: 'DataAnalysis',
  },
  {
    title: '商品管理',
    icon: 'Goods',
    children: [
      { title: '酒水商品', path: '/product/list' },
      { title: '套餐管理', path: '/package/list' },
    ],
  },
  {
    title: '订单管理',
    path: '/order/list',
    icon: 'List',
  },
  {
    title: '预约管理',
    path: '/booking/list',
    icon: 'Calendar',
  },
  {
    title: '活动管理',
    path: '/activity/list',
    icon: 'Present',
  },
  {
    title: '会员管理',
    path: '/user/list',
    icon: 'User',
  },
  {
    title: '营销工具',
    icon: 'Ticket',
    children: [
      { title: '团购券', path: '/coupon/list' },
      { title: '通知管理', path: '/notification/list' },
    ],
  },
  {
    title: '互动运营',
    icon: 'Trophy',
    children: [
      { title: '互动游戏', path: '/game/list' },
      { title: '排行榜配置', path: '/rank/config' },
    ],
  },
  {
    title: '基础设置',
    icon: 'Setting',
    children: [
      { title: '桌位管理', path: '/table/list' },
    ],
  },
]

export function findMenuTitle(path) {
  for (const item of menuList) {
    if (item.path === path) return item.title
    if (item.children) {
      const child = item.children.find((c) => c.path === path)
      if (child) return child.title
    }
  }
  return '云享生活管理后台'
}
