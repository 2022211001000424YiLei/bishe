<template>
  <div class="container py-4">
    <!-- Toast 提示 -->
    <div class="toast-container">
      <div v-if="toast.show" class="toast-message" :class="toast.type">
        {{ toast.message }}
      </div>
    </div>

    <h2 class="mb-4">管理后台</h2>

    <ul class="nav nav-tabs mb-4">
      <li class="nav-item">
        <button class="nav-link" :class="{ active: tab === 'stats' }" @click="tab = 'stats'">
          📊 数据统计
        </button>
      </li>
      <li class="nav-item">
        <button class="nav-link" :class="{ active: tab === 'audit' }" @click="tab = 'audit'">
          审核管理 <span class="badge bg-danger" v-if="pendingCount > 0">{{ pendingCount }}</span>
        </button>
      </li>
      <li class="nav-item">
        <button class="nav-link" :class="{ active: tab === 'users' }" @click="tab = 'users'">用户管理</button>
      </li>
      <li class="nav-item">
        <button class="nav-link" :class="{ active: tab === 'foods' }" @click="tab = 'foods'">美食管理</button>
      </li>
      <li class="nav-item">
        <button class="nav-link" :class="{ active: tab === 'comments' }" @click="tab = 'comments'">评论管理</button>
      </li>
    </ul>

    <!-- 数据统计 -->
    <div v-if="tab === 'stats'">
      <h4 class="mb-4">数据概览</h4>
      <div class="row">
        <div class="col-md-3 mb-3">
          <div class="stat-card stat-users">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
              <h3>{{ stats.userCount || 0 }}</h3>
              <p>用户总数</p>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="stat-card stat-foods">
            <div class="stat-icon">🍜</div>
            <div class="stat-info">
              <h3>{{ stats.foodCount || 0 }}</h3>
              <p>美食总数</p>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="stat-card stat-comments">
            <div class="stat-icon">💬</div>
            <div class="stat-info">
              <h3>{{ stats.commentCount || 0 }}</h3>
              <p>评论总数</p>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="stat-card stat-likes">
            <div class="stat-icon">❤️</div>
            <div class="stat-info">
              <h3>{{ stats.likeCount || 0 }}</h3>
              <p>点赞总数</p>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-4">
        <h5>待审核美食: <span class="text-warning">{{ stats.pendingCount || 0 }}</span></h5>
      </div>
    </div>

    <!-- 审核管理 -->
    <div v-if="tab === 'audit'">
      <h4>待审核美食</h4>
      <div v-if="pendingFoods.content.length === 0" class="text-muted">暂无待审核内容</div>
      <div class="row">
        <div class="col-md-6 mb-3" v-for="food in pendingFoods.content" :key="food.id">
          <div class="card">
            <div class="card-body">
              <h5>{{ food.title }}</h5>
              <p class="text-muted">{{ food.description }}</p>
              <div class="mb-2">
                <span class="badge bg-secondary">{{ food.category }}</span>
                <span class="badge bg-info ms-1">{{ food.location }}</span>
              </div>
              <p class="mb-2">作者: {{ food.username }}</p>
              <div class="d-flex gap-2">
                <button class="btn btn-success btn-sm" @click="approveFood(food.id)">通过</button>
                <button class="btn btn-danger btn-sm" @click="rejectFood(food.id)">拒绝</button>
                <router-link class="btn btn-primary btn-sm" :to="'/food/' + food.id">查看详情</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
      <nav v-if="pendingFoods.totalPages > 1">
        <ul class="pagination">
          <li class="page-item" :class="{ disabled: pendingPage === 0 }">
            <a class="page-link" href="#" @click.prevent="changePendingPage(pendingPage - 1)">上一页</a>
          </li>
          <li class="page-item" v-for="i in pendingFoods.totalPages" :key="i" :class="{ active: pendingPage === i - 1 }">
            <a class="page-link" href="#" @click.prevent="changePendingPage(i - 1)">{{ i }}</a>
          </li>
          <li class="page-item" :class="{ disabled: pendingPage >= pendingFoods.totalPages - 1 }">
            <a class="page-link" href="#" @click.prevent="changePendingPage(pendingPage + 1)">下一页</a>
          </li>
        </ul>
      </nav>
    </div>

    <!-- 用户管理 -->
    <div v-if="tab === 'users'">
      <h4>用户列表</h4>
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users.content" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>
              <span class="badge" :class="user.role === 'ADMIN' ? 'bg-danger' : 'bg-secondary'">
                {{ user.role }}
              </span>
            </td>
            <td>
              <button
                class="btn btn-sm btn-danger"
                @click="deleteUser(user.id)"
                :disabled="user.role === 'ADMIN'"
              >删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <nav v-if="users.totalPages > 1">
        <ul class="pagination">
          <li class="page-item" :class="{ disabled: userPage === 0 }">
            <button class="page-link" @click="changeUserPage(userPage - 1)">上一页</button>
          </li>
          <li class="page-item" v-for="i in users.totalPages" :key="i" :class="{ active: userPage === i - 1 }">
            <button class="page-link" @click="changeUserPage(i - 1)">{{ i }}</button>
          </li>
          <li class="page-item" :class="{ disabled: userPage >= users.totalPages - 1 }">
            <button class="page-link" @click="changeUserPage(userPage + 1)">下一页</button>
          </li>
        </ul>
      </nav>
    </div>

    <!-- 美食管理 -->
    <div v-if="tab === 'foods'">
      <h4>美食列表</h4>
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>分类</th>
            <th>作者</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="food in foods.content" :key="food.id">
            <td>{{ food.id }}</td>
            <td>{{ food.title }}</td>
            <td>{{ food.category }}</td>
            <td>{{ food.username }}</td>
            <td>
              <button class="btn btn-sm btn-danger" @click="deleteFood(food.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <nav v-if="foods.totalPages > 1">
        <ul class="pagination">
          <li class="page-item" :class="{ disabled: foodPage === 0 }">
            <button class="page-link" @click="changeFoodPage(foodPage - 1)">上一页</button>
          </li>
          <li class="page-item" v-for="i in foods.totalPages" :key="i" :class="{ active: foodPage === i - 1 }">
            <button class="page-link" @click="changeFoodPage(i - 1)">{{ i }}</button>
          </li>
          <li class="page-item" :class="{ disabled: foodPage >= foods.totalPages - 1 }">
            <button class="page-link" @click="changeFoodPage(foodPage + 1)">下一页</button>
          </li>
        </ul>
      </nav>
    </div>

    <!-- 评论管理 -->
    <div v-if="tab === 'comments'">
      <h4>评论列表</h4>
      <div v-if="comments.content.length === 0" class="text-muted">暂无评论</div>
      <div class="comment-list">
        <div class="comment-item card mb-2" v-for="comment in comments.content" :key="comment.id">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start">
              <div>
                <div class="d-flex align-items-center gap-2 mb-2">
                  <strong>{{ comment.username }}</strong>
                  <small class="text-muted">{{ formatDate(comment.createdAt) }}</small>
                </div>
                <p class="mb-2">{{ comment.content }}</p>
                <small class="text-muted">
                  评论了 <router-link :to="'/food/' + comment.foodId" class="text-primary">{{ comment.foodTitle }}</router-link>
                </small>
              </div>
              <button class="btn btn-sm btn-outline-danger" @click="deleteComment(comment.id)">删除</button>
            </div>
          </div>
        </div>
      </div>
      <nav v-if="comments.totalPages > 1">
        <ul class="pagination">
          <li class="page-item" :class="{ disabled: commentPage === 0 }">
            <a class="page-link" href="#" @click.prevent="changeCommentPage(commentPage - 1)">上一页</a>
          </li>
          <li class="page-item" v-for="i in comments.totalPages" :key="i" :class="{ active: commentPage === i - 1 }">
            <a class="page-link" href="#" @click.prevent="changeCommentPage(i - 1)">{{ i }}</a>
          </li>
          <li class="page-item" :class="{ disabled: commentPage >= comments.totalPages - 1 }">
            <a class="page-link" href="#" @click.prevent="changeCommentPage(commentPage + 1)">下一页</a>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminAPI } from '../api'

const tab = ref('stats')
const users = ref({ content: [] })
const foods = ref({ content: [] })
const pendingFoods = ref({ content: [] })
const comments = ref({ content: [] })
const pendingPage = ref(0)
const commentPage = ref(0)
const userPage = ref(0)
const foodPage = ref(0)
const pendingCount = ref(0)
const stats = ref({})
const toast = ref({ show: false, message: '', type: 'success' })

const loadStats = async () => {
  try {
    const res = await adminAPI.getStatistics()
    stats.value = res.data
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type }
  setTimeout(() => {
    toast.value.show = false
  }, 3000)
}

const loadUsers = async (page = 0) => {
  try {
    const res = await adminAPI.getUsers(page, 10)
    users.value = res.data
    userPage.value = page
  } catch (error) {
    console.error('加载失败', error)
    showToast('加载用户失败', 'error')
  }
}

const loadFoods = async (page = 0) => {
  try {
    const res = await adminAPI.getAllFoods(page, 10)
    foods.value = res.data
    foodPage.value = page
  } catch (error) {
    console.error('加载失败', error)
    showToast('加载美食失败', 'error')
  }
}

const changeUserPage = (page) => {
  loadUsers(page)
}

const changeFoodPage = (page) => {
  loadFoods(page)
}

const loadPendingFoods = async (page = 0) => {
  try {
    const res = await adminAPI.getPendingFoods(page, 10)
    pendingFoods.value = res.data
    pendingPage.value = page
    pendingCount.value = res.data.totalElements
  } catch (error) {
    console.error('加载失败', error)
  }
}

const loadComments = async (page = 0) => {
  try {
    const res = await adminAPI.getComments(page, 20)
    comments.value = res.data
    commentPage.value = page
  } catch (error) {
    console.error('加载评论失败', error)
  }
}

const changePendingPage = (page) => {
  loadPendingFoods(page)
}

const changeCommentPage = (page) => {
  loadComments(page)
}

const approveFood = async (id) => {
  try {
    await adminAPI.approveFood(id)
    loadPendingFoods(pendingPage.value)
    loadStats()
  } catch (error) {
    console.error('审核失败', error)
  }
}

const rejectFood = async (id) => {
  try {
    await adminAPI.rejectFood(id)
    loadPendingFoods(pendingPage.value)
    loadStats()
  } catch (error) {
    console.error('审核失败', error)
  }
}

const deleteUser = async (id) => {
  if (!confirm('确定删除该用户？')) return
  try {
    await adminAPI.deleteUser(id)
    loadUsers(userPage.value)
    showToast('删除成功')
  } catch (error) {
    console.error('删除失败', error)
    showToast('删除失败', 'error')
  }
}

const deleteFood = async (id) => {
  if (!confirm('确定删除该美食？')) return
  try {
    await adminAPI.deleteFood(id)
    loadFoods(foodPage.value)
    showToast('删除成功')
  } catch (error) {
    showToast('删除失败: ' + (error.response?.data?.message || error.message), 'error')
  }
}

const deleteComment = async (id) => {
  if (!confirm('确定删除该评论？')) return
  try {
    await adminAPI.deleteComment(id)
    loadComments(commentPage.value)
    showToast('删除成功')
  } catch (error) {
    console.error('删除失败', error)
    showToast('删除失败', 'error')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadStats()
  loadPendingFoods()
  loadUsers()
  loadFoods()
  loadComments()
})
</script>

<style scoped>
.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.stat-icon {
  font-size: 40px;
}

.stat-info h3 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
}

.stat-info p {
  margin: 0;
  color: #666;
}

.stat-users { border-left: 4px solid #4CAF50; }
.stat-foods { border-left: 4px solid #FF9800; }
.stat-comments { border-left: 4px solid #2196F3; }
.stat-likes { border-left: 4px solid #E91E63; }

.comment-item {
  transition: box-shadow 0.2s;
}

.comment-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* Toast 提示样式 */
.toast-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
}

.toast-message {
  padding: 12px 24px;
  border-radius: 8px;
  color: white;
  font-weight: 500;
  animation: slideIn 0.3s ease;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.toast-message.success {
  background: #4CAF50;
}

.toast-message.error {
  background: #f44336;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(100px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 分页按钮样式 */
.pagination .page-link {
  border: none;
  background: transparent;
  color: #333;
  padding: 8px 12px;
  border-radius: 8px;
}

.pagination .page-link:hover {
  background: rgba(0,0,0,0.05);
}

.pagination .page-item.active .page-link {
  background: #ff4d8d;
  color: white;
}

.pagination .page-item.disabled .page-link {
  color: #ccc;
  pointer-events: none;
}

/* 修复 nav-tabs 和分页按钮冲突 */
.nav-tabs .nav-link {
  border: none;
  background: transparent;
  color: #666;
}

.nav-tabs .nav-link.active {
  color: #ff4d8d;
  border-bottom: 2px solid #ff4d8d;
  background: transparent;
}

.nav-tabs .nav-link:hover {
  color: #ff4d8d;
  background: transparent;
}
</style>
