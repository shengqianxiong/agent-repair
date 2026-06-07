/** API 与 Mock 开关配置 */
const BASE_URL = 'http://127.0.0.1:9294/sqx_fast'

/** 本地开发默认走 Mock，无需后端 */
const USE_MOCK = true

export default {
  baseUrl: BASE_URL,
  useMock: USE_MOCK,
  appPrefix: '/app',
  appName: '云享生活',
  version: '3.4.0'
}
