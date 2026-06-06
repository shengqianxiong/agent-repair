import config from '@/config/index.js'

const TOKEN_KEY = 'login_token'
const USER_KEY = 'login_user'
const EXPIRE_KEY = 'login_expire'

/**
 * 获取本地存储的 Token。
 */
export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

/**
 * 保存登录 Token 及过期时间。
 */
export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
  const expireAt = Date.now() + config.tokenExpireHours * 60 * 60 * 1000
  uni.setStorageSync(EXPIRE_KEY, expireAt)
}

/**
 * 获取当前登录用户信息。
 */
export function getUserInfo() {
  return uni.getStorageSync(USER_KEY) || null
}

/**
 * 保存登录用户信息。
 */
export function setUserInfo(userInfo) {
  uni.setStorageSync(USER_KEY, userInfo)
}

/**
 * 判断 Token 是否在有效期内。
 */
export function isLoggedIn() {
  const token = getToken()
  const expireAt = uni.getStorageSync(EXPIRE_KEY)
  if (!token || !expireAt) {
    return false
  }
  return Date.now() < expireAt
}

/**
 * 清除登录态。
 */
export function clearAuth() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(USER_KEY)
  uni.removeStorageSync(EXPIRE_KEY)
}
