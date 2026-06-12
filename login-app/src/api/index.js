import { get, post } from '@/utils/request.js'

/** 用户登录 */
export function userLogin(data) {
  return post('/app/user/login', data)
}

/** 首页 */
export function getHomeIndex() {
  return get('/app/home/index')
}

export function getUnreadCount() {
  return get('/app/notification/unread-count')
}

export function getUserInfo() {
  return get('/app/user/info')
}

/** 积分 */
export function getPointsBalance() {
  return get('/app/points/balance')
}

export function getPointsRecords(params) {
  return get('/app/points/records', params)
}

export function getRedeemList(params) {
  return get('/app/points/redeem-list', params)
}

export function redeemPoints(data) {
  return post('/app/points/redeem', data)
}

/** 商品与购物车 */
export function getCategoryList() {
  return get('/app/product/categories')
}

export function getProductList(params) {
  return get('/app/product/list', params)
}

export function getProductDetail(id) {
  return get('/app/product/detail', { id })
}

export function getPackageList(params) {
  return get('/app/package/list', params)
}

export function addToCart(data) {
  return post('/app/cart/add', data)
}

export function getCartList() {
  return get('/app/cart/list')
}

export function updateCart(data) {
  return post('/app/cart/update', data)
}

export function removeFromCart(id) {
  return post('/app/cart/remove', { id })
}

/** 订单 */
export function getOrderList(params) {
  return get('/app/order/list', params)
}

export function getOrderDetail(id) {
  return get('/app/order/detail', { id })
}

export function getTableList() {
  return get('/app/order/tables')
}

export function submitOrder(data) {
  return post('/app/order/submit', data)
}

export function cancelOrder(id) {
  return post('/app/order/cancel', { id })
}

export function payOrder(data) {
  return post('/app/order/pay', data)
}

/** 预约 */
export function getBookingList(params) {
  return get('/app/booking/list', params)
}

export function getBookingDetail(id) {
  return get('/app/booking/detail', { id })
}

export function getBookingTimeSlots(params) {
  return get('/app/booking/time-slots', params)
}

export function submitBooking(data) {
  return post('/app/booking/submit', data)
}

export function cancelBooking(id) {
  return post('/app/booking/cancel', { id })
}

/** 活动 */
export function getActivityDetail(id) {
  return get('/app/activity/detail', { id })
}

export function buyActivity(id) {
  return post('/app/activity/buy', { activityId: id })
}

/** 会员 */
export function getMemberInfo() {
  return get('/app/member/info')
}

/** 优惠券 */
export function verifyCoupon(data) {
  return post('/app/coupon/verify', data)
}

export function getCouponList(params) {
  return get('/app/coupon/list', params)
}

/** 搭子 */
export function getNearbyPartners(params) {
  return get('/app/partner/nearby', params)
}

export function getPartnerDetail(id) {
  return get('/app/partner/detail', { id })
}

export function invitePartner(id) {
  return post('/app/partner/invite', { partnerId: id })
}

/** 聊天 */
export function getChatMessages(partnerId) {
  return get('/app/chat/messages', { partnerId })
}

export function sendChatMessage(data) {
  return post('/app/chat/send', data)
}

/** 游戏 */
export function getGameList() {
  return get('/app/game/list')
}

export function submitGameScore(data) {
  return post('/app/game/submit', data)
}

/** 排行榜 */
export function getRankList(params) {
  return get('/app/rank/list', params)
}

export function getMyRank(params) {
  return get('/app/rank/my', params)
}

/** 通知 */
export function getNotificationList(params) {
  return get('/app/notification/list', params)
}

export function markNotificationRead(id) {
  return post('/app/notification/read', { id })
}
