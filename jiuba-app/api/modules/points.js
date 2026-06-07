import { get, post } from '@/utils/request.js'

export const getPointsBalance = () => get('/app/points/balance')
export const getPointsRecords = (params) => get('/app/points/records', params)
export const getMallProductList = (params) => get('/app/points/mall/list', params)
export const redeemPoints = (data) => post('/app/points/redeem', data)
export const checkinPoints = () => post('/app/points/checkin')
