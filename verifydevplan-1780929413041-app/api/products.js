import { get } from '@/utils/request.js'

/** 商品列表 GET /app/products */
export function getProductList(params) {
  return get('/app/products', params)
}

/** 商品详情 GET /app/products/:id */
export function getProductDetail(id) {
  return get(`/app/products/${id}`)
}
