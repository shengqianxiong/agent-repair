import { get, post } from '@/utils/request.js'

export const getSquareFeed = (params) => get('/app/square/list', params)
export const likeSquarePost = (data) => post('/app/square/like', data)
export const createSquarePost = (data) => post('/app/square/create', data)
export const getSquareCategories = () => get('/app/square/categories')
export const getSocialProfile = (userId) => get('/app/dynamic/user_list', { userId })
export const createChat = (data) => post('/app/chat/create', data)
