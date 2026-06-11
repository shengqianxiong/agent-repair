import config from '@/config/index.js'
import { getToken, removeToken } from '@/utils/auth.js'

/**
 * uni-app 统一请求封装
 * 默认携带 token，登录与注册接口请传 requireToken: false
 */
export function request(options) {
  const {
    url,
    method = 'GET',
    data = {},
    header = {},
    requireToken = true
  } = options
  const token = getToken()

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${config.baseUrl}${url}`,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...(requireToken && token ? { token } : {}),
        ...header
      },
      success: ({ data: body = {}, statusCode }) => {
        if (statusCode === 401 || body.code === 401) {
          removeToken()
          uni.showToast({ title: body.msg || '登录状态失效，请重新登录', icon: 'none' })
          reject(body)
          return
        }
        if (body.code === 0 || body.code === 200 || statusCode === 200) {
          resolve(body.data ?? body)
          return
        }
        uni.showToast({ title: body.msg || '请求失败，请稍后重试', icon: 'none' })
        reject(body)
      },
      fail: (error) => {
        uni.showToast({ title: '网络异常，请检查网络连接', icon: 'none' })
        reject(error)
      }
    })
  })
}

export const get = (url, data, options = {}) => request({ url, method: 'GET', data, ...options })
export const post = (url, data, options = {}) => request({ url, method: 'POST', data, ...options })
export const put = (url, data, options = {}) => request({ url, method: 'PUT', data, ...options })
export const del = (url, data, options = {}) => request({ url, method: 'DELETE', data, ...options })
