import { BASE_URL } from './config'
import { getToken } from './auth'

export function request({ url, method = 'GET', data, header = {} }) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        token: getToken(),
        ...header
      },
      success: (res) => {
        const body = res.data
        if (body && body.code === 0) {
          resolve(body.data)
        } else if (body && body.code === 401) {
          uni.showToast({ title: body.msg || '请先登录', icon: 'none' })
          reject(body)
        } else {
          const msg = (body && body.msg) || '请求失败'
          reject({ ...body, msg })
        }
      },
      fail: (err) => {
        reject(err)
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
