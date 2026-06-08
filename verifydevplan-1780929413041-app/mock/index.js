import { mockLoginResponse } from './data/user.js'
import { mockProducts } from './data/products.js'

const MOCK_DELAY = 300

function delay(data) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(data), MOCK_DELAY)
  })
}

const routes = [
  {
    method: 'POST',
    pattern: /\/app\/login$/,
    handler: ({ data }) => {
      if (!data?.username || !data?.password) {
        return { __error: true, code: 400, message: '用户名和密码不能为空' }
      }
      return mockLoginResponse
    }
  },
  {
    method: 'GET',
    pattern: /\/app\/products$/,
    handler: ({ data }) => {
      const page = Number(data?.page) || 1
      const pageSize = Number(data?.pageSize) || 20
      const keyword = (data?.keyword || '').trim().toLowerCase()
      let list = mockProducts
      if (keyword) {
        list = list.filter(
          (item) =>
            item.name.toLowerCase().includes(keyword) ||
            item.desc.toLowerCase().includes(keyword)
        )
      }
      const start = (page - 1) * pageSize
      const pageList = list.slice(start, start + pageSize)
      return { list: pageList, total: list.length, page }
    }
  },
  {
    method: 'GET',
    pattern: /\/app\/products\/(\d+)$/,
    handler: (_, match) => mockProducts.find((p) => p.id === Number(match[1])) || mockProducts[0]
  }
]

export function getMockResponse({ url, method, data }) {
  const normalizedMethod = (method || 'GET').toUpperCase()
  for (const route of routes) {
    if (route.method !== normalizedMethod) continue
    const match = url.match(route.pattern)
    if (match) {
      const result = route.handler({ data }, match)
      if (result?.__error) {
        return delay(null).then(() => Promise.reject(result))
      }
      return delay(result)
    }
  }
  console.warn('[Mock] 未匹配接口:', normalizedMethod, url)
  return delay({})
}
