import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器：携带 sessionId
api.interceptors.request.use(config => {
  const sessionId = localStorage.getItem('sessionId')
  if (sessionId) {
    config.headers['X-Session-Id'] = sessionId
  }
  return config
})

// 响应拦截器：统一处理错误
api.interceptors.response.use(
  res => res.data,
  err => {
    const msg = err.response?.data?.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

export default {
  // 用户
  login(phone, password) { return api.post('/user/login', { phone, password }) },

  // 景点管理
  getScenics(city, keyword) {
    return api.get('/explore/search', { params: { cityCode: city, keyword } })
  },
  addScenic(data) {
    return api.post('/admin/scenic', data)
  },
  deleteScenic(id) {
    return api.delete(`/admin/scenic/${id}`)
  },

  // 同步高德数据
  syncScenic(city) {
    return api.get('/common/sync-scenic', { params: { city } })
  },

  // 帖子管理
  getPosts(page = 1, pageSize = 10) {
    return api.get('/post/list', { params: { page, pageSize } })
  },
  deletePost(id) {
    return api.delete(`/admin/post/${id}`)
  },

  // 数据概览
  getDashboard() {
    return api.get('/admin/dashboard')
  },

  // 榜单配置
  getRankings(city) {
    return api.get('/explore/rankings', { params: { city } })
  }
}
