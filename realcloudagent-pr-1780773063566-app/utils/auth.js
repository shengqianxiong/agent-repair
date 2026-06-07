const TOKEN_KEY = 'app_token'
const USER_INFO_KEY = 'app_user_info'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function removeToken() {
  uni.removeStorageSync(TOKEN_KEY)
}

export function getUserInfo() {
  const info = uni.getStorageSync(USER_INFO_KEY)
  return info || null
}

export function setUserInfo(userInfo) {
  uni.setStorageSync(USER_INFO_KEY, userInfo)
}

export function removeUserInfo() {
  uni.removeStorageSync(USER_INFO_KEY)
}

export function isLoggedIn() {
  return !!getToken()
}

export function clearAuth() {
  removeToken()
  removeUserInfo()
}
