import request from '../request'

export function getNotificationList(params) {
  return request.get('/admin/notification/list', { params })
}

export function pushNotification(data) {
  return request.post('/admin/notification/push', data)
}
