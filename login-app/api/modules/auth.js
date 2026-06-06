import config from '@/config/index.js'
import { post } from '@/utils/request.js'

/**
 * 用户端登录
 * @param {{ username: string, password: string }} payload
 */
export function login(payload) {
  return post(`${config.appPrefix}/login`, payload, {
    showError: false
  })
}
