import request from '../request'

export function getOrderList(params) {
  return request.get('/admin/order/list', { params })
}

export function getOrderDetail(id) {
  return request.get(`/admin/order/detail/${id}`)
}

export function updateOrderStatus(data) {
  return request.put('/admin/order/status', data)
}

export function refundOrder(id) {
  return request.post(`/admin/order/refund/${id}`)
}
