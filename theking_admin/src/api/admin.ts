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

// ========== CMS 健身内容管理 ==========

export function getExerciseList(params: { page?: number; size?: number }) {
  return request.get('/admin/cms/exercises', { params })
}

export function getExerciseDetail(id: number | string) {
  return request.get(`/admin/cms/exercises/${id}`)
}

export function createExercise(data: any) {
  return request.post('/admin/cms/exercises', data)
}

export function updateExercise(id: number | string, data: any) {
  return request.put(`/admin/cms/exercises/${id}`, data)
}

export function deleteExercise(id: number | string) {
  return request.delete(`/admin/cms/exercises/${id}`)
}

export function toggleExerciseStatus(id: number | string) {
  return request.post(`/admin/cms/exercises/${id}/toggle`)
}

export function getPlanList(params: { page?: number; size?: number }) {
  return request.get('/admin/cms/plans', { params })
}

export function getPlanDetail(id: number | string) {
  return request.get(`/admin/cms/plans/${id}`)
}

export function createPlan(data: any) {
  return request.post('/admin/cms/plans', data)
}

export function updatePlan(id: number | string, data: any) {
  return request.put(`/admin/cms/plans/${id}`, data)
}

export function deletePlan(id: number | string) {
  return request.delete(`/admin/cms/plans/${id}`)
}

export function togglePlanStatus(id: number | string) {
  return request.post(`/admin/cms/plans/${id}/toggle`)
}

export function getPlanExercises(planId: number | string) {
  return request.get(`/admin/cms/plans/${planId}/exercises`)
}

export function setPlanExercises(planId: number | string, data: any[]) {
  return request.post(`/admin/cms/plans/${planId}/exercises`, data)
}

export function getCmsStats() {
  return request.get('/admin/cms/stats')
}

// ========== Dashboard 数据可视化 ==========

export function getDashboardOverview() {
  return request.get('/admin/dashboard/overview')
}

export function getUserGrowthTrend(days: number = 30) {
  return request.get('/admin/dashboard/user-growth', { params: { days } })
}

export function getDailyActiveUsers(days: number = 30) {
  return request.get('/admin/dashboard/dau', { params: { days } })
}

export function getActivityDistribution() {
  return request.get('/admin/dashboard/activity-distribution')
}

export function getContentStats() {
  return request.get('/admin/dashboard/content-stats')
}

// ========== Operation Logs 操作日志 ==========

export function getLogList(params: { page?: number; size?: number }) {
  return request.get('/admin/logs/list', { params })
}

export function getMyLogs(params: { page?: number; size?: number }) {
  return request.get('/admin/logs/my', { params })
}
