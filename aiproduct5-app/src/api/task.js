import { get, post } from '@/common/httpRequest.js'
import { ensureVisitorId } from '@/utils/auth.js'

export const demoActivity = {
  id: 10001,
  code: 'DP20260611',
  title: '满杯鲜萃茶饮评价任务',
  productName: '招牌厚乳拿铁',
  productImage: 'https://images.unsplash.com/photo-1541167760496-1628856ab772?auto=format&fit=crop&w=520&q=80',
  rebateAmount: 20,
  validEndTime: '2026-06-30 23:59',
  douyinLink: 'https://www.douyin.com',
  shopName: '豆评严选旗舰店',
  copywritingList: [
    {
      id: 1,
      title: '真实体验版',
      content: '这杯厚乳拿铁口感很顺，奶香和咖啡香平衡得刚好，包装也很干净。到手后冰感还在，整体体验超出预期，适合下午提神。'
    },
    {
      id: 2,
      title: '精简好评版',
      content: '口感顺滑，奶香浓郁，甜度刚好，配送速度也快。已经收藏店铺，下次还会回购。'
    },
    {
      id: 3,
      title: '图文种草版',
      content: '颜值和口味都在线，杯身很适合拍照。入口不会腻，厚乳香气明显，喜欢奶咖的朋友可以放心冲。'
    }
  ],
  recommendImages: [
    'https://images.unsplash.com/photo-1522992319-0365e5f11656?auto=format&fit=crop&w=520&q=80',
    'https://images.unsplash.com/photo-1554118811-1e0d58224f24?auto=format&fit=crop&w=520&q=80',
    'https://images.unsplash.com/photo-1442512595331-e89e73853f31?auto=format&fit=crop&w=520&q=80'
  ]
}

export const demoTasks = [
  {
    taskId: 88001,
    rebateId: 66001,
    activityId: 10001,
    activityTitle: '满杯鲜萃茶饮评价任务',
    productName: '招牌厚乳拿铁',
    productImage: demoActivity.productImage,
    status: 'auditing',
    statusText: '审核中',
    rebateAmount: 20,
    submitTime: '2026-06-11 14:28',
    rejectReason: ''
  },
  {
    taskId: 88002,
    rebateId: 66002,
    activityTitle: '夏日轻食套餐评价',
    productName: '低脂鸡胸沙拉',
    productImage: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=520&q=80',
    status: 'paid',
    statusText: '已到账',
    rebateAmount: 18,
    submitTime: '2026-06-08 10:12',
    paidTime: '2026-06-09 09:35',
    rejectReason: ''
  },
  {
    taskId: 88003,
    rebateId: 66003,
    activityTitle: '爆款小食评价',
    productName: '芝士薯角',
    productImage: 'https://images.unsplash.com/photo-1630383249896-424e482df921?auto=format&fit=crop&w=520&q=80',
    status: 'rejected',
    statusText: '已拒绝',
    rebateAmount: 15,
    submitTime: '2026-06-05 18:20',
    rejectReason: '截图未包含完整评价内容，请重新提交清晰截图。'
  }
]

export function getActivityByCode(code) {
  return get('/app/activity/by-code', { code })
}

export function startTask(activityId) {
  return post('/app/task/start', {
    activityId,
    userIdentifier: ensureVisitorId()
  })
}

export function reportCopyBehavior(taskId, copyTextId) {
  return post('/app/task/copy-report', {
    taskId,
    copyTextId
  })
}

export function submitTask(data) {
  return post('/app/task/submit', {
    ...data,
    userIdentifier: ensureVisitorId()
  })
}

export function getTaskDetail(taskId) {
  return get('/app/task/detail', { taskId })
}

export function getUserSummary() {
  return get('/app/user/summary', {
    userIdentifier: ensureVisitorId()
  })
}

export function getUserTasks(params = {}) {
  return get('/app/user/tasks', {
    page: 1,
    pageSize: 20,
    userIdentifier: ensureVisitorId(),
    ...params
  })
}

export function getRebateDetail(rebateId) {
  return get('/app/rebate/detail', { rebateId })
}
