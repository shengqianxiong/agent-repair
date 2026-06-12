import request from '@/api/request'

/** AI 生成记录分页 */
export function getAiHistoryPage(params) {
  return request.get('/admin/ai/history/page', { params })
}

/** AI 生成记录详情 */
export function getAiHistoryDetail(id) {
  return request.get('/admin/ai/history/detail', { params: { id } })
}

/** 重新生成 */
export function retryAiGenerate(data) {
  return request.post('/admin/ai/history/retry', data)
}
