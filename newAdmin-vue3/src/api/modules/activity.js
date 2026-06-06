import request from '@/utils/request'

export function getActivityList(params) {
  return request.get('/admin/activity/list', { params })
}

export function saveActivity(data) {
  return request.post('/admin/activity/save', data)
}

export function updateActivity(data) {
  return request.put('/admin/activity/update', data)
}

export function deleteActivity(id) {
  return request.delete(`/admin/activity/delete/${id}`)
}

export function updateActivityStatus(id, status) {
  return request.put(`/admin/activity/status/${id}`, null, { params: { status } })
}
