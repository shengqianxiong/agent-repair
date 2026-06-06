import { get, post, put } from '@/utils/request'

export const getTimeSlots = (params) => get('/app/booking/time-slots', params)
export const submitBooking = (data) => post('/app/booking/submit', data)
export const getBookingList = (params) => get('/app/booking/list', params)
export const getBookingDetail = (id) => get(`/app/booking/detail/${id}`)
export const cancelBooking = (id) => put(`/app/booking/cancel/${id}`)
