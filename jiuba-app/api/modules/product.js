import { get, post, put, del } from '@/utils/request.js'

export const getCategoryList = () => get('/app/product/category/list')
export const getProductList = (params) => get('/app/product/list', params)
export const getProductDetail = (id) => get(`/app/product/detail/${id}`)
export const getCartList = () => get('/app/cart/list')
export const addToCart = (data) => post('/app/cart/add', data)
export const updateCart = (data) => put('/app/cart/update', data)
export const removeFromCart = (id) => del(`/app/cart/remove/${id}`)
