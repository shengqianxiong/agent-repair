import { get, post } from '@/utils/request.js'

const APP_PREFIX = '/sqx_fast/app'

/**
 * 用户端登录（不携带 token）
 */
export function appLogin(payload) {
  return post(`${APP_PREFIX}/user/login`, payload, { requireToken: false })
}

export function fetchHomeData() {
  return get(`${APP_PREFIX}/recycle/home`)
}

export function createReserveOrder(payload) {
  return post(`${APP_PREFIX}/recycle/order/create`, payload)
}

export function fetchOrderList(params) {
  return get(`${APP_PREFIX}/recycle/order/list`, params)
}

export function cancelOrder(payload) {
  return post(`${APP_PREFIX}/recycle/order/cancel`, payload)
}

export function fetchRewardSummary() {
  return get(`${APP_PREFIX}/reward/summary`)
}

export function fetchRewardLogs(params) {
  return get(`${APP_PREFIX}/reward/logs`, params)
}

export function fetchGiftList(params) {
  return get(`${APP_PREFIX}/reward/gift/list`, params)
}

export function redeemGift(payload) {
  return post(`${APP_PREFIX}/reward/gift/redeem`, payload)
}

export function fetchCertificate() {
  return get(`${APP_PREFIX}/public-welfare/certificate/latest`)
}
