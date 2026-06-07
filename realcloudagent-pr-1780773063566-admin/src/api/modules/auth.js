import request from '@/api/request'

/**
 * 管理员登录
 */
export function adminLogin(data) {
  return request({
    url: '/admin/user/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前管理员信息
 */
export function getAdminInfo() {
  return request({
    url: '/admin/user/info',
    method: 'get'
  })
}
