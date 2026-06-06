import { get, post } from '@/utils/request'

export const login = (data) => post('/app/user/login', data)
export const getUserInfo = () => get('/app/user/info')
