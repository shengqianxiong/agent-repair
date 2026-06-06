import request from '../request'

/** 管理员登录 */
export function adminLogin(data) {
  return request.post('/admin/account/login', data)
}

/** 账号列表（支持账号关键词模糊查询） */
export function getAccountList(params) {
  return request.get('/admin/account/list', { params })
}

/** 账号详情 */
export function getAccountDetail(id) {
  return request.get(`/admin/account/detail/${id}`)
}

/** 新增账号 */
export function saveAccount(data) {
  return request.post('/admin/account/save', data)
}

/** 编辑账号（重置密码、修改状态） */
export function updateAccount(data) {
  return request.put('/admin/account/update', data)
}

/** 删除账号 */
export function deleteAccount(id) {
  return request.delete(`/admin/account/delete/${id}`)
}
