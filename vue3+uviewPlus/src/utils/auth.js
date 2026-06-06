import { DEV_TOKEN } from './config'

const TOKEN_KEY = 'token'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function ensureLogin() {
  if (!getToken()) {
    setToken(DEV_TOKEN)
  }
}

export function clearToken() {
  uni.removeStorageSync(TOKEN_KEY)
}
