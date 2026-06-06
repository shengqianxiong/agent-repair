import request from '../request'

/**
 * 分页查询账号列表
 * @param {{ account?: string, page: number, pageSize: number }} params
 */
export function getAccountList(params) {
  return request.get('/admin/account/list', { params })
}

/**
 * 新增账号
 * @param {{ account: string, password: string }} data
 */
export function createAccount(data) {
  return request.post('/admin/account', data)
}

/**
 * 编辑账号（重置密码、修改状态）
 * @param {{ id: number, password?: string, status: number }} data
 */
export function updateAccount(data) {
  return request.put('/admin/account', data)
}

/**
 * 删除账号
 * @param {number} id
 */
export function deleteAccount(id) {
  return request.delete(`/admin/account/${id}`)
}
