import request from '@/utils/request'

export function getPackageList(params) {
  return request.get('/admin/package/list', { params })
}

export function savePackage(data) {
  return request.post('/admin/package/save', data)
}

export function updatePackage(data) {
  return request.put('/admin/package/update', data)
}

export function deletePackage(id) {
  return request.delete(`/admin/package/delete/${id}`)
}
