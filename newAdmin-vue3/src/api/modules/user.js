import request from '../request'

export function adminLogin(data) {
  return request.post('/admin/user/login', data)
}

export function getUserList(params) {
  return request.get('/admin/user/list', { params })
}

export function getUserDetail(id) {
  return request.get(`/admin/user/detail/${id}`)
}

export function adjustUserPoints(data) {
  return request.put('/admin/user/points/adjust', data)
}

export function adjustUserLevel(data) {
  return request.put('/admin/user/level/adjust', data)
}
