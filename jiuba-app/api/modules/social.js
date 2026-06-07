import { get, post } from '@/utils/request.js'

export const getSquareFeed = (params) => get('/app/social/feed', params)
export const getSocialProfile = (id) => get(`/app/social/profile/${id}`)
export const sendChatMessage = (data) => post('/app/chat/send', data)
