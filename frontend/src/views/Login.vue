<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-image">
        <img src="https://java-abcde.oss-cn-beijing.aliyuncs.com/90.jpg" alt="校园美食">
        <div class="login-image-overlay">
          <h2>校园美食分享网</h2>
          <p>分享美味，发现美食</p>
        </div>
      </div>
      <div class="login-form-wrapper">
        <div class="login-form">
          <h3 class="text-center mb-4">校园美食分享平台</h3>

          <!-- 角色选择标签 -->
          <div class="role-tabs">
            <button
              v-for="r in roles"
              :key="r.value"
              class="role-tab"
              :class="{ active: selectedRole === r.value }"
              @click="selectedRole = r.value"
            >
              <span class="role-icon">{{ r.icon }}</span>
              <span>{{ r.label }}</span>
            </button>
          </div>

          <form @submit.prevent="handleLogin">
            <div class="form-group">
              <label>用户名</label>
              <input type="text" v-model="form.username" placeholder="请输入用户名" required>
            </div>
            <div class="form-group">
              <label>密码</label>
              <input type="password" v-model="form.password" placeholder="请输入密码" required>
            </div>
            <div class="error-msg" v-if="error">{{ error }}</div>
            <button type="submit" class="btn-login" :disabled="loading">
              {{ loading ? '登录中...' : '登 录' }}
            </button>
          </form>

          <div class="login-footer">
            <span>还没有账号？</span>
            <router-link to="/register">立即注册</router-link>
          </div>

          <div class="divider"><span>或</span></div>

          <button class="btn-visitor" @click="visitorLogin">游客浏览</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api'
import { useAuth } from '../store/auth'

const router = useRouter()
const { setAuth } = useAuth()

const roles = [
  { value: 'USER', label: '用户', icon: '👤' },
  { value: 'MERCHANT', label: '商家', icon: '🏪' },
  { value: 'ADMIN', label: '管理员', icon: '⚙️' }
]

const selectedRole = ref('USER')
const form = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await authAPI.login(form)
    const { role } = res.data

    // 验证角色是否匹配
    if (selectedRole.value !== role) {
      const roleNames = { USER: '用户', MERCHANT: '商家', ADMIN: '管理员' }
      error.value = `该账号不是${roleNames[selectedRole.value]}账号`
      return
    }

    setAuth(res.data)

    if (role === 'ADMIN' || role === 'MERCHANT') {
      router.push('/admin')
    } else {
      router.push('/home')
    }
  } catch (err) {
    error.value = err.response?.data?.message || '用户名或密码错误'
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
  background: linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('https://java-abcde.oss-cn-beijing.aliyuncs.com/90.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.login-card {
  display: flex;
  width: 900px;
  max-width: 100%;
  height: 560px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.25);
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
  padding: 40px;
  background: linear-gradient(transparent, rgba(0,0,0,0.85));
  color: white;
}

.login-image-overlay h2 {
  font-size: 28px;
  margin-bottom: 10px;
  font-weight: 600;
}

.login-image-overlay p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.login-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 40px;
}

.login-form {
  width: 100%;
  max-width: 320px;
}

.login-form h3 {
  color: #333;
  font-weight: 600;
  font-size: 22px;
}

/* 角色选择标签 */
.role-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  background: #f5f5f5;
  padding: 6px;
  border-radius: 12px;
}

.role-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 12px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.role-tab:hover {
  color: #333;
}

.role-tab.active {
  background: white;
  color: #667eea;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.role-icon {
  font-size: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.form-group input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.error-msg {
  color: #f44336;
  font-size: 13px;
  margin-bottom: 14px;
  padding: 10px 14px;
  background: #fff1f0;
  border-radius: 8px;
}

.btn-login {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-login:hover {
  opacity: 0.9;
}

.btn-login:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-footer {
  text-align: center;
  margin-top: 18px;
  font-size: 14px;
  color: #666;
}

.login-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.divider {
  text-align: center;
  margin: 18px 0;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e0e0e0;
}

.divider span {
  background: white;
  padding: 0 10px;
  position: relative;
  color: #999;
  font-size: 12px;
}

.btn-visitor {
  width: 100%;
  padding: 10px;
  background: white;
  color: #666;
  border: 1px solid #d9d9d9;
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-visitor:hover {
  color: #667eea;
  border-color: #667eea;
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
