import request from '../request'

/**
 * 管理端登录
 * POST /admin/auth/login
 */
export function login(data) {
  return request.post('/admin/auth/login', data)
}
