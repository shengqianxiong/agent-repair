/** API 基础配置：H5 开发环境走 Vite 代理，避免跨域 */
const BASE_URL = import.meta.env.DEV ? '/sqx_fast' : 'http://127.0.0.1:9294/sqx_fast'

export default {
  baseUrl: BASE_URL,
  appPrefix: '/app'
}
