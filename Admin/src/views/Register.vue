<template>
  <div class="register-container">
    <div class="register-form-container">
      <div class="register-box">
        <div class="logo">
          <h1>TerraVista</h1>
          <p class="slogan">探索世界，发现美好</p>
        </div>
        <h2>注册</h2>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="username">用户名</label>
            <div class="input-with-icon">
              <span class="icon">👤</span>
              <input
                type="text"
                id="username"
                v-model="form.username"
                required
                placeholder="请输入用户名"
              />
            </div>
          </div>
          <div class="form-group">
            <label for="email">邮箱</label>
            <div class="input-with-icon">
              <span class="icon">📧</span>
              <input
                type="email"
                id="email"
                v-model="form.email"
                required
                placeholder="请输入邮箱"
              />
            </div>
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <div class="input-with-icon">
              <span class="icon">🔒</span>
              <input
                type="password"
                id="password"
                v-model="form.password"
                required
                placeholder="请输入密码"
                minlength="6"
              />
              <small class="password-hint">密码长度至少6位</small>
            </div>
          </div>
          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <div class="input-with-icon">
              <span class="icon">🔐</span>
              <input
                type="password"
                id="confirmPassword"
                v-model="form.confirmPassword"
                required
                placeholder="请再次输入密码"
              />
            </div>
          </div>
          <div class="error-message" v-if="error">{{ error }}</div>
          <button type="submit" :disabled="loading || !isFormValid">
            {{ loading ? '注册中...' : '注册' }}
          </button>
          <div class="login-link">
            已有账号？ <router-link to="/login">立即登录</router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const error = ref('')
    const form = reactive({
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    })

    const isFormValid = computed(() => {
      return (
        form.username &&
        form.email &&
        form.password &&
        form.confirmPassword &&
        form.password === form.confirmPassword &&
        form.password.length >= 6
      )
    })

    const handleRegister = async () => {
      if (!isFormValid.value) {
        error.value = '请检查表单填写是否正确'
        return
      }

      try {
        loading.value = true
        error.value = ''
        const { confirmPassword, ...registerData } = form
        await axios.post('/api/user/register', registerData)
        router.push('/login')
      } catch (err) {
        error.value = err.response?.data?.error || '注册失败，请重试'
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      error,
      isFormValid,
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
  min-height: calc(100vh - 140px);
  background-image: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('https://source.unsplash.com/1600x900/?travel,mountain');
  background-size: cover;
  background-position: center;
}

.register-form-container {
  width: 100%;
  max-width: 450px;
  animation: fadeIn 0.8s;
}

.register-box {
  background: rgba(255, 255, 255, 0.9);
  padding: 2.5rem;
  border-radius: 10px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.logo {
  text-align: center;
  margin-bottom: 1.5rem;
}

.logo h1 {
  color: #2c3e50;
  margin: 0;
  font-size: 2rem;
  font-weight: 700;
}

.slogan {
  color: #42b983;
  font-style: italic;
  margin-top: 0.5rem;
}

h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  font-weight: 600;
  position: relative;
}

h2:after {
  content: '';
  position: absolute;
  width: 50px;
  height: 3px;
  background: #42b983;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: 500;
}

.input-with-icon {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.icon {
  position: absolute;
  left: 10px;
  top: 12px;
  font-size: 1.2rem;
}

input {
  width: 100%;
  padding: 0.75rem 0.75rem 0.75rem 2.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: all 0.3s;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.25);
}

.password-hint {
  display: block;
  color: #666;
  font-size: 0.875rem;
  margin-top: 0.25rem;
  margin-left: 2.5rem;
}

button {
  width: 100%;
  padding: 0.85rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 5px rgba(66, 185, 131, 0.3);
}

button:hover {
  background-color: #3aa876;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(66, 185, 131, 0.4);
}

button:disabled {
  background-color: #a8d5c2;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.error-message {
  color: #dc3545;
  margin-bottom: 1rem;
  text-align: center;
  background: rgba(220, 53, 69, 0.1);
  padding: 0.5rem;
  border-radius: 4px;
  border-left: 3px solid #dc3545;
}

.login-link {
  text-align: center;
  margin-top: 1.5rem;
}

.login-link a {
  color: #42b983;
  text-decoration: none;
  font-weight: 600;
}

.login-link a:hover {
  text-decoration: underline;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
