import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

const LOGIN_PATH = '/admin/user/login'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/sqx_fast',
  timeout: 30000
})

function isLoginRequest(config) {
  const url = config?.url || ''
  return url === LOGIN_PATH || url.endsWith(LOGIN_PATH)
}

service.interceptors.request.use((config) => {
  // 登录接口不做 token 鉴权，避免携带过期 token 导致登录失败
  if (!isLoginRequest(config)) {
    const token = getToken()
    if (token) {
      config.headers.token = token
    }
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 登录接口不触发 token 失效跳转
    if (res.code === 401 && !isLoginRequest(response.config)) {
      removeToken()
      router.push('/login')
      ElMessage.error(res.msg || '请先登录')
      return Promise.reject(new Error(res.msg || '请先登录'))
    }
    if (res.code !== 0) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res.data
  },
  (error) => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service
