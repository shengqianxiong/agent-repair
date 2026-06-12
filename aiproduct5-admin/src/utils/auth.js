const TOKEN_KEY = 'login_admin_token'
const ACCOUNT_KEY = 'login_admin_account'
const ACCOUNT_ID_KEY = 'login_admin_account_id'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ACCOUNT_KEY)
  localStorage.removeItem(ACCOUNT_ID_KEY)
}

export function getAccount() {
  return localStorage.getItem(ACCOUNT_KEY) || ''
}

export function setAccount(account) {
  localStorage.setItem(ACCOUNT_KEY, account)
}

export function getAccountId() {
  const id = localStorage.getItem(ACCOUNT_ID_KEY)
  return id ? Number(id) : null
}

export function setAccountId(id) {
  localStorage.setItem(ACCOUNT_ID_KEY, String(id))
}

export function isLoggedIn() {
  return !!getToken()
}
