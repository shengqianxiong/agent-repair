import { get, post, del } from '@/utils/request.js'

export const getTableInteraction = (params) => get('/app/table/interaction', params)
export const takeSeat = (data) => post('/app/table/seat', data)
export const leaveSeat = (data) => del('/app/table/seat', data)
