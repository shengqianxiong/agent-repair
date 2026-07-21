import request from '@/api/request'

/**
 * 管理员登录
 * @param {{ account: string, password: string }} data 登录凭证
 * @returns {Promise<{ token: string, id: number, account: string }>}
 */
export function login(data) {
  return request({
    url: '/admin/auth/login',
    method: 'post',
    data
  })
}
