import { get, post } from '@/utils/request.js'

/**
 * 用户登录
 * @param {{ username: string, password: string }} data
 */
export function userLogin(data) {
  return post('/app/user/login', data)
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return get('/app/user/info')
}
