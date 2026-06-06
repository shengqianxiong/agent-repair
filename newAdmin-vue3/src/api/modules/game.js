import request from '@/utils/request'

export function getGameList(params) {
  return request.get('/admin/game/list', { params })
}

export function saveGame(data) {
  return request.post('/admin/game/save', data)
}

export function updateGame(data) {
  return request.put('/admin/game/update', data)
}
