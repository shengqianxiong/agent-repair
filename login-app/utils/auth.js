/** 会话 Token 存储键 */
const TOKEN_KEY = 'login_token'
/** Token 过期时间戳存储键 */
const TOKEN_EXPIRE_KEY = 'login_token_expire'
/** 登录用户信息存储键 */
const USER_INFO_KEY = 'login_user_info'
/** 会话有效期：2 小时（与 PRD 一致） */
const TOKEN_TTL_MS = 2 * 60 * 60 * 1000

/**
 * 获取有效 Token，过期则自动清除
 * @returns {string} 有效 Token 或空字符串
 */
export function getToken() {
  const token = uni.getStorageSync(TOKEN_KEY) || ''
  const expireAt = uni.getStorageSync(TOKEN_EXPIRE_KEY) || 0
  if (!token || !expireAt) {
    return ''
  }
  if (Date.now() > expireAt) {
    removeToken()
    return ''
  }
  return token
}

/**
 * 保存登录 Token 及过期时间
 * @param {string} token 服务端返回的会话 Token
 */
export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
  uni.setStorageSync(TOKEN_EXPIRE_KEY, Date.now() + TOKEN_TTL_MS)
}

/**
 * 清除本地登录态
 */
export function removeToken() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(TOKEN_EXPIRE_KEY)
  uni.removeStorageSync(USER_INFO_KEY)
}

/**
 * 是否已登录（Token 存在且未过期）
 * @returns {boolean}
 */
export function isLoggedIn() {
  return !!getToken()
}

/**
 * 保存登录用户信息
 * @param {object} userInfo 用户信息
 */
export function setUserInfo(userInfo) {
  uni.setStorageSync(USER_INFO_KEY, userInfo || {})
}

/**
 * 获取登录用户信息
 * @returns {object}
 */
export function getUserInfo() {
  return uni.getStorageSync(USER_INFO_KEY) || {}
}
