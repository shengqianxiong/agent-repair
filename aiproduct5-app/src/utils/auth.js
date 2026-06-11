const TOKEN_KEY = 'aiproduct5_app_token'
const VISITOR_KEY = 'aiproduct5_app_visitor'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token || '')
}

export function getVisitorId() {
  return uni.getStorageSync(VISITOR_KEY) || ''
}

export function ensureVisitorId() {
  const current = getVisitorId()
  if (current) return current
  const visitorId = `visitor_${Date.now()}_${Math.random().toString(16).slice(2, 8)}`
  uni.setStorageSync(VISITOR_KEY, visitorId)
  return visitorId
}
