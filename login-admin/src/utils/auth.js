const TOKEN_KEY = 'login_admin_token'
const USERNAME_KEY = 'login_admin_username'
const ACCOUNT_ID_KEY = 'login_admin_account_id'

/** 保存登录会话信息 */
export function setAuthSession({ token, username, accountId }) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USERNAME_KEY, username)
  localStorage.setItem(ACCOUNT_ID_KEY, String(accountId))
}

/** 获取 Token */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

/** 获取当前登录账号名 */
export function getUsername() {
  return localStorage.getItem(USERNAME_KEY) || ''
}

/** 获取当前登录账号 ID */
export function getAccountId() {
  const id = localStorage.getItem(ACCOUNT_ID_KEY)
  return id ? Number(id) : null
}

/** 是否已登录 */
export function isLoggedIn() {
  return Boolean(getToken())
}

/** 清除登录态 */
export function clearAuthSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USERNAME_KEY)
  localStorage.removeItem(ACCOUNT_ID_KEY)
}
