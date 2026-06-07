import { get, post } from '@/utils/request.js'

export const getStoredWineList = (params) => get('/app/alcohol/stored/list', params)
export const retrieveWine = (data) => post('/app/alcohol/stored/retrieve', data)
export const renewWine = (data) => post('/app/alcohol/stored/renew', data)
