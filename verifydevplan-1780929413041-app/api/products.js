import { get } from '@/utils/request.js'

/** 商品列表 GET /app/products */
export const getProductList = (params) => get('/app/products', params)

/** 商品详情 GET /app/products/:id */
export const getProductDetail = (id) => get(`/app/products/${id}`)
