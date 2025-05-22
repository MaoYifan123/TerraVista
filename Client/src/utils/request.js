import axios from 'axios'
import router from '../router'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 5000
})

// Request interceptor
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8'

    // Add token to request header if it exists
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
    }

    return config
}, error => {
    return Promise.reject(error)
})

// Response interceptor
request.interceptors.response.use(
    response => {
        return response
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // Token expired or invalid
                    localStorage.removeItem('token')
                    router.push('/login')
                    break
                case 403:
                    // Permission denied
                    router.push('/')
                    break
                case 429:
                    // Rate limit exceeded
                    ElMessage.error(error.response.data.error || '请求过于频繁，请稍后再试')
                    break
                default:
                    console.error('API Error:', error.response.data)
            }
        }
        return Promise.reject(error)
    }
)

export default request

