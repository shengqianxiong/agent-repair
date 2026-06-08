import config from '@/config/index.js'
import { getToken, removeToken } from '@/utils/auth.js'

/**
 * 统一请求封装
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
        if (body.code === 401) {
          removeToken()
          uni.showToast({ title: body.msg || '请先登录', icon: 'none' })
          uni.reLaunch({ url: '/pages/login/index' })
          reject(body)
          return
        }
        if (body.code === 0) {
          resolve(body.data)
        } else {
          uni.showToast({ title: body.msg || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail: () => {
        uni.showToast({ title: '网络异常', icon: 'none' })
        reject(new Error('网络异常'))
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

export function put(url, data) {
  return request({ url, method: 'PUT', data })
}

export function del(url, data) {
  return request({ url, method: 'DELETE', data })
}
