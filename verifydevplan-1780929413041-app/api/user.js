import { post } from '@/utils/request.js'

/** 用户登录 POST /app/login */
export const userLogin = (data) => post('/app/login', data, { skipAuth: true })
