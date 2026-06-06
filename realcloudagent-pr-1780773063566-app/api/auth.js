import { get, post } from '@/utils/request.js'

/**
 * 用户端登录
 * @param {{ username: string, password: string }} data
 */
export function login(data) {
  return post('/app/user/login', data)
}

/**
 * 获取当前登录用户信息
 */
export function getCurrentUser() {
  return get('/app/user/info')
}
