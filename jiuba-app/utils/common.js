/** 页面跳转工具 */

const TAB_PAGES = [
  '/pages/home/index',
  '/pages/menu/index',
  '/pages/square/index',
  '/pages/my/index'
]

export function switchTab(url) {
  if (TAB_PAGES.includes(url)) {
    uni.switchTab({ url })
  } else {
    uni.navigateTo({ url })
  }
}

export function formatMoney(amount) {
  const num = Number(amount) || 0
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

export function formatDateTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

export function showToast(title, icon = 'none') {
  uni.showToast({ title, icon })
}

export function showLoading(title = '加载中...') {
  uni.showLoading({ title, mask: true })
}

export function hideLoading() {
  uni.hideLoading()
}

/** 表单基础校验 */
export function validatePhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

export function validateRequired(value, label) {
  if (!value || !String(value).trim()) {
    showToast(`请填写${label}`)
    return false
  }
  return true
}
