import { get, post, put } from '@/utils/request'

export function getTimeSlots(params) {
  return get('/booking/time-slots', params)
}

export function submitBooking(data) {
  return post('/booking/submit', data, { showLoading: true, loadingText: '提交预约...' })
}

export function getBookingList(params) {
  return get('/booking/list', params)
}

export function getBookingDetail(id) {
  return get(`/booking/detail/${id}`)
}

export function cancelBooking(id) {
  return put(`/booking/cancel/${id}`)
}
