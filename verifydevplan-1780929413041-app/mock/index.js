import { mockLoginResponse } from './data/user.js'
import { mockProducts } from './data/products.js'

const MOCK_DELAY = 300

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

const routes = [
  {
    method: 'POST',
    pattern: /\/app\/login$/,
    handler: ({ data }) => {
      const { username, password } = data || {}
      if (!username || !password) {
        throw { code: 400, message: '请输入用户名和密码' }
      }
      if (username === 'demo' && password === '123456') {
        return mockLoginResponse
      }
      throw { code: 400, message: '用户名或密码错误' }
    }
  },
  {
    method: 'GET',
    pattern: /\/app\/products$/,
    handler: ({ data }) => {
      const page = Number(data?.page) || 1
      const pageSize = Number(data?.pageSize) || 20
      const keyword = (data?.keyword || '').trim()
      let list = mockProducts
      if (keyword) {
        list = list.filter((item) => item.name.includes(keyword))
      }
      const start = (page - 1) * pageSize
      const pageList = list.slice(start, start + pageSize)
      return { list: pageList, total: list.length, page }
    }
  },
  {
    method: 'GET',
    pattern: /\/app\/products\/(\d+)$/,
    handler: (_, match) => {
      const product = mockProducts.find((item) => item.id === Number(match[1]))
      if (!product) {
        throw { code: 404, message: '商品不存在' }
      }
      return product
    }
  }
]

export async function getMockResponse({ url, method, data }) {
  await sleep(MOCK_DELAY)
  const normalizedMethod = (method || 'GET').toUpperCase()
  for (const route of routes) {
    if (route.method !== normalizedMethod) continue
    const match = url.match(route.pattern)
    if (match) {
      return route.handler({ data }, match)
    }
  }
  throw { code: 404, message: 'Mock 接口未定义: ' + url }
}

/** Mock 默认账号：demo / 123456 */
export const MOCK_CREDENTIALS = { username: 'demo', password: '123456' }
