import { get, post, put, del } from '@/utils/request'

export function getCategoryList() {
  return get('/product/category/list')
}

export function getProductList(params) {
  return get('/product/list', params)
}

export function getProductDetail(id) {
  return get(`/product/detail/${id}`)
}

export function getPackageList(params) {
  return get('/package/list', params)
}

export function getPackageDetail(id) {
  return get(`/package/detail/${id}`)
}

export function getCartList() {
  return get('/cart/list')
}

export function addToCart(data) {
  return post('/cart/add', data, { showLoading: true })
}

export function updateCart(data) {
  return put('/cart/update', data)
}

export function removeFromCart(id) {
  return del(`/cart/remove/${id}`)
}

export function getTableList() {
  return get('/table/list')
}

export function submitOrder(data) {
  return post('/order/submit', data, { showLoading: true, loadingText: '提交中...' })
}

export function payOrder(data) {
  return post('/order/pay', data, { showLoading: true, loadingText: '支付中...' })
}

export function getOrderList(params) {
  return get('/order/list', params)
}

export function getOrderDetail(id) {
  return get(`/order/detail/${id}`)
}

export function cancelOrder(id) {
  return put(`/order/cancel/${id}`)
}
