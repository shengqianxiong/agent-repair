/**
 * API 基础配置
 * H5 开发环境通过 Vite 代理 /sqx_fast → 9294
 */
const isH5Dev = typeof window !== 'undefined' && import.meta.env.DEV

const BASE_URL = isH5Dev ? '/sqx_fast' : 'http://127.0.0.1:9294/sqx_fast'

export default {
  baseUrl: BASE_URL,
  appPrefix: '/app'
}
