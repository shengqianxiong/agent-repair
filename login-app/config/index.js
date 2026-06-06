/** API 基础配置，H5 开发环境走 Vite 代理 */
const isDev = import.meta.env.DEV

const BASE_URL = isDev ? '/sqx_fast' : 'http://127.0.0.1:9295/sqx_fast'

export default {
  baseUrl: BASE_URL,
  appPrefix: '/app'
}
