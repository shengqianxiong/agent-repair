import config from '@/config/index.js'
import { getToken, removeToken } from '@/utils/auth.js'

/**
 * 统一 HTTP 请求封装
 * @param {object} options 请求配置
 * @returns {Promise<any>} 业务 data 字段
 */
export function request(options) {
  const { url, method = 'GET', data = {}, header = {}, showError = true } = options
  const token = getToken()

  return new Promise((resolve, reject) => {
    uni.request({
      url: config.baseUrl + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...header
      },
      success: (res) => {
        const body = res.data || {}

        if (res.statusCode === 401) {
          removeToken()
          uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/login/index' })
          }, 1500)
          reject(body)
          return
        }

        if (body.code === 0) {
          resolve(body.data)
          return
        }

        if (showError) {
          uni.showToast({ title: body.msg || '请求失败', icon: 'none' })
        }
        reject(body)
      },
      fail: () => {
        if (showError) {
          uni.showToast({ title: '网络异常，请稍后重试', icon: 'none' })
        }
        reject(new Error('network error'))
      }
    })
  })
}

export function get(url, data, options = {}) {
  return request({ url, method: 'GET', data, ...options })
}

export function post(url, data, options = {}) {
  return request({ url, method: 'POST', data, ...options })
}
