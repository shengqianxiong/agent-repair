import { post } from '@/utils/request.js'

/** 用户登录 POST /app/login */
export function userLogin(data) {
  return post('/app/login', data, { skipAuth: true })
}
