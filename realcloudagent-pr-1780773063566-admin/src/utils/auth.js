const TOKEN_KEY = 'rca_admin_token'
const ACCOUNT_KEY = 'rca_admin_account'
const ACCOUNT_ID_KEY = 'rca_admin_account_id'

/** 获取本地存储的登录令牌 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/** 保存登录令牌 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 清除登录态 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ACCOUNT_KEY)
  localStorage.removeItem(ACCOUNT_ID_KEY)
}

/** 获取当前登录账号名 */
export function getAccount() {
  return localStorage.getItem(ACCOUNT_KEY) || ''
}

/** 保存当前登录账号名 */
export function setAccount(account) {
  localStorage.setItem(ACCOUNT_KEY, account)
}

/** 获取当前登录账号 ID */
export function getAccountId() {
  const id = localStorage.getItem(ACCOUNT_ID_KEY)
  return id ? Number(id) : null
}

/** 保存当前登录账号 ID */
export function setAccountId(id) {
  localStorage.setItem(ACCOUNT_ID_KEY, String(id))
}

/** 判断是否已登录 */
export function isLoggedIn() {
  return !!getToken()
}
