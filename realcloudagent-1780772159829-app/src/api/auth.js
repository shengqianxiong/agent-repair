import { post } from '@/utils/request.js'

/**
 * 用户端登录
 * @param {{ username: string, password: string }} data 登录凭证
 */
export function appLogin(data) {
  return post('/app/auth/login', data)
}
