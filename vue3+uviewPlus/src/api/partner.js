import { get, post } from '@/utils/request'

export const getNearbyPartners = (params) => get('/app/partner/nearby', params)
export const getPartnerDetail = (id) => get(`/app/partner/detail/${id}`)
export const invitePartner = (id) => post(`/app/partner/invite/${id}`)
