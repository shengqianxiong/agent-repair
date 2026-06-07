import config from '@/config/index.js'
import { getToken, clearAuth } from '@/utils/auth.js'

/**
 * 统一请求封装，对接服务端 Result 响应结构
 */
export function request(options) {
  const { url, method = 'GET', data = {}, header = {} } = options
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
          uni.showToast({ title: body.msg || '请先登录', icon: 'none' })
          uni.reLaunch({ url: '/pages/login/index' })
          reject(body)
          return
        }

        if (body.code === 0) {
          resolve(body.data)
          return
        }

        uni.showToast({ title: body.msg || '请求失败', icon: 'none' })
        reject(body)
      },
      fail: () => {
        uni.showToast({ title: '网络异常，请稍后重试', icon: 'none' })
        reject(new Error('network error'))
      }
    })
  })
}

export function get(url, data) {
  return request({ url, method: 'GET', data })
}

export function post(url, data) {
  return request({ url, method: 'POST', data })
}
