import request from '../request'

export function getRankConfig() {
  return request.get('/admin/rank/config')
}

export function updateRankConfig(data) {
  return request.put('/admin/rank/config', data)
}
