const TOKEN_KEY = 'admin_token'
const USERNAME_KEY = 'admin_username'

/** 获取本地存储的登录令牌 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/** 持久化登录令牌 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 清除登录令牌 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

/** 获取当前登录用户名 */
export function getUsername() {
  return localStorage.getItem(USERNAME_KEY) || ''
}

/** 持久化登录用户名 */
export function setUsername(username) {
  localStorage.setItem(USERNAME_KEY, username)
}

/** 清除登录用户名 */
export function removeUsername() {
  localStorage.removeItem(USERNAME_KEY)
}

/** 判断是否已登录 */
export function isLoggedIn() {
  return !!getToken()
}

/** 退出登录并清理本地凭证 */
export function clearAuth() {
  removeToken()
  removeUsername()
}
