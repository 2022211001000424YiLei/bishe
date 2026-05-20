import axios from 'axios'

const api = axios.create({
  baseURL: '/api'
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getCurrentUser: () => api.get('/auth/me'),
  updateProfile: (data) => api.put('/auth/profile', data)
}

export const foodAPI = {
  getAll: (page = 0, size = 10) => api.get(`/foods?page=${page}&size=${size}`),
  getById: (id) => api.get(`/foods/${id}`),
  create: (data) => api.post('/foods', data),
  delete: (id) => api.delete(`/foods/${id}`),
  search: (keyword, page = 0, size = 10) => api.get(`/foods/search?keyword=${keyword}&page=${page}&size=${size}`),
  getByCategory: (category, page = 0, size = 10) => api.get(`/foods/category/${category}?page=${page}&size=${size}`),
  getByTag: (tag, page = 0, size = 10) => api.get(`/foods/tag/${tag}?page=${page}&size=${size}`),
  getUserFoods: (userId, page = 0, size = 10) => api.get(`/foods/user/${userId}?page=${page}&size=${size}`),
  getTop: (limit = 10) => api.get(`/foods/top?limit=${limit}`)
}

export const commentAPI = {
  getByFood: (foodId, page = 0, size = 10) => api.get(`/foods/${foodId}/comments?page=${page}&size=${size}`),
  add: (foodId, data) => api.post(`/foods/${foodId}/comments`, data)
}

export const likeAPI = {
  toggle: (foodId) => api.post(`/foods/${foodId}/like`)
}

export const favoriteAPI = {
  toggle: (foodId) => api.post(`/favorites/${foodId}`),
  getAll: (page = 0, size = 10) => api.get(`/favorites?page=${page}&size=${size}`)
}

export const uploadAPI = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const adminAPI = {
  getUsers: (page = 0, size = 10) => api.get(`/admin/users?page=${page}&size=${size}`),
  deleteUser: (userId) => api.delete(`/admin/users/${userId}`),
  updateUserRole: (userId, role) => api.put(`/admin/users/${userId}/role`, { role }),
  getAllFoods: (page = 0, size = 10) => api.get(`/admin/foods?page=${page}&size=${size}`),
  deleteFood: (foodId) => api.delete(`/admin/foods/${foodId}`),
  getPendingFoods: (page = 0, size = 10) => api.get(`/admin/foods/pending?page=${page}&size=${size}`),
  approveFood: (foodId) => api.post(`/admin/foods/${foodId}/approve`),
  rejectFood: (foodId) => api.post(`/admin/foods/${foodId}/reject`),
  getStatistics: () => api.get('/admin/statistics'),
  getComments: (page = 0, size = 20) => api.get(`/admin/comments?page=${page}&size=${size}`),
  deleteComment: (commentId) => api.delete(`/admin/comments/${commentId}`),
  getMerchants: () => api.get('/admin/merchants'),
  deleteShop: (shopId) => api.delete(`/admin/shops/${shopId}`)
}

export const recommendationAPI = {
  chat: (data) => api.post('/recommendations/chat', data)
}

export const shopAPI = {
  getMyShop: () => api.get('/shops/my'),
  getAll: () => api.get('/shops'),
  createShop: (data) => api.post('/shops', data),
  updateShop: (id, data) => api.put(`/shops/${id}`, data)
}

export default api
