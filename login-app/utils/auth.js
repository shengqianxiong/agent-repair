const TOKEN_KEY = 'login_token'
const USERNAME_KEY = 'login_username'
const TOKEN_EXPIRE_KEY = 'login_token_expire'

/**
 * 获取本地存储的登录 Token
 */
export function getToken() {
  const token = uni.getStorageSync(TOKEN_KEY) || ''
  if (!token) {
    return ''
  }
  const expireAt = uni.getStorageSync(TOKEN_EXPIRE_KEY)
  if (expireAt && Date.now() > Number(expireAt)) {
    clearAuth()
    return ''
  }
  return token
}

/**
 * 保存登录 Token 及过期时间
 */
export function setToken(token, expireHours = 2) {
  uni.setStorageSync(TOKEN_KEY, token)
  const expireAt = Date.now() + expireHours * 60 * 60 * 1000
  uni.setStorageSync(TOKEN_EXPIRE_KEY, String(expireAt))
}

/**
 * 保存当前登录账号
 */
export function setUsername(username) {
  uni.setStorageSync(USERNAME_KEY, username)
}

/**
 * 获取当前登录账号
 */
export function getUsername() {
  return uni.getStorageSync(USERNAME_KEY) || ''
}

/**
 * 清除登录态
 */
export function clearAuth() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(USERNAME_KEY)
  uni.removeStorageSync(TOKEN_EXPIRE_KEY)
}

/**
 * 是否已登录（Token 存在且未过期）
 */
export function isLoggedIn() {
  return !!getToken()
}
