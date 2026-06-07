import { get, post } from '@/utils/request.js'

export const getWalletBalance = () => get('/app/wallet/info')
export const getFundRecords = (params) => get('/app/wallet/bill/list', params)
export const getWithdrawRecords = (params) => get('/app/wallet/withdraw/list', params)
export const getWithdrawAccount = () => get('/app/wallet/account/list')
export const submitWithdraw = (data) => post('/app/wallet/withdraw', data)
export const submitRecharge = (data) => post('/app/wallet/recharge', data)
