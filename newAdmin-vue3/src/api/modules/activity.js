import request from '../request'

export function getActivityList(params) {
  return request.get('/admin/activity/list', { params })
}

export function saveActivity(data) {
  return request.post('/admin/activity/save', data)
}

/** 编辑复用 save 接口 */
export function updateActivity(data) {
  return saveActivity(data)
}

/** 活动状态更新（预留，待后端落地） */
export function updateActivityStatus(id, status) {
  return request.post('/admin/activity/status', { id, status })
}

/** 删除活动（预留，待后端落地） */
export function deleteActivity(id) {
  return request.delete(`/admin/activity/${id}`)
}
