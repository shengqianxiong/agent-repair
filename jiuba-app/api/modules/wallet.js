import { get, post } from '@/utils/request.js'

export const getWalletBalance = () => get('/app/wallet/balance')
export const getFundRecords = (params) => get('/app/wallet/fund-records', params)
export const getWithdrawRecords = (params) => get('/app/wallet/withdraw-records', params)
export const getWithdrawAccount = () => get('/app/wallet/withdraw-account')
export const submitWithdraw = (data) => post('/app/wallet/withdraw', data)
export const submitRecharge = (data) => post('/app/wallet/recharge', data)
