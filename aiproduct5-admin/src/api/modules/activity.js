import request from '@/api/request'

/** 活动分页列表 */
export function getActivityPage(params) {
  return request.get('/admin/activity/page', { params })
}

/** 活动详情 */
export function getActivityDetail(id) {
  return request.get('/admin/activity/detail', { params: { id } })
}

/** 创建活动 */
export function createActivity(data) {
  return request.post('/admin/activity/create', data)
}

/** 编辑活动 */
export function updateActivity(data) {
  return request.put('/admin/activity/update', data)
}

/** 上下架活动 */
export function updateActivityStatus(id, status) {
  return request.put('/admin/activity/status', { id, status })
}

/** AI 生成文案 */
export function generateCopy(data) {
  return request.post('/admin/activity/ai-generate', data)
}

/** 活动统计数据 */
export function getActivityStats(id) {
  return request.get('/admin/activity/stats', { params: { id } })
}

/** 下载二维码 */
export function getQrCode(id) {
  return request.get('/admin/activity/qrcode', { params: { id } })
}
