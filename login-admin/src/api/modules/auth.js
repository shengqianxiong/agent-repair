import request from '../request'

/**
 * 管理员登录
 * @param {{ account: string, password: string }} data
 */
export function adminLogin(data) {
  return request.post('/admin/auth/login', data)
}

/**
 * 获取当前登录管理员信息
 */
export function getAdminInfo() {
  return request.get('/admin/auth/info')
}
