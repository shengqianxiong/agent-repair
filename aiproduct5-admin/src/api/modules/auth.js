import request from '@/api/request'

/** 商家登录 */
export function login(data) {
  return request({
    url: '/admin/seller/login',
    method: 'post',
    data
  })
}
