// #ifdef H5
const BASE_URL = '/sqx_fast/app'
// #endif
// #ifndef H5
const BASE_URL = 'http://127.0.0.1:9294/sqx_fast/app'
// #endif

import { getToken, clearAuth } from './auth'

function showToast(title) {
  uni.showToast({ title, icon: 'none', duration: 2000 })
}

export function request(options) {
  const { url, method = 'GET', data, header = {}, showLoading = false, loadingText = '加载中...' } = options

  if (showLoading) {
    uni.showLoading({ title: loadingText, mask: true })
  }

  const token = getToken()

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}${url}`,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...(token ? { token } : {}),
        ...header
      },
      success(res) {
        if (showLoading) uni.hideLoading()
        const body = res.data
        if (body && body.code === 0) {
          resolve(body.data)
          return
        }
        const msg = (body && body.msg) || '请求失败'
        if (body && body.code === 401) {
          clearAuth()
          showToast('请先登录')
        } else {
          showToast(msg)
        }
        reject(body || { code: -1, msg })
      },
      fail(err) {
        if (showLoading) uni.hideLoading()
        showToast('网络异常，请稍后重试')
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

export function formatPrice(val) {
  if (val === null || val === undefined) return '0.00'
  return Number(val).toFixed(2)
}

export function formatDateTime(val) {
  if (!val) return ''
  const d = new Date(val)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
