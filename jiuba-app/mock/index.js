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

/** Mock 路由表 - 覆盖 App 端全部接口 */
const routes = [
  // 用户
  { method: 'POST', pattern: /\/app\/user\/login$/, handler: () => mockLoginResponse },
  { method: 'GET', pattern: /\/app\/user\/info$/, handler: () => mockUser },
  { method: 'PUT', pattern: /\/app\/user\/profile$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/user\/avatar$/, handler: () => ({ url: mockUser.avatar }) },
  // 首页
  { method: 'GET', pattern: /\/app\/home\/index$/, handler: () => mockHomeData },
  { method: 'GET', pattern: /\/app\/notification\/unread-count$/, handler: () => ({ count: 3 }) },
  { method: 'GET', pattern: /\/app\/notification\/list/, handler: () => ({ list: [], total: 0 }) },
  // 商品
  { method: 'GET', pattern: /\/app\/product\/category\/list$/, handler: () => mockCategories },
  { method: 'GET', pattern: /\/app\/product\/list/, handler: ({ data }) => {
    const catId = data?.categoryId
    return catId ? mockProducts.filter((p) => p.categoryId === Number(catId)) : mockProducts
  }},
  { method: 'GET', pattern: /\/app\/product\/detail\/(\d+)/, handler: (_, m) => mockProducts.find((p) => p.id === Number(m[1])) || mockProducts[0] },
  // 购物车
  { method: 'GET', pattern: /\/app\/cart\/list$/, handler: () => mockCart },
  { method: 'POST', pattern: /\/app\/cart\/add$/, handler: () => ({ success: true }) },
  { method: 'PUT', pattern: /\/app\/cart\/update$/, handler: () => ({ success: true }) },
  { method: 'DELETE', pattern: /\/app\/cart\/remove\/(\d+)/, handler: () => ({ success: true }) },
  // 订单
  { method: 'GET', pattern: /\/app\/table\/list$/, handler: () => [mockStore] },
  { method: 'POST', pattern: /\/app\/order\/submit$/, handler: () => ({ orderId: mockOrderDetail.id }) },
  { method: 'POST', pattern: /\/app\/order\/pay$/, handler: () => ({ success: true }) },
  { method: 'GET', pattern: /\/app\/order\/list/, handler: ({ data }) => {
    const status = data?.status
    if (!status || status === 'all') return { list: mockOrders, total: mockOrders.length }
    return { list: mockOrders.filter((o) => o.status === status), total: 1 }
  }},
  { method: 'GET', pattern: /\/app\/order\/detail\/(.+)/, handler: () => mockOrderDetail },
  { method: 'PUT', pattern: /\/app\/order\/cancel\/(.+)/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/order\/reorder\/(.+)/, handler: () => ({ success: true }) },
  // 钱包
  { method: 'GET', pattern: /\/app\/wallet\/balance$/, handler: () => ({ balance: mockWallet.balance }) },
  { method: 'GET', pattern: /\/app\/wallet\/fund-records/, handler: () => ({ list: mockWallet.fundRecords }) },
  { method: 'GET', pattern: /\/app\/wallet\/withdraw-records/, handler: () => ({ list: mockWallet.withdrawRecords }) },
  { method: 'GET', pattern: /\/app\/wallet\/withdraw-account$/, handler: () => mockWallet.withdrawAccount },
  { method: 'POST', pattern: /\/app\/wallet\/withdraw$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/wallet\/recharge$/, handler: () => ({ success: true }) },
  // 积分
  { method: 'GET', pattern: /\/app\/points\/balance$/, handler: () => ({ balance: mockPoints.balance }) },
  { method: 'GET', pattern: /\/app\/points\/records/, handler: () => ({ list: mockPoints.records }) },
  { method: 'GET', pattern: /\/app\/points\/mall\/list/, handler: () => ({ list: mockPoints.mallProducts }) },
  { method: 'POST', pattern: /\/app\/points\/redeem$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/points\/checkin$/, handler: () => ({ points: 10 }) },
  // 存酒
  { method: 'GET', pattern: /\/app\/wine\/stored\/list/, handler: ({ data }) => {
    const kw = data?.keyword
    const list = kw ? mockStoredWines.filter((w) => w.name.includes(kw)) : mockStoredWines
    return { list, total: list.length }
  }},
  { method: 'POST', pattern: /\/app\/wine\/retrieve$/, handler: () => ({ success: true }) },
  { method: 'POST', pattern: /\/app\/wine\/renew$/, handler: () => ({ success: true }) },
  // 分销
  { method: 'GET', pattern: /\/app\/distribution\/info$/, handler: () => mockDistribution },
  { method: 'GET', pattern: /\/app\/distribution\/leaderboard/, handler: () => ({ list: mockDistribution.leaderboard }) },
  // 社交
  { method: 'GET', pattern: /\/app\/social\/feed/, handler: () => ({ list: mockSquareFeed }) },
  { method: 'GET', pattern: /\/app\/social\/profile\/(\d+)/, handler: () => mockSocialProfile },
  { method: 'POST', pattern: /\/app\/chat\/send$/, handler: () => ({ success: true }) },
  // 桌台互动
  { method: 'GET', pattern: /\/app\/table\/interaction/, handler: () => mockTableInteraction },
  { method: 'POST', pattern: /\/app\/table\/seat$/, handler: () => ({ success: true }) },
  { method: 'DELETE', pattern: /\/app\/table\/seat$/, handler: () => ({ success: true }) },
  // 活动
  { method: 'GET', pattern: /\/app\/activity\/list/, handler: () => ({ list: mockHomeData.activities }) },
  { method: 'GET', pattern: /\/app\/activity\/detail\/(\d+)/, handler: (_, m) => mockHomeData.activities.find((a) => a.id === Number(m[1])) },
  // 预约
  { method: 'GET', pattern: /\/app\/booking\/time-slots/, handler: () => ({ slots: [] }) },
  { method: 'POST', pattern: /\/app\/booking\/submit$/, handler: () => ({ success: true }) },
  // 会员
  { method: 'GET', pattern: /\/app\/member\/info$/, handler: () => ({ level: 5, levelName: '高级会员' }) },
  // 排行榜
  { method: 'GET', pattern: /\/app\/rank\/list/, handler: () => ({ list: [] }) }
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
