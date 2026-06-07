import config from '@/config/index.js'
import { getToken, removeToken } from '@/utils/auth.js'
import { getMockResponse } from '@/mock/index.js'

/**
 * 统一请求封装
 * 鉴权 Header: token（登录接口除外）
 */
export function request(options) {
  const { url, method = 'GET', data = {}, header = {}, skipAuth = false } = options

  if (config.useMock) {
    return getMockResponse({ url, method, data })
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
          removeToken()
          uni.showToast({ title: '请先登录', icon: 'none' })
          reject(body)
          return
        }
        if (body.code === 0 || body.code === 200) {
          resolve(body.data)
        } else {
          uni.showToast({ title: body.msg || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
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

export function put(url, data, options = {}) {
  return request({ url, method: 'PUT', data, ...options })
}

export function del(url, data, options = {}) {
  return request({ url, method: 'DELETE', data, ...options })
}
