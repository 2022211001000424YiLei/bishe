import { reactive } from 'vue'

const state = reactive({
  token: localStorage.getItem('token') || '',
  username: localStorage.getItem('username') || '',
  role: localStorage.getItem('role') || '',
  userId: localStorage.getItem('userId') || ''
})

export function useAuth() {
  const setAuth = (data) => {
    state.token = data.token
    state.username = data.username
    state.role = data.role
    state.userId = data.userId
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
    localStorage.setItem('userId', data.userId)
  }

  const clearAuth = () => {
    state.token = ''
    state.username = ''
    state.role = ''
    state.userId = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
  }

  return { state, setAuth, clearAuth }
}
