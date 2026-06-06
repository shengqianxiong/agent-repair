/**
 * API 基础配置
 * 开发环境通过 Vite 代理转发至 login-server（9295）
 */
const isDev = process.env.NODE_ENV === 'development'

export default {
  /** 接口根路径，Context 为 /sqx_fast */
  baseUrl: isDev ? '/sqx_fast' : 'http://127.0.0.1:9295/sqx_fast',
  /** 用户端接口前缀 */
  appPrefix: '/app'
}
