import request from '../request'

export function getProductList(params) {
  return request.get('/admin/product/list', { params })
}

export function getProductDetail(id) {
  return request.get(`/admin/product/detail/${id}`)
}

export function saveProduct(data) {
  return request.post('/admin/product/save', data)
}

export function updateProduct(data) {
  return request.put('/admin/product/update', data)
}

export function deleteProduct(id) {
  return request.delete(`/admin/product/delete/${id}`)
}

export function getCategoryList() {
  return request.get('/admin/product/category/list')
}

export function saveCategory(data) {
  return request.post('/admin/product/category/save', data)
}
