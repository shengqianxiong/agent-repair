import { get, post } from '@/utils/request'

export const getGameList = () => get('/app/game/list')
export const submitGameScore = (data) => post('/app/game/play', data)
