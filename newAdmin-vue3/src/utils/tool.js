/**
 * 侧边栏菜单配置
 */
export const menuList = [
  {
    title: '数据看板',
    path: '/dashboard',
    icon: 'DataAnalysis'
  },
  {
    title: '商品管理',
    icon: 'Goods',
    children: [
      { title: '酒水商品', path: '/product/list' },
      { title: '套餐管理', path: '/package/list' }
    ]
  },
  {
    title: '订单管理',
    path: '/order/list',
    icon: 'List'
  },
  {
    title: '预约管理',
    path: '/booking/list',
    icon: 'Calendar'
  },
  {
    title: '活动管理',
    path: '/activity/list',
    icon: 'Present'
  },
  {
    title: '会员管理',
    path: '/user/list',
    icon: 'User'
  },
  {
    title: '营销工具',
    icon: 'Ticket',
    children: [
      { title: '团购券', path: '/coupon/list' },
      { title: '通知管理', path: '/notification/list' }
    ]
  },
  {
    title: '互动运营',
    icon: 'Trophy',
    children: [
      { title: '互动游戏', path: '/game/list' },
      { title: '排行榜配置', path: '/rank/config' }
    ]
  },
  {
    title: '基础设置',
    icon: 'Setting',
    children: [
      { title: '桌位管理', path: '/table/list' }
    ]
  }
]

export function formatMoney(val) {
  if (val === null || val === undefined || val === '') return '0.00'
  return Number(val).toFixed(2)
}

export function formatDateTime(val) {
  if (!val) return '-'
  const d = new Date(val)
  if (Number.isNaN(d.getTime())) return val
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

export const orderStatusMap = {
  待支付: 'warning',
  已支付: 'primary',
  制作中: '',
  已完成: 'success',
  已取消: 'info'
}

export const bookingStatusMap = {
  待确认: 'warning',
  已确认: 'primary',
  已到店: 'success',
  已取消: 'info'
}

export const tableStatusMap = {
  空闲: 'success',
  占用: 'danger',
  预约中: 'warning'
}
