const TOKEN_KEY = 'app_token'
const USERNAME_KEY = 'app_username'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function getUsername() {
  return uni.getStorageSync(USERNAME_KEY) || ''
}

export function setUsername(username) {
  uni.setStorageSync(USERNAME_KEY, username)
}

export function removeToken() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(USERNAME_KEY)
}
