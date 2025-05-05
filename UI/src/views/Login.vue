<template>
  <div class="login-container">
    <div class="login-card">git commit -m
      <h2>登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-item">
          <div class="input-wrapper">
            <i class="el-icon-user"></i>
            <input
              type="text"
              v-model="loginForm.username"
              placeholder="用户名"
              maxlength="20"
              class="input-field"
            />
          </div>
          <span class="error-message" v-if="errors.username">{{ errors.username }}</span>
        </div>
        <div class="form-item">
          <div class="input-wrapper">
            <i class="el-icon-lock"></i>
            <input
              :type="showPassword ? 'text' : 'password'"
              v-model="loginForm.password"
              placeholder="密码"
              maxlength="20"
              class="input-field"
            />
            <i 
              :class="showPassword ? 'el-icon-view' : 'el-icon-hide'"
              @click="showPassword = !showPassword"
              class="password-toggle"
            ></i>
          </div>
          <span class="error-message" v-if="errors.password">{{ errors.password }}</span>
        </div>
        <div class="form-item">
          <button 
            type="submit" 
            class="login-button"
            :disabled="loading"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>
        <div class="form-item">
          <a href="#" @click.prevent="$router.push('/register')" class="register-link">
            还没有账号？立即注册
          </a>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const loginForm = reactive({
      username: '',
      password: ''
    })
    const loading = ref(false)
    const showPassword = ref(false)
    const errors = reactive({
      username: '',
      password: ''
    })

    const validateForm = () => {
      let isValid = true
      errors.username = ''
      errors.password = ''

      if (!loginForm.username) {
        errors.username = '请输入用户名'
        isValid = false
      } else if (loginForm.username.length < 3) {
        errors.username = '用户名至少需要3个字符'
        isValid = false
      }

      if (!loginForm.password) {
        errors.password = '请输入密码'
        isValid = false
      } else if (loginForm.password.length < 6) {
        errors.password = '密码至少需要6个字符'
        isValid = false
      }

      return isValid
    }

    const handleLogin = async () => {
      if (!validateForm()) return

      try {
        loading.value = true
        await login(loginForm.username, loginForm.password)
        router.push('/')
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }

    return {
      loginForm,
      loading,
      showPassword,
      errors,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.form-item {
  margin-bottom: 20px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-field {
  width: 100%;
  padding: 12px 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.input-field:focus {
  outline: none;
  border-color: #409EFF;
}

.input-wrapper i {
  position: absolute;
  left: 12px;
  color: #909399;
}

.password-toggle {
  left: auto !important;
  right: 12px;
  cursor: pointer;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.login-button {
  width: 100%;
  padding: 12px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: #66b1ff;
}

.login-button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.register-link {
  color: #409EFF;
  text-decoration: none;
  font-size: 14px;
}

.register-link:hover {
  color: #66b1ff;
}
</style> 