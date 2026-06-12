import request from '@/api/request'

/**
 * 管理员登录
 */
export function login(data) {
  return request({
    url: '/admin/auth/login',
    method: 'post',
    data
  })
}
