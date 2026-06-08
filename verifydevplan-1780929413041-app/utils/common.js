export function formatMoney(amount) {
  const num = Number(amount) || 0
  return num.toFixed(2)
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

export function validateRequired(value, label) {
  if (!value || !String(value).trim()) {
    showToast(`请填写${label}`)
    return false
  }
  return true
}
