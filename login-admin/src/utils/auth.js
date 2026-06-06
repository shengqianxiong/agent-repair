const TOKEN_KEY = 'login_admin_token'
const ACCOUNT_ID_KEY = 'login_admin_account_id'
const USERNAME_KEY = 'login_admin_username'

/**
 * 保存管理员登录会话信息。
 */
export function setAuthInfo({ token, accountId, username }) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(ACCOUNT_ID_KEY, String(accountId))
  localStorage.setItem(USERNAME_KEY, username)
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function getAccountId() {
  const id = localStorage.getItem(ACCOUNT_ID_KEY)
  return id ? Number(id) : null
}

export function getUsername() {
  return localStorage.getItem(USERNAME_KEY) || ''
}

export function isLoggedIn() {
  return Boolean(getToken())
}

export function clearAuthInfo() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ACCOUNT_ID_KEY)
  localStorage.removeItem(USERNAME_KEY)
}
