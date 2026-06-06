import { get, post } from '@/utils/request'

export const getChatMessages = (partnerId) => get(`/app/chat/messages/${partnerId}`)
export const sendChatMessage = (data) => post('/app/chat/send', data)
