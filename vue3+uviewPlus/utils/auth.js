const TOKEN_KEY = 'bar_token'
const USER_KEY = 'bar_user'

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
