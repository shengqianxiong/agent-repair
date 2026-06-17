const TOKEN_KEY = 'login_app_token'
const ACCOUNT_KEY = 'login_app_account'
const EXPIRE_KEY = 'login_app_token_expire'

/** Token 默认有效期 2 小时（毫秒） */
const TOKEN_EXPIRE_MS = 2 * 60 * 60 * 1000

export function getToken() {
  if (isTokenExpired()) {
    removeToken()
    return ''
  }
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
  uni.setStorageSync(EXPIRE_KEY, Date.now() + TOKEN_EXPIRE_MS)
}

export function removeToken() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(ACCOUNT_KEY)
  uni.removeStorageSync(EXPIRE_KEY)
}

export function getAccount() {
  return uni.getStorageSync(ACCOUNT_KEY) || ''
}

export function setAccount(account) {
  uni.setStorageSync(ACCOUNT_KEY, account)
}

export function isTokenExpired() {
  const expire = uni.getStorageSync(EXPIRE_KEY)
  return expire && Date.now() > expire
}

export function isLoggedIn() {
  return !!getToken()
}
