const TAB_PAGES = [
  '/pages/index/index',
  '/pages/order/index',
  '/pages/rank/index',
  '/pages/profile/index'
]

export function goPage(url) {
  if (!url) return
  if (TAB_PAGES.includes(url)) {
    uni.switchTab({ url })
  } else {
    uni.navigateTo({ url })
  }
}

export function formatPrice(price) {
  if (price === null || price === undefined) return '0.00'
  return Number(price).toFixed(2)
}

export function formatDateTime(val) {
  if (!val) return ''
  const d = new Date(val)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
