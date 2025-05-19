import axios from 'axios'
import router from '../router'

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
                default:
                    console.error('API Error:', error.response.data)
            }
        }
        return Promise.reject(error)
    }
)

export default request

