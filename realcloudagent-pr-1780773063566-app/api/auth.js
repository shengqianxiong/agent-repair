import { post } from '@/utils/request.js'

/**
 * 用户端登录
 * @param {{ username: string, password: string }} data
 */
export function login(data) {
  return post('/app/user/login', data)
}
