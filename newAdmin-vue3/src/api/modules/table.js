import request from '../request'

export function getTableList(params) {
  return request.get('/admin/table/list', { params })
}

export function saveTable(data) {
  return request.post('/admin/table/save', data)
}

/** 编辑复用 save 接口 */
export function updateTable(data) {
  return saveTable(data)
}

/** 桌台状态更新（预留，待后端落地） */
export function updateTableStatus(data) {
  return request.post('/admin/table/status', data)
}
