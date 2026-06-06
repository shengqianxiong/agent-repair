import { get, post, put, del } from '@/utils/request.js'

/** 首页 */
export const getHomeIndex = () => get('/app/home/index')
export const getActivityList = (params) => get('/app/activity/list', params)

/** 用户 */
export const userLogin = (data) => post('/app/user/login', data)
export const getUserInfo = () => get('/app/user/info')

/** 通知 */
export const getNotificationList = (params) => get('/app/notification/list', params)
export const markNotificationRead = (id) => put(`/app/notification/read/${id}`)
export const getUnreadCount = () => get('/app/notification/unread-count')

/** 商品 */
export const getCategoryList = () => get('/app/product/category/list')
export const getProductList = (params) => get('/app/product/list', params)
export const getProductDetail = (id) => get(`/app/product/detail/${id}`)

/** 套餐 */
export const getPackageList = (params) => get('/app/package/list', params)
export const getPackageDetail = (id) => get(`/app/package/detail/${id}`)

/** 购物车 */
export const getCartList = () => get('/app/cart/list')
export const addToCart = (data) => post('/app/cart/add', data)
export const updateCart = (data) => put('/app/cart/update', data)
export const removeFromCart = (id) => del(`/app/cart/remove/${id}`)

/** 桌位 & 订单 */
export const getTableList = () => get('/app/table/list')
export const submitOrder = (data) => post('/app/order/submit', data)
export const payOrder = (data) => post('/app/order/pay', data)
export const getOrderList = (params) => get('/app/order/list', params)
export const getOrderDetail = (id) => get(`/app/order/detail/${id}`)
export const cancelOrder = (id) => put(`/app/order/cancel/${id}`)

/** 预约 */
export const getBookingTimeSlots = (params) => get('/app/booking/time-slots', params)
export const submitBooking = (data) => post('/app/booking/submit', data)
export const getBookingList = (params) => get('/app/booking/list', params)
export const getBookingDetail = (id) => get(`/app/booking/detail/${id}`)
export const cancelBooking = (id) => put(`/app/booking/cancel/${id}`)

/** 活动 */
export const getActivityDetail = (id) => get(`/app/activity/detail/${id}`)
export const buyActivity = (id) => post(`/app/activity/buy/${id}`)

/** 会员 & 积分 */
export const getMemberInfo = () => get('/app/member/info')
export const getPointsBalance = () => get('/app/points/balance')
export const getPointsRecords = (params) => get('/app/points/records', params)
export const getRedeemList = (params) => get('/app/points/redeem/list', params)
export const redeemPoints = (data) => post('/app/points/redeem', data)

/** 团购券 */
export const verifyCoupon = (data) => post('/app/coupon/verify', data)
export const getCouponList = (params) => get('/app/coupon/list', params)

/** 搭子 & 聊天 */
export const getNearbyPartners = (params) => get('/app/partner/nearby', params)
export const getPartnerDetail = (id) => get(`/app/partner/detail/${id}`)
export const invitePartner = (id) => post(`/app/partner/invite/${id}`)
export const getChatMessages = (partnerId) => get(`/app/chat/messages/${partnerId}`)
export const sendChatMessage = (data) => post('/app/chat/send', data)

/** 游戏 & 排行榜 */
export const getGameList = () => get('/app/game/list')
export const submitGameScore = (data) => post('/app/game/play', data)
export const getRankList = (params) => get('/app/rank/list', params)
export const getMyRank = (params) => get('/app/rank/my', params)
