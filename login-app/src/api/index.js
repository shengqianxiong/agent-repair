import { post } from '@/utils/request.js'

/**
 * 用户登录
 * @param {{ account: string, password: string }} data
 */
export function userLogin(data) {
  return post('/app/user/login', data)
}
