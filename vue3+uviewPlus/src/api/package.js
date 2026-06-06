import { get } from '@/utils/request'

export const getPackageList = (params) => get('/app/package/list', params)
export const getPackageDetail = (id) => get(`/app/package/detail/${id}`)
