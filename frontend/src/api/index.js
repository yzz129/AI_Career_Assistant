import request from '../utils/request'

export const login = (data) => request.post('/auth/login', data)
export const register = (data) => request.post('/auth/register', data)
export const me = () => request.get('/auth/me')
export const dashboardOverview = () => request.get('/dashboard/overview')

export const pageApi = (base, params) => request.get(`${base}/page`, { params })
export const createApi = (base, data) => request.post(base, data)
export const updateApi = (base, id, data) => request.put(`${base}/${id}`, data)
export const deleteApi = (base, id) => request.delete(`${base}/${id}`)

export const submitApply = (data) => request.post('/apply/submit', data)
export const reviewApply = (data) => request.post('/apply/review', data)
export const myApplies = (params) => request.get('/apply/my', { params })
export const todoApplies = (params) => request.get('/apply/todo', { params })

export const saveLog = (data) => request.post('/log/save', data)
export const commentLog = (data) => request.post('/log/comment', data)
export const myLogs = (params) => request.get('/log/my', { params })
export const todoLogs = (params) => request.get('/log/todo', { params })

export const saveResume = (data) => request.post('/resume/save', data)
export const myResume = () => request.get('/resume/my')
