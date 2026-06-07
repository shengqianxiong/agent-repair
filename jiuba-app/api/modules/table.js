import { get, post } from '@/utils/request.js'

export const getTableInteraction = (params) => get('/app/table/detail', params)
export const takeSeat = (data) => post('/app/table/seat/join', data)
export const leaveSeat = (data) => post('/app/table/seat/leave', data)
