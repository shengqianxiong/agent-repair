import { get, post } from '@/utils/request.js'

/**
 * 用户登录。
 */
export const accountLogin = (data) => post('/app/account/login', data, { showError: false })

/**
 * 获取当前登录用户信息。
 */
export const getAccountInfo = () => get('/app/account/info')
