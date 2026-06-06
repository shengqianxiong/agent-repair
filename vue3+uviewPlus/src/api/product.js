import { get } from '@/utils/request'

export const getCategoryList = () => get('/app/product/category/list')
export const getProductList = (params) => get('/app/product/list', params)
export const getProductDetail = (id) => get(`/app/product/detail/${id}`)
