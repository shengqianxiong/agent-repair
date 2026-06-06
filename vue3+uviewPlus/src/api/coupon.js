import { get, post } from '@/utils/request'

export const verifyCoupon = (data) => post('/app/coupon/verify', data)
export const getCouponList = (params) => get('/app/coupon/list', params)
