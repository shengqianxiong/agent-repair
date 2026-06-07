import { get, post } from '@/utils/request.js'

export const getStoredWineList = (params) => get('/app/wine/stored/list', params)
export const retrieveWine = (data) => post('/app/wine/retrieve', data)
export const renewWine = (data) => post('/app/wine/renew', data)
