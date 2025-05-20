import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userRole: (state) => state.user?.role,
    isAdmin: (state) => state.user?.role === 'ADMIN'
  },

  actions: {
    async login(username, password) {
      try {
        const response = await axios.post('/api/user/login', {
          username,
          password
        })
        if (response.data.token) {
          this.token = response.data.token
          localStorage.setItem('token', this.token)
          axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
          // 获取用户信息
          await this.fetchUserInfo()
          return { success: true }
        }
        return { success: false, error: '登录失败，请重试' }
      } catch (error) {
        console.error('Login failed:', error)
        if (error.response) {
          if (error.response.status === 429) {
            return { success: false, error: '请求过于频繁，请稍后再试' }
          } else if (error.response.status === 401) {
            return { success: false, error: '用户名或密码错误' }
          }
          return { success: false, error: error.response.data?.error || '登录失败，请重试' }
        }
        return { success: false, error: '网络错误，请检查网络连接' }
      }
    },

    async fetchUserInfo() {
      try {
        const response = await axios.get('/api/user/profile')
        this.user = response.data
      } catch (error) {
        console.error('Failed to fetch user info:', error)
        throw error
      }
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      delete axios.defaults.headers.common['Authorization']
    },

    async register(userData) {
      try {
        const response = await axios.post('/api/user/register', userData)
        this.token = response.data.token
        this.user = response.data.user
        localStorage.setItem('token', this.token)
        // 设置axios默认请求头
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
        return true
      } catch (error) {
        console.error('Registration failed:', error)
        return false
      }
    },

    // 检查是否是管理员
    checkIsAdmin() {
      return this.user?.role === 'ADMIN'
    }
  }
}) 