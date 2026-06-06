import request from '@/utils/request'

export function getCouponList(params) {
  return request.get('/admin/coupon/list', { params })
}

export function generateCoupon(data) {
  return request.post('/admin/coupon/generate', data)
}

export function getVerifyRecords(params) {
  return request.get('/admin/coupon/verify-records', { params })
}
