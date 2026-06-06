const TOKEN_KEY = 'admin_token'
const USERNAME_KEY = 'admin_username'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUsername() {
  return localStorage.getItem(USERNAME_KEY) || ''
}

export function setUsername(username) {
  localStorage.setItem(USERNAME_KEY, username)
}

export function removeUsername() {
  localStorage.removeItem(USERNAME_KEY)
}

export function isLoggedIn() {
  return !!getToken()
}

export function clearAuth() {
  removeToken()
  removeUsername()
}

export function saveAuth(token, username) {
  setToken(token)
  setUsername(username)
}
