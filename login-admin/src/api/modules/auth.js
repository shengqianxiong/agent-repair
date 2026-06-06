import request from '../request'

/**
 * 管理员登录
 * @param {{ username: string, password: string }} data
 */
export function adminLogin(data) {
  return request.post('/admin/auth/login', data)
}

/**
 * 退出登录
 */
export function adminLogout() {
  return request.post('/admin/auth/logout')
}
