import request from '../request'

export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}

export function getRecentOrders(params) {
  return request.get('/admin/dashboard/recent-orders', { params })
}

export function getRecentBookings(params) {
  return request.get('/admin/dashboard/recent-bookings', { params })
}
