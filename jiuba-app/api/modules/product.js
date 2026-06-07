import { get, post } from '@/utils/request.js'

export const getCategoryList = () => get('/app/category/list')
export const getProductList = (params) => get('/app/product/list', params)
export const getProductDetail = (id) => get(`/app/product/detail/${id}`)
export const getCartSummary = () => get('/app/cart/summary')
export const addToCart = (data) => post('/app/cart/add', data)

/** 兼容页面已有命名 */
export const getCartList = getCartSummary
