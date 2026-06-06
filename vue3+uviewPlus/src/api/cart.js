import { get, post, put, del } from '@/utils/request'

export const getCartList = () => get('/app/cart/list')
export const addCart = (data) => post('/app/cart/add', data)
export const updateCart = (data) => put('/app/cart/update', data)
export const removeCart = (id) => del(`/app/cart/remove/${id}`)
