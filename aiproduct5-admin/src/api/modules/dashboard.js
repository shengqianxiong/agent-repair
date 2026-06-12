import request from '@/api/request'

/** 工作台统计 */
export function getDashboardStats(params) {
  return request.get('/admin/seller/dashboard', { params })
}

/** 最近活动概览 */
export function getRecentActivities(params) {
  return request.get('/admin/seller/dashboard/recent-activities', { params })
}

/** 最近审核记录 */
export function getRecentVerifies(params) {
  return request.get('/admin/seller/dashboard/recent-verifies', { params })
}
