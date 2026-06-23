import request from './request'

export function adminLogin(data: { account: string; password: string }) {
  return request.post('/admin/login', data)
}

export function adminLogout() {
  return request.post('/admin/logout', {}, { params: {} })
}

export function getStats() {
  return request.get('/admin/stats')
}

export function getUserList(params: { page?: number; size?: number; keyword?: string }) {
  return request.get('/admin/users', { params })
}

export function getUserDetail(id: number | string) {
  return request.get(`/admin/users/${id}`)
}

export function toggleUserStatus(id: number | string) {
  return request.put(`/admin/users/${id}/status`)
}
