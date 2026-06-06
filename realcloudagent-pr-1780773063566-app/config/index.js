/** API 基础配置 */
let baseUrl = 'http://127.0.0.1:9294/sqx_fast'

// H5 开发环境走本地代理，避免跨域
// #ifdef H5
if (process.env.NODE_ENV === 'development') {
  baseUrl = '/sqx_fast'
}
// #endif

export default {
  baseUrl,
  appPrefix: '/app'
}
