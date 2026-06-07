import { get } from '@/utils/request.js'

export const getHomeIndex = () => get('/app/home/index')
export const getUnreadCount = () => get('/app/notification/unread-count')
export const getNotificationList = (params) => get('/app/notification/list', params)
export const getActivityList = (params) => get('/app/activity/list', params)
export const getActivityDetail = (id) => get(`/app/activity/detail/${id}`)
