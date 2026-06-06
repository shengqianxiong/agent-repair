import { get, put } from '@/utils/request'

export const getNotificationList = (params) => get('/app/notification/list', params)
export const markNotificationRead = (id) => put(`/app/notification/read/${id}`)
export const getUnreadCount = () => get('/app/notification/unread-count')
