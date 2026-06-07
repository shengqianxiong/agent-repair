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

/** 编辑复用 save 接口（PRD 统一 POST /admin/product/save） */
export function updateProduct(data) {
  return saveProduct(data)
}

export function deleteProduct(id) {
  return request.delete(`/admin/product/${id}`)
}

export function getCategoryList() {
  return request.get('/admin/category/list')
}

export function saveCategory(data) {
  return request.post('/admin/category/save', data)
}
