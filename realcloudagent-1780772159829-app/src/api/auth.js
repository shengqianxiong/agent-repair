import { post } from '@/utils/request.js'

/**
 * 用户端登录
 * POST /app/auth/login
 */
export function login(data) {
  return post('/app/auth/login', data)
}
