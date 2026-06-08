import { get, post } from '@/utils/request.js'

export const getDistributionInfo = () => get('/app/distribution/info')
export const getLeaderboard = (params) => get('/app/distribution/invitees', params)
export const withdrawDistribution = (data) => post('/app/distribution/withdraw', data)
