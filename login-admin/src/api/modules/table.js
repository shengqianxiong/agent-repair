import request from '../request'

export function getTableList(params) {
  return request.get('/admin/table/list', { params })
}

export function saveTable(data) {
  return request.post('/admin/table/save', data)
}

export function updateTable(data) {
  return request.put('/admin/table/update', data)
}

export function updateTableStatus(data) {
  return request.put('/admin/table/status', data)
}
