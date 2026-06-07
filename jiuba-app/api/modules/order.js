import { get, post, put } from '@/utils/request.js'

export const getTableList = () => get('/app/table/list')
export const submitOrder = (data) => post('/app/order/submit', data)
export const payOrder = (data) => post('/app/order/pay', data)
export const getOrderList = (params) => get('/app/order/list', params)
export const getOrderDetail = (id) => get(`/app/order/detail/${id}`)
export const cancelOrder = (id) => put(`/app/order/cancel/${id}`)
export const reorder = (id) => post(`/app/order/reorder/${id}`)
