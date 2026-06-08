import config from '@/config/index.js'
import { getToken, clearAuth } from '@/utils/auth.js'
import { getMockResponse } from '@/mock/index.js'

const LOGIN_PAGE = '/pages/login/login'

/**
 * 统一请求封装
 * 鉴权 Header: token（登录接口除外）
 */
export function request(options) {
  const { url, method = 'GET', data = {}, header = {}, skipAuth = false } = options

  if (config.useMock) {
    return getMockResponse({ url, method, data }).catch((body) => {
      uni.showToast({ title: body?.message || body?.msg || '请求失败', icon: 'none' })
      return Promise.reject(body)
    })
  }

  const token = getToken()

  return new Promise((resolve, reject) => {
    uni.request({
      url: config.baseUrl + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...(!skipAuth && token ? { token } : {}),
        ...header
      },
      success: (res) => {
        const body = res.data || {}
        if (res.statusCode === 401) {
          clearAuth()
          uni.showToast({ title: '请先登录', icon: 'none' })
          uni.reLaunch({ url: LOGIN_PAGE })
          reject(body)
          return
        }
        if (body.code === 0 || body.code === 200) {
          resolve(body.data)
        } else {
          uni.showToast({ title: body.message || body.msg || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail: () => {
        uni.showToast({ title: '网络异常', icon: 'none' })
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
