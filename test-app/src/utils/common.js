/** 格式化金额 */
export function formatPrice(val) {
  if (val == null || val === '') return '0.00'
  return Number(val).toFixed(2)
}

/** 订单状态映射 */
export const ORDER_STATUS_MAP = {
  '待支付': { text: '待支付', type: 'warning' },
  '已支付': { text: '已支付', type: 'primary' },
  '制作中': { text: '制作中', type: 'info' },
  '已完成': { text: '已完成', type: 'success' },
  '已取消': { text: '已取消', type: 'error' }
}

/** 预约状态映射 */
export const BOOKING_STATUS_MAP = {
  '待确认': { text: '待确认', type: 'warning' },
  '已确认': { text: '已确认', type: 'primary' },
  '已到店': { text: '已到店', type: 'success' },
  '已取消': { text: '已取消', type: 'error' }
}

/** 页面跳转 */
export function navigateTo(url) {
  uni.navigateTo({ url })
}

export function switchTab(url) {
  uni.switchTab({ url })
}
