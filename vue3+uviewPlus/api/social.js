import { get, post } from '@/utils/request'

export function getPartnerNearby(params) {
  return get('/partner/nearby', params)
}

export function getPartnerDetail(id) {
  return get(`/partner/detail/${id}`)
}

export function invitePartner(id) {
  return post(`/partner/invite/${id}`, {}, { showLoading: true })
}

export function getChatMessages(partnerId) {
  return get(`/chat/messages/${partnerId}`)
}

export function sendChatMessage(data) {
  return post('/chat/send', data)
}

export function getGameList() {
  return get('/game/list')
}

export function submitGameScore(data) {
  return post('/game/play', data, { showLoading: true })
}

export function getRankList(params) {
  return get('/rank/list', params)
}

export function getMyRank(params) {
  return get('/rank/my', params)
}
