import { get, post } from '@/utils/request.js'

export const getOrderPreview = (data) => post('/app/order/preview', data)
export const submitOrder = (data) => post('/app/order/submit', data)
export const createOrder = (data) => post('/app/order/create', data)
export const getOrderList = (params) => get('/app/order/list', params)
export const getOrderDetail = (id) => get(`/app/order/detail/${id}`)
export const reorder = (data) => post('/app/order/reorder', data)
export const getPickupCode = (id) => get(`/app/order/pickup-code/${id}`)
export const cancelOrder = (data) => post('/app/order/cancel', data)
export const getPaymentList = () => get('/app/payment/list')
