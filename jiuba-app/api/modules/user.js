import { get, post } from '@/utils/request.js'

export const userLogin = (data) => post('/app/user/login', data, { skipAuth: true })
export const userRegister = (data) => post('/app/user/register', data, { skipAuth: true })
export const getUserInfo = () => get('/app/user/info')
export const getUserProfile = () => get('/app/user/profile')
export const updateUserProfile = (data) => post('/app/user/update', data)
export const uploadImage = (data) => post('/app/common/upload', data)
