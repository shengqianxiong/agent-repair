import { get } from '@/utils/request'

export const getRankList = (params) => get('/app/rank/list', params)
export const getMyRank = (params) => get('/app/rank/my', params)
