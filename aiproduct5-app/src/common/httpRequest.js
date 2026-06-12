import { getToken } from '@/utils/auth.js'

const BASE_URL = 'http://127.0.0.1:9294/sqx_fast'

function normalizeResponse(res) {
  const body = res.data || {}
  if (body.code === 0 || body.code === 200) {
    return body.data ?? body.result ?? body
  }
  throw body
}

export function httpRequest(options) {
  const { url, method = 'GET', data = {}, header = {} } = options
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
      success: (res) => {
        try {
          resolve(normalizeResponse(res))
        } catch (error) {
          uni.showToast({ title: error.msg || error.message || '请求失败', icon: 'none' })
          reject(error)
        }
      },
      fail: () => {
        reject(new Error('网络异常'))
      }
    })
  })
}

export function get(url, data) {
  return httpRequest({ url, method: 'GET', data })
}

export function post(url, data) {
  return httpRequest({ url, method: 'POST', data })
}

export function uploadFile(url, filePath, formData = {}) {
  const token = getToken()
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${BASE_URL}${url}`,
      filePath,
      name: 'file',
      formData,
      header: {
        ...(token ? { token } : {})
      },
      success: (res) => {
        try {
          const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data
          resolve(normalizeResponse({ data }))
        } catch (error) {
          uni.showToast({ title: error.msg || '上传失败', icon: 'none' })
          reject(error)
        }
      },
      fail: () => {
        reject(new Error('上传失败'))
      }
    })
  })
}
