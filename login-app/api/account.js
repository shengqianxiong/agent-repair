import { post } from '@/utils/request.js'

/**
 * 用户端账号登录
 * @param {{ username: string, password: string }} data 登录参数
 * @returns {Promise<{ token: string, username: string }>}
 */
export function accountLogin(data) {
  return post('/app/account/login', data, { showError: false })
}
