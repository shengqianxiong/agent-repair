/** API 基础配置，开发环境通过 Vite 代理访问服务端 */
const BASE_URL = import.meta.env.VITE_API_BASE || 'http://127.0.0.1:9295/sqx_fast'

/** Token 默认有效期（小时），与服务端保持一致 */
const TOKEN_EXPIRE_HOURS = 2

export default {
  baseUrl: BASE_URL,
  tokenExpireHours: TOKEN_EXPIRE_HOURS
}
