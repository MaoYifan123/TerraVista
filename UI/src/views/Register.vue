<template>
  <div class="register-container">
    <div class="register-card">
      <h2>注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-item">
          <div class="input-wrapper">
            <i class="el-icon-user"></i>
            <input
              type="text"
              v-model="registerForm.username"
              placeholder="用户名"
              maxlength="20"
              class="input-field"
            />
          </div>
          <span class="error-message" v-if="errors.username">{{ errors.username }}</span>
        </div>
        <div class="form-item">
          <div class="input-wrapper">
            <i class="el-icon-message"></i>
            <input
              type="email"
              v-model="registerForm.email"
              placeholder="邮箱"
              maxlength="50"
              class="input-field"
            />
          </div>
          <span class="error-message" v-if="errors.email">{{ errors.email }}</span>
        </div>
        <div class="form-item">
          <div class="input-wrapper">
            <i class="el-icon-lock"></i>
            <input
              :type="showPassword ? 'text' : 'password'"
              v-model="registerForm.password"
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
          <div class="input-wrapper">
            <i class="el-icon-lock"></i>
            <input
              :type="showPassword ? 'text' : 'password'"
              v-model="registerForm.confirmPassword"
              placeholder="确认密码"
              maxlength="20"
              class="input-field"
            />
          </div>
          <span class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</span>
        </div>
        <div class="form-item">
          <button 
            type="submit" 
            class="register-button"
            :disabled="loading"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </div>
        <div class="form-item">
          <a href="#" @click.prevent="$router.push('/login')" class="login-link">
            已有账号？立即登录
          </a>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    const registerForm = reactive({
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    })
    const loading = ref(false)
    const showPassword = ref(false)
    const errors = reactive({
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    })

    const validateForm = () => {
      let isValid = true
      errors.username = ''
      errors.email = ''
      errors.password = ''
      errors.confirmPassword = ''

      if (!registerForm.username) {
        errors.username = '请输入用户名'
        isValid = false
      } else if (registerForm.username.length < 3) {
        errors.username = '用户名至少需要3个字符'
        isValid = false
      }

      if (!registerForm.email) {
        errors.email = '请输入邮箱'
        isValid = false
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
        errors.email = '请输入有效的邮箱地址'
        isValid = false
      }

      if (!registerForm.password) {
        errors.password = '请输入密码'
        isValid = false
      } else if (registerForm.password.length < 6) {
        errors.password = '密码至少需要6个字符'
        isValid = false
      }

      if (!registerForm.confirmPassword) {
        errors.confirmPassword = '请确认密码'
        isValid = false
      } else if (registerForm.password !== registerForm.confirmPassword) {
        errors.confirmPassword = '两次输入的密码不一致'
        isValid = false
      }

      return isValid
    }

    const handleRegister = async () => {
      if (!validateForm()) return

      try {
        loading.value = true
        await register({
          username: registerForm.username,
          email: registerForm.email,
          password: registerForm.password
        })
        router.push('/login')
      } catch (error) {
        console.error('注册失败:', error)
        if (error.response?.data?.error) {
          // 显示后端返回的错误信息
          const errorMessage = error.response.data.error
          if (errorMessage.includes('username')) {
            errors.username = errorMessage
          } else if (errorMessage.includes('email')) {
            errors.email = errorMessage
          } else {
            errors.username = errorMessage
          }
        }
      } finally {
        loading.value = false
      }
    }

    return {
      registerForm,
      loading,
      showPassword,
      errors,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
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

.register-button {
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

.register-button:hover {
  background-color: #66b1ff;
}

.register-button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.login-link {
  color: #409EFF;
  text-decoration: none;
  font-size: 14px;
}

.login-link:hover {
  color: #66b1ff;
}
</style> 