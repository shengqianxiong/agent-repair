import config from '@/config/index.js'
import { getToken, clearAuth } from '@/utils/auth.js'

/**
 * 统一请求封装，对接服务端 Result 结构
 */
export function request(options) {
  const {
    url,
    method = 'GET',
    data = {},
    header = {},
    showError = true,
    redirectOnUnauthorized = true
  } = options
  const token = getToken()

  return new Promise((resolve, reject) => {
    uni.request({
      url: config.baseUrl + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...(token ? { token } : {}),
        ...header
      },
      success: (res) => {
        const body = res.data || {}

        if (res.statusCode === 401 || body.code === 401) {
          clearAuth()
          if (redirectOnUnauthorized) {
            uni.reLaunch({ url: '/pages/login/index' })
          }
          if (showError) {
            uni.showToast({ title: body.msg || '请先登录', icon: 'none' })
          }
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
          uni.showToast({ title: '网络异常', icon: 'none' })
        }
        reject(new Error('网络异常'))
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
