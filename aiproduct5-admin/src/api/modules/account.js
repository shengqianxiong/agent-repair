import request from '@/api/request'

/**
 * 账号分页列表
 */
export function getAccountList(params) {
  return request({
    url: '/admin/account/list',
    method: 'get',
    params
  })
}

/**
 * 账号详情
 */
export function getAccountDetail(id) {
  return request({
    url: `/admin/account/detail/${id}`,
    method: 'get'
  })
}

/**
 * 新增账号
 */
export function saveAccount(data) {
  return request({
    url: '/admin/account/save',
    method: 'post',
    data
  })
}

/**
 * 编辑账号
 */
export function updateAccount(data) {
  return request({
    url: '/admin/account/update',
    method: 'put',
    data
  })
}

/**
 * 删除账号
 */
export function deleteAccount(id) {
  return request({
    url: `/admin/account/delete/${id}`,
    method: 'delete'
  })
}
