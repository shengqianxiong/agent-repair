import { get, post } from '@/utils/request'

export const getActivityList = (params) => get('/app/activity/list', params)
export const getActivityDetail = (id) => get(`/app/activity/detail/${id}`)
export const buyActivity = (id) => post(`/app/activity/buy/${id}`)
