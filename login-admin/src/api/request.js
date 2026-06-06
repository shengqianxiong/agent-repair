import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { clearAuth, getToken } from '@/utils/auth'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/sqx_fast',
  timeout: 30000
})

/** 请求拦截：附加管理员 Token */
service.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.token = token
  }
  return config
})

/** 响应拦截：统一处理业务码与鉴权失效 */
service.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result.code !== 0) {
      const message = result.msg || '请求失败'
      if (result.code === 401) {
        clearAuth()
        router.replace('/login')
      }
      ElMessage.error(message)
      return Promise.reject(new Error(message))
    }
    return result.data
  },
  (error) => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service
