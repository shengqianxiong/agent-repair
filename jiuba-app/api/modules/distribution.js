import { get } from '@/utils/request.js'

export const getDistributionInfo = () => get('/app/distribution/info')
export const getLeaderboard = (params) => get('/app/distribution/leaderboard', params)
