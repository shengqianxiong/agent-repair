import config from '@/config/index.js'
import { get } from '@/utils/request.js'

/** 获取当前登录用户详情 */
export function fetchAccountDetail() {
  return get(`${config.appPrefix}/account/detail`)
}

/** 获取当前登录用户列表（仅本人） */
export function fetchAccountList() {
  return get(`${config.appPrefix}/account/list`)
}
