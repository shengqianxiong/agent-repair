import { get, post, put } from '@/utils/request.js'

export const userLogin = (data) => post('/app/user/login', data, { skipAuth: true })
export const getUserInfo = () => get('/app/user/info')
export const updateUserProfile = (data) => put('/app/user/profile', data)
export const uploadAvatar = (data) => post('/app/user/avatar', data)
