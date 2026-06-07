import { mockLoginResponse, mockUser } from './data/user.js'
import { mockHomeData } from './data/home.js'
import { mockCategories, mockProducts, mockCart } from './data/product.js'
import { mockStore, mockOrders, mockOrderDetail } from './data/order.js'
import { mockWallet } from './data/wallet.js'
import { mockPoints } from './data/points.js'
import { mockStoredWines } from './data/wine.js'
import { mockDistribution } from './data/distribution.js'
import { mockSocialProfile, mockSquareFeed } from './data/social.js'
import { mockTableInteraction } from './data/table.js'

const MOCK_DELAY = 300

function delay(data) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(data), MOCK_DELAY)
  })
}

/** Mock 路由表 - 与 PRD §8.2 App 接口路径一致 */
const routes = [
  // 用户与鉴权
  { method: 'POST', pattern: /\/app\/user\/login$/, handler: () => mockLoginResponse },
  { method: 'POST', pattern: /\/app\/user\/register$/, handler: () => mockLoginResponse },
  { method: 'GET', pattern: /\/app\/user\/info$/, handler: () => mockUser },
  { method: 'GET', pattern: /\/app\/user\/profile$/, handler: () => mockUser },
  { method: 'POST', pattern: /\/app\/user\/update$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/common\/upload$/, handler: () => ({ url: mockUser.avatar }) },
  // 首页与活动
  { method: 'GET', pattern: /\/app\/home\/init$/, handler: () => mockHomeData },
  { method: 'GET', pattern: /\/app\/activity\/list/, handler: () => ({ list: mockHomeData.activities, total: mockHomeData.activities.length }) },
  { method: 'GET', pattern: /\/app\/activity\/detail\/(\d+)/, handler: (_, m) => mockHomeData.activities.find((a) => a.id === Number(m[1])) },
  // 点餐与购物车
  { method: 'GET', pattern: /\/app\/category\/list$/, handler: () => mockCategories },
  { method: 'GET', pattern: /\/app\/product\/list/, handler: ({ data }) => {
    const catId = data?.categoryId
    return catId ? mockProducts.filter((p) => p.categoryId === Number(catId)) : mockProducts
  }},
  { method: 'GET', pattern: /\/app\/product\/detail\/(\d+)/, handler: (_, m) => mockProducts.find((p) => p.id === Number(m[1])) || mockProducts[0] },
  { method: 'POST', pattern: /\/app\/cart\/add$/, handler: () => ({ success: true }) },
  { method: 'GET', pattern: /\/app\/cart\/summary$/, handler: () => mockCart },
  // 订单
  { method: 'POST', pattern: /\/app\/order\/preview$/, handler: () => ({ cart: mockCart, store: mockStore }) },
  { method: 'POST', pattern: /\/app\/order\/submit$/, handler: () => ({ orderId: mockOrderDetail.id }) },
  { method: 'POST', pattern: /\/app\/order\/create$/, handler: () => ({ orderId: mockOrderDetail.id }) },
  { method: 'GET', pattern: /\/app\/order\/list/, handler: ({ data }) => {
    const status = data?.status
    if (!status || status === 'all') return { list: mockOrders, total: mockOrders.length }
    return { list: mockOrders.filter((o) => o.status === status), total: 1 }
  }},
  { method: 'GET', pattern: /\/app\/order\/detail\/(.+)/, handler: () => mockOrderDetail },
  { method: 'POST', pattern: /\/app\/order\/reorder$/, handler: () => ({ success: true }) },
  { method: 'GET', pattern: /\/app\/order\/pickup-code\/(.+)/, handler: () => ({ pickupCode: mockOrderDetail.pickupCode }) },
  { method: 'POST', pattern: /\/app\/order\/cancel$/, handler: () => ({ success: true }) },
  // 支付
  { method: 'GET', pattern: /\/app\/payment\/list$/, handler: () => ({ methods: [{ type: 'wechat', name: '微信支付' }, { type: 'balance', name: '零钱支付', balance: mockWallet.balance }] }) },
  // 钱包
  { method: 'GET', pattern: /\/app\/wallet\/info$/, handler: () => ({ balance: mockWallet.balance }) },
  { method: 'GET', pattern: /\/app\/wallet\/bill\/list/, handler: () => ({ list: mockWallet.fundRecords, total: mockWallet.fundRecords.length }) },
  { method: 'GET', pattern: /\/app\/wallet\/withdraw\/list/, handler: () => ({ list: mockWallet.withdrawRecords, total: mockWallet.withdrawRecords.length }) },
  { method: 'GET', pattern: /\/app\/wallet\/account\/list$/, handler: () => mockWallet.withdrawAccount },
  { method: 'POST', pattern: /\/app\/wallet\/withdraw$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/wallet\/recharge$/, handler: () => ({ success: true }) },
  // 积分
  { method: 'GET', pattern: /\/app\/points\/info$/, handler: () => ({ balance: mockPoints.balance, totalPoints: mockPoints.balance }) },
  { method: 'GET', pattern: /\/app\/points\/list/, handler: () => ({ list: mockPoints.records, total: mockPoints.records.length }) },
  { method: 'GET', pattern: /\/app\/points\/products/, handler: () => ({ list: mockPoints.mallProducts, total: mockPoints.mallProducts.length }) },
  { method: 'POST', pattern: /\/app\/points\/redeem$/, handler: () => ({ success: true }) },
  { method: 'GET', pattern: /\/app\/points\/history/, handler: () => ({ list: [], total: 0 }) },
  { method: 'POST', pattern: /\/app\/points\/checkin$/, handler: () => ({ points: 10 }) },
  // 存酒
  { method: 'GET', pattern: /\/app\/alcohol\/stored\/list/, handler: ({ data }) => {
    const kw = data?.keyword
    const list = kw ? mockStoredWines.filter((w) => w.name.includes(kw)) : mockStoredWines
    return { list, total: list.length }
  }},
  { method: 'POST', pattern: /\/app\/alcohol\/stored\/retrieve$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/alcohol\/stored\/renew$/, handler: () => ({ success: true }) },
  // 分销
  { method: 'GET', pattern: /\/app\/distribution\/info$/, handler: () => mockDistribution },
  { method: 'GET', pattern: /\/app\/distribution\/invitees/, handler: () => ({ list: mockDistribution.leaderboard, total: mockDistribution.leaderboard.length }) },
  { method: 'POST', pattern: /\/app\/distribution\/withdraw$/, handler: () => ({ success: true }) },
  // 广场与社交
  { method: 'GET', pattern: /\/app\/square\/list/, handler: () => ({ list: mockSquareFeed, total: mockSquareFeed.length }) },
  { method: 'POST', pattern: /\/app\/square\/like$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/square\/create$/, handler: () => ({ success: true }) },
  { method: 'GET', pattern: /\/app\/square\/categories$/, handler: () => ({ list: ['推荐', '附近', '热门'] }) },
  { method: 'GET', pattern: /\/app\/dynamic\/user_list/, handler: () => mockSocialProfile },
  { method: 'POST', pattern: /\/app\/chat\/create$/, handler: () => ({ chatId: 1 }) },
  // 桌台互动
  { method: 'GET', pattern: /\/app\/table\/detail/, handler: () => mockTableInteraction },
  { method: 'POST', pattern: /\/app\/table\/seat\/join$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/table\/seat\/leave$/, handler: () => ({ success: true }) }
]

/**
 * 根据请求匹配 Mock 数据
 */
export function getMockResponse({ url, method, data }) {
  const normalizedMethod = (method || 'GET').toUpperCase()
  for (const route of routes) {
    if (route.method !== normalizedMethod) continue
    const match = url.match(route.pattern)
    if (match) {
      const result = route.handler({ data }, match)
      return delay(result)
    }
  }
  console.warn('[Mock] 未匹配接口:', normalizedMethod, url)
  return delay({})
}
