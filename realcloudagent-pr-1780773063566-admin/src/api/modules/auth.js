import request from '@/api/request'

/**
 * 管理端登录
 * @param {{ username: string, password: string }} data 登录凭证
 * @returns {Promise<{ token: string, username: string }>}
 */
export function adminLogin(data) {
  return request.post('/admin/user/login', data)
}

/**
 * 获取当前登录管理员信息
 * @returns {Promise<{ username: string, nickname?: string }>}
 */
export function getAdminInfo() {
  return request.get('/admin/user/info')
}
