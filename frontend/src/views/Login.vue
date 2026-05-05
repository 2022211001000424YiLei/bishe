<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-image">
        <img src="https://java-abcde.oss-cn-beijing.aliyuncs.com/e10891d3-a0f8-4fd0-957e-4068e436da3c.jpg" alt="校园美食">
        <div class="login-image-overlay">
          <h2>校园美食分享网</h2>
          <p>分享美味，发现美食</p>
        </div>
      </div>
      <div class="login-form-wrapper">
        <div class="login-form">
          <h3 class="text-center mb-4">登录</h3>
          <form @submit.prevent="handleLogin">
            <div class="mb-3">
              <label class="form-label">用户名</label>
              <input type="text" class="form-control" v-model="form.username" required>
            </div>
            <div class="mb-3">
              <label class="form-label">密码</label>
              <input type="password" class="form-control" v-model="form.password" required>
            </div>
            <div class="alert alert-danger" v-if="error">{{ error }}</div>
            <button type="submit" class="btn btn-primary w-100" :disabled="loading">
              {{ loading ? '登录中...' : '登录' }}
            </button>
          </form>
          <div class="text-center mt-3">
            <router-link to="/register">注册新账号</router-link>
          </div>
          <hr>
          <button type="button" class="btn btn-outline-secondary w-100" @click="visitorLogin">
            游客浏览
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api'
import { useAuth } from '../store/auth'

const router = useRouter()
const { setAuth } = useAuth()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await authAPI.login(form.value)
    setAuth(res.data)
    router.push('/home')
  } catch (err) {
    error.value = err.response?.data?.message || '登录失败'
  } finally {
    loading.value = false
  }
}

const visitorLogin = () => {
  setAuth({
    token: 'visitor',
    username: '游客',
    role: 'VISITOR',
    userId: 0
  })
  router.push('/home')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(rgba(255,255,255,0.35), rgba(255,255,255,0.35)), url('/images/bg2.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.login-card {
  display: flex;
  width: 800px;
  max-width: 100%;
  height: 500px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.login-image {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.login-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login-image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30px;
  background: linear-gradient(transparent, rgba(0,0,0,0.8));
  color: white;
}

.login-image-overlay h2 {
  font-size: 1.8rem;
  margin-bottom: 8px;
}

.login-image-overlay p {
  margin: 0;
  opacity: 0.9;
}

.login-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-form {
  width: 100%;
  max-width: 300px;
}

.login-form h3 {
  color: #333;
  font-weight: 600;
}

@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    height: auto;
  }

  .login-image {
    height: 200px;
  }
}
</style>
