import { get, post, put } from '@/utils/request'

export function getActivityList(params) {
  return get('/activity/list', params)
}

export function getActivityDetail(id) {
  return get(`/activity/detail/${id}`)
}

export function buyActivity(id) {
  return post(`/activity/buy/${id}`, {}, { showLoading: true })
}

export function getMemberInfo() {
  return get('/member/info')
}

export function getPointsBalance() {
  return get('/points/balance')
}

export function getPointsRecords(params) {
  return get('/points/records', params)
}

export function getRedeemList(params) {
  return get('/points/redeem/list', params)
}

export function redeemPoints(data) {
  return post('/points/redeem', data, { showLoading: true })
}

export function verifyCoupon(data) {
  return post('/coupon/verify', data, { showLoading: true })
}

export function getCouponList(params) {
  return get('/coupon/list', params)
}

export function getNotificationList(params) {
  return get('/notification/list', params)
}

export function markNotificationRead(id) {
  return put(`/notification/read/${id}`)
}

export function getUnreadCount() {
  return get('/notification/unread-count')
}
