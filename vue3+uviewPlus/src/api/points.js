import { get, post } from '@/utils/request'

export const getPointsBalance = () => get('/app/points/balance')
export const getPointsRecords = (params) => get('/app/points/records', params)
export const getRedeemList = (params) => get('/app/points/redeem/list', params)
export const redeemPoints = (data) => post('/app/points/redeem', data)
