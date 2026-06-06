const TOKEN_KEY = 'login_app_token'
const USER_KEY = 'login_app_user'

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
  const raw = uni.getStorageSync(USER_KEY)
  if (!raw) {
    return null
  }
  try {
    return typeof raw === 'string' ? JSON.parse(raw) : raw
  } catch (error) {
    return null
  }
}

export function setUserInfo(userInfo) {
  uni.setStorageSync(USER_KEY, JSON.stringify(userInfo))
}

export function removeUserInfo() {
  uni.removeStorageSync(USER_KEY)
}

export function isLoggedIn() {
  return !!getToken()
}

export function clearAuth() {
  removeToken()
  removeUserInfo()
}
