import { get, post } from '@/utils/request.js'

export const getPointsBalance = () => get('/app/points/info')
export const getPointsRecords = (params) => get('/app/points/list', params)
export const getMallProductList = (params) => get('/app/points/products', params)
export const redeemPoints = (data) => post('/app/points/redeem', data)
export const getPointsHistory = (params) => get('/app/points/history', params)
export const checkinPoints = () => post('/app/points/checkin')
