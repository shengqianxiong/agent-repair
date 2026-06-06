/** API 基础配置，联调时由环境统一对齐 */
const BASE_URL = 'http://127.0.0.1:9295/sqx_fast'

export default {
  baseUrl: BASE_URL,
  appPrefix: '/app',
  /** 会话 Token 有效期（小时），与服务端保持一致 */
  tokenExpireHours: 2
}
