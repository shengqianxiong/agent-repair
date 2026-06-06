import request from '@/utils/request'

export function getBookingList(params) {
  return request.get('/admin/booking/list', { params })
}

export function getBookingDetail(id) {
  return request.get(`/admin/booking/detail/${id}`)
}

export function confirmBooking(id) {
  return request.put(`/admin/booking/confirm/${id}`)
}

export function cancelBooking(id) {
  return request.put(`/admin/booking/cancel/${id}`)
}

export function assignTable(data) {
  return request.put('/admin/booking/assign-table', data)
}
