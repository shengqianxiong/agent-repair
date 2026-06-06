const TOKEN_KEY = 'login_admin_token'
const USER_KEY = 'login_admin_user'

/**
 * 获取管理员登录 Token
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/**
 * 保存登录凭证
 */
export function setAuth(token, user) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USER_KEY, JSON.stringify(user || {}))
}

/**
 * 获取当前登录管理员信息
 */
export function getUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

/**
 * 清除登录状态
 */
export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

/**
 * 是否已登录
 */
export function isLoggedIn() {
  return Boolean(getToken())
}
