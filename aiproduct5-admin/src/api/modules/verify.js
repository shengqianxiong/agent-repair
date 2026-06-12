import request from '@/api/request'

/** 核销审核分页列表 */
export function getVerifyPage(params) {
  return request.get('/admin/verify/page', { params })
}

/** 审核详情 */
export function getVerifyDetail(id) {
  return request.get('/admin/verify/detail', { params: { id } })
}

/** 通过审核 */
export function approveVerify(data) {
  return request.post('/admin/verify/approve', data)
}

/** 拒绝审核 */
export function rejectVerify(data) {
  return request.post('/admin/verify/reject', data)
}
