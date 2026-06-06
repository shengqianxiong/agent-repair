import { post } from '@/utils/request.js'

/**
 * 用户端登录接口
 * @param {{ username: string, password: string }} data
 * @returns {Promise<{ token: string, username: string }>}
 */
export const login = (data) => post('/app/login', data, { showError: false })
