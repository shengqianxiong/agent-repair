import request from '@/api/request'

/**
 * 管理端登录
 * @param {{ username: string, password: string }} data 登录凭证
 * @returns {Promise<{ token: string, username: string }>}
 */
export function login(data) {
  return request({
    url: '/admin/auth/login',
    method: 'post',
    data
  })
}
