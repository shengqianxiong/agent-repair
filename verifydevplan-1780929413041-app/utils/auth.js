const TOKEN_KEY = 'verifydevplan_token'
const USER_KEY = 'verifydevplan_user'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function removeToken() {
  uni.removeStorageSync(TOKEN_KEY)
}

export function getUser() {
  const raw = uni.getStorageSync(USER_KEY)
  return raw ? JSON.parse(raw) : null
}

export function setUser(user) {
  uni.setStorageSync(USER_KEY, JSON.stringify(user))
}

export function removeUser() {
  uni.removeStorageSync(USER_KEY)
}

export function clearAuth() {
  removeToken()
  removeUser()
}

export function isLoggedIn() {
  return !!getToken()
}

const LOGIN_PATH = '/pages/login/login'

/** 清除登录态并跳转登录页 */
export function redirectToLogin() {
  clearAuth()
  uni.reLaunch({ url: LOGIN_PATH })
}

/** 页面级鉴权守卫，未登录则跳转登录 */
export function requireAuth() {
  if (!isLoggedIn()) {
    redirectToLogin()
    return false
  }
  return true
}
