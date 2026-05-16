<template>
  <div class="admin-wrapper">
    <!-- 左侧边栏 -->
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <div class="logo-area">
          <span class="logo-icon">🍜</span>
          <span class="logo-text">{{ isMerchant ? '商家管理' : '系统管理' }}</span>
        </div>
      </div>

      <nav class="sidebar-menu">
        <!-- 商家菜单 -->
        <template v-if="isMerchant">
          <div class="menu-group">
            <div class="menu-label">店铺管理</div>
            <a class="menu-item" :class="{ active: currentView === 'shop' }" @click="currentView = 'shop'">
              <span class="menu-icon">🏪</span>
              <span>我的店铺</span>
            </a>
          </div>
        </template>

        <!-- 管理员菜单 -->
        <template v-else>
          <div class="menu-group">
            <div class="menu-label">数据管理</div>
            <a class="menu-item" :class="{ active: currentView === 'stats' }" @click="currentView = 'stats'">
              <span class="menu-icon">📊</span>
              <span>数据统计</span>
            </a>
            <a class="menu-item" :class="{ active: currentView === 'audit' }" @click="currentView = 'audit'">
              <span class="menu-icon">✅</span>
              <span>审核管理</span>
              <span class="badge" v-if="pendingCount > 0">{{ pendingCount }}</span>
            </a>
          </div>
          <div class="menu-group">
            <div class="menu-label">用户管理</div>
            <a class="menu-item" :class="{ active: currentView === 'users' }" @click="currentView = 'users'">
              <span class="menu-icon">👥</span>
              <span>用户列表</span>
            </a>
            <a class="menu-item" :class="{ active: currentView === 'merchants' }" @click="currentView = 'merchants'">
              <span class="menu-icon">🏪</span>
              <span>商家管理</span>
            </a>
          </div>
          <div class="menu-group">
            <div class="menu-label">内容管理</div>
            <a class="menu-item" :class="{ active: currentView === 'foods' }" @click="currentView = 'foods'">
              <span class="menu-icon">🍜</span>
              <span>美食管理</span>
            </a>
            <a class="menu-item" :class="{ active: currentView === 'comments' }" @click="currentView = 'comments'">
              <span class="menu-icon">💬</span>
              <span>评论管理</span>
            </a>
          </div>
        </template>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <span class="user-name">{{ username }}</span>
          <span class="user-role">{{ isMerchant ? '商家' : '管理员' }}</span>
        </div>
        <a class="menu-item logout-btn" @click="handleLogout">
          <span class="menu-icon">🚪</span>
          <span>退出系统</span>
        </a>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="admin-main">
      <!-- 顶部栏 -->
      <header class="main-header">
        <div class="header-title">{{ pageTitle }}</div>
      </header>

      <!-- 内容区域 -->
      <div class="main-content">
        <!-- Toast -->
        <div class="toast-container" v-if="toast.show">
          <div class="toast-item" :class="toast.type">{{ toast.message }}</div>
        </div>

        <!-- ========== 商家视图 ========== -->
        <div v-if="isMerchant && currentView === 'shop'" class="view-shop">
          <!-- 成功提示 -->
          <div v-if="shopJustSaved" class="success-banner">
            <span class="success-icon">✅</span>
            <span>{{ shopJustSavedMessage }}</span>
          </div>

          <!-- 店铺列表 -->
          <div class="content-card">
            <div class="card-header">
              <h3>我的店铺 ({{ myShops.length }})</h3>
              <button class="btn-primary" @click="startAddShop">+ 添加店铺</button>
            </div>
            <div class="card-body">
              <div v-if="myShops.length === 0" class="empty-state">暂无店铺，点击添加按钮创建一个</div>
              <div class="shop-list">
                <div class="shop-item" v-for="shop in myShops" :key="shop.id">
                  <div class="shop-image">
                    <img v-if="shop.imageUrl" :src="shop.imageUrl" alt="店铺图片" @error="imgError">
                    <div v-else class="no-image">暂无图片</div>
                  </div>
                  <div class="shop-info">
                    <div class="shop-name">{{ shop.name }}</div>
                    <div class="shop-address">{{ shop.address || '未设置地址' }}</div>
                    <div class="shop-desc">{{ shop.description || '暂无简介' }}</div>
                  </div>
                  <div class="shop-actions">
                    <button class="btn-edit" @click="startEditShop(shop)">编辑</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 添加/编辑表单弹窗 -->
          <div class="modal-overlay" v-if="showShopModal" @click.self="closeShopModal">
            <div class="modal-content">
              <div class="modal-header">
                <h3>{{ editingShopId ? '编辑店铺' : '添加店铺' }}</h3>
                <button class="modal-close" @click="closeShopModal">×</button>
              </div>
              <div class="modal-body">
                <div class="form-layout">
                  <div class="preview-section">
                    <div class="preview-box" @click="triggerShopImage" :class="{ 'has-image': shopForm.imageUrl }">
                      <img v-if="shopForm.imageUrl" :src="shopImagePreview" alt="店铺图片" @error="imgError">
                      <div v-else class="preview-empty">
                        <span>📷</span>
                        <p>点击上传店铺图片</p>
                      </div>
                      <div v-if="shopUploading" class="upload-mask">
                        <div class="upload-spinner"></div>
                        <span>上传中...</span>
                      </div>
                    </div>
                    <input type="file" ref="shopImageInput" accept="image/*" @change="handleShopImageChange" hidden>
                  </div>
                  <div class="form-section">
                    <div class="form-group">
                      <label>店铺名称</label>
                      <input type="text" v-model="shopForm.name" placeholder="请输入店铺名称">
                    </div>
                    <div class="form-group">
                      <label>店铺地址</label>
                      <input type="text" v-model="shopForm.address" placeholder="如：学生食堂二楼">
                    </div>
                    <div class="form-group">
                      <label>店铺简介</label>
                      <textarea v-model="shopForm.description" rows="4" placeholder="介绍一下您的店铺特色..."></textarea>
                    </div>
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn-primary" @click="saveShop">{{ editingShopId ? '保存修改' : '创建店铺' }}</button>
                <button class="btn-default" @click="closeShopModal">取消</button>
              </div>
            </div>
          </div>
        </div>

        <!-- ========== 管理员视图 ========== -->
        <template v-else-if="!isMerchant">
          <!-- 数据统计 -->
          <div v-if="currentView === 'stats'" class="view-stats">
            <div class="stats-grid">
              <div class="stat-box">
                <div class="stat-icon">👥</div>
                <div class="stat-data">
                  <div class="stat-value">{{ stats.userCount || 0 }}</div>
                  <div class="stat-label">用户总数</div>
                </div>
              </div>
              <div class="stat-box">
                <div class="stat-icon">🍜</div>
                <div class="stat-data">
                  <div class="stat-value">{{ stats.foodCount || 0 }}</div>
                  <div class="stat-label">美食总数</div>
                </div>
              </div>
              <div class="stat-box">
                <div class="stat-icon">💬</div>
                <div class="stat-data">
                  <div class="stat-value">{{ stats.commentCount || 0 }}</div>
                  <div class="stat-label">评论总数</div>
                </div>
              </div>
              <div class="stat-box">
                <div class="stat-icon">❤️</div>
                <div class="stat-data">
                  <div class="stat-value">{{ stats.likeCount || 0 }}</div>
                  <div class="stat-label">点赞总数</div>
                </div>
              </div>
            </div>
            <div class="content-card mt-16">
              <div class="card-body">
                <div class="pending-info">
                  <span>待审核美食：</span>
                  <span class="pending-num">{{ stats.pendingCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 审核管理 -->
          <div v-if="currentView === 'audit'" class="view-audit">
            <div class="content-card">
              <div class="card-header">
                <h3>待审核美食</h3>
              </div>
              <div class="card-body">
                <div v-if="pendingFoods.length === 0" class="empty-state">暂无待审核内容</div>
                <div class="audit-list">
                  <div class="audit-item" v-for="food in pendingFoods" :key="food.id">
                    <div class="audit-info">
                      <div class="audit-title">{{ food.title }}</div>
                      <div class="audit-meta">
                        <span>{{ food.category }}</span>
                        <span>{{ food.location }}</span>
                        <span>发布者：{{ food.username }}</span>
                      </div>
                      <div class="audit-desc">{{ food.description }}</div>
                    </div>
                    <div class="audit-actions">
                      <button class="btn-success btn-sm" @click="approveFood(food.id)">通过</button>
                      <button class="btn-danger btn-sm" @click="rejectFood(food.id)">拒绝</button>
                      <router-link class="btn-default btn-sm" :to="'/food/' + food.id">查看</router-link>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 用户管理 -->
          <div v-if="currentView === 'users'" class="view-users">
            <div class="content-card">
              <div class="card-header">
                <h3>用户列表</h3>
              </div>
              <div class="card-body">
                <table class="data-table">
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
                    <tr v-for="user in users" :key="user.id">
                      <td>{{ user.id }}</td>
                      <td>{{ user.username }}</td>
                      <td>{{ user.email }}</td>
                      <td>
                        <span class="role-tag" :class="user.role.toLowerCase()">{{ getRoleName(user.role) }}</span>
                      </td>
                      <td>
                        <button v-if="user.role !== 'ADMIN'" class="btn-primary btn-sm" @click="changeRole(user)">
                          改为{{ user.role === 'USER' ? '商家' : '用户' }}
                        </button>
                        <button class="btn-danger btn-sm" @click="deleteUser(user.id)" :disabled="user.role === 'ADMIN'">删除</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 商家管理 -->
          <div v-if="currentView === 'merchants'" class="view-merchants">
            <div class="content-card">
              <div class="card-header">
                <h3>商家列表</h3>
              </div>
              <div class="card-body">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>用户名</th>
                      <th>邮箱</th>
                      <th>店铺</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="user in merchants" :key="user.id">
                      <td>{{ user.id }}</td>
                      <td>{{ user.username }}</td>
                      <td>{{ user.email }}</td>
                      <td>{{ user.shopName || '-' }}</td>
                      <td>
                        <button class="btn-default btn-sm" @click="changeRole(user)">改为用户</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 美食管理 -->
          <div v-if="currentView === 'foods'" class="view-foods">
            <div class="content-card">
              <div class="card-header">
                <h3>美食列表</h3>
              </div>
              <div class="card-body">
                <table class="data-table">
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
                    <tr v-for="food in foods" :key="food.id">
                      <td>{{ food.id }}</td>
                      <td>{{ food.title }}</td>
                      <td>{{ food.category }}</td>
                      <td>{{ food.username }}</td>
                      <td>
                        <button class="btn-danger btn-sm" @click="deleteFood(food.id)">删除</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 评论管理 -->
          <div v-if="currentView === 'comments'" class="view-comments">
            <div class="content-card">
              <div class="card-header">
                <h3>评论列表</h3>
              </div>
              <div class="card-body">
                <div v-if="comments.length === 0" class="empty-state">暂无评论</div>
                <div class="comment-list">
                  <div class="comment-item" v-for="c in comments" :key="c.id">
                    <div class="comment-info">
                      <div class="comment-user">
                        <strong>{{ c.username }}</strong>
                        <span class="comment-time">{{ formatDate(c.createdAt) }}</span>
                      </div>
                      <div class="comment-content">{{ c.content }}</div>
                      <div class="comment-ref">
                        评论了 <router-link :to="'/food/' + c.foodId">{{ c.foodTitle }}</router-link>
                      </div>
                    </div>
                    <button class="btn-danger btn-sm" @click="deleteComment(c.id)">删除</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminAPI, shopAPI, uploadAPI } from '../api'

const router = useRouter()
const userRole = ref(localStorage.getItem('role') || '')
const username = ref(localStorage.getItem('username') || '用户')
const isMerchant = computed(() => userRole.value === 'MERCHANT')
const currentView = ref(isMerchant.value ? 'shop' : 'stats')

// 商家数据
const myShops = ref([])
const shopForm = ref({ name: '', address: '', description: '', imageUrl: '' })
const shopImageInput = ref(null)
const shopUploading = ref(false)
const shopJustSaved = ref(false)
const shopJustSavedMessage = ref('')
const showShopModal = ref(false)
const editingShopId = ref(null)

const shopImagePreview = computed(() => {
  if (!shopForm.value.imageUrl) return ''
  if (shopForm.value.imageUrl.startsWith('http')) return shopForm.value.imageUrl
  return `http://localhost:8080${shopForm.value.imageUrl}`
})

const startAddShop = () => {
  editingShopId.value = null
  shopForm.value = { name: '', address: '', description: '', imageUrl: '' }
  showShopModal.value = true
}

const startEditShop = (shop) => {
  editingShopId.value = shop.id
  shopForm.value = {
    name: shop.name || '',
    address: shop.address || '',
    description: shop.description || '',
    imageUrl: shop.imageUrl || ''
  }
  showShopModal.value = true
}

const closeShopModal = () => {
  showShopModal.value = false
  editingShopId.value = null
}

const triggerShopImage = () => {
  shopImageInput.value.click()
}

const handleShopImageChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  shopUploading.value = true
  try {
    const res = await uploadAPI.upload(file)
    shopForm.value.imageUrl = res.data
  } catch (err) {
    showToast('图片上传失败', 'error')
  } finally {
    shopUploading.value = false
  }
}

const saveShop = async () => {
  try {
    if (editingShopId.value) {
      await shopAPI.updateShop(editingShopId.value, shopForm.value)
      shopJustSavedMessage.value = '保存成功！'
    } else {
      await shopAPI.createShop(shopForm.value)
      shopJustSavedMessage.value = '店铺创建成功！'
    }
    shopJustSaved.value = true
    showShopModal.value = false
    loadMyShops()
    setTimeout(() => {
      shopJustSaved.value = false
    }, 3000)
  } catch (e) {
    showToast('保存失败', 'error')
  }
}

const loadMyShops = async () => {
  if (!isMerchant.value) return
  try {
    const res = await shopAPI.getMyShop()
    myShops.value = res.data || []
  } catch (e) {
    myShops.value = []
  }
}

const imgError = (e) => {
  e.target.src = 'https://java-abcde.oss-cn-beijing.aliyuncs.com/90.jpg'
}

// 管理员数据
const stats = ref({})
const pendingFoods = ref([])
const users = ref([])
const merchants = ref([])
const foods = ref([])
const comments = ref([])
const pendingCount = ref(0)

const toast = ref({ show: false, message: '', type: 'success' })

const pageTitle = computed(() => {
  const titles = {
    shop: '店铺管理',
    stats: '数据统计',
    audit: '审核管理',
    users: '用户列表',
    merchants: '商家管理',
    foods: '美食管理',
    comments: '评论管理'
  }
  return titles[currentView.value] || ''
})

const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type }
  setTimeout(() => { toast.value.show = false }, 3000)
}

const getRoleName = (role) => {
  const names = { ADMIN: '管理员', MERCHANT: '商家', USER: '用户' }
  return names[role] || role
}

// 管理员方法
const loadStats = async () => {
  try {
    const res = await adminAPI.getStatistics()
    stats.value = res.data
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

const loadPendingFoods = async () => {
  try {
    const res = await adminAPI.getPendingFoods(0, 20)
    pendingFoods.value = res.data.content || []
    pendingCount.value = res.data.totalElements || 0
  } catch (e) {
    console.error('加载待审核失败', e)
  }
}

const loadUsers = async () => {
  try {
    const res = await adminAPI.getUsers(0, 100)
    users.value = res.data.content || []
    merchants.value = users.value.filter(u => u.role === 'MERCHANT').map(m => ({ ...m, shopName: '' }))
  } catch (e) {
    console.error('加载用户失败', e)
  }
}

const loadFoods = async () => {
  try {
    const res = await adminAPI.getAllFoods(0, 100)
    foods.value = res.data.content || []
  } catch (e) {
    console.error('加载美食失败', e)
  }
}

const loadComments = async () => {
  try {
    const res = await adminAPI.getComments(0, 100)
    comments.value = res.data.content || []
  } catch (e) {
    console.error('加载评论失败', e)
  }
}

const approveFood = async (id) => {
  try {
    await adminAPI.approveFood(id)
    showToast('审核通过')
    loadPendingFoods()
    loadStats()
  } catch (e) {
    showToast('操作失败', 'error')
  }
}

const rejectFood = async (id) => {
  try {
    await adminAPI.rejectFood(id)
    showToast('已拒绝')
    loadPendingFoods()
    loadStats()
  } catch (e) {
    showToast('操作失败', 'error')
  }
}

const changeRole = async (user) => {
  const newRole = user.role === 'USER' ? 'MERCHANT' : 'USER'
  if (!confirm(`确定将 ${user.username} 改为${newRole === 'MERCHANT' ? '商家' : '用户'}？`)) return
  try {
    await adminAPI.updateUserRole(user.id, newRole)
    showToast('修改成功')
    loadUsers()
  } catch (e) {
    showToast('修改失败', 'error')
  }
}

const deleteUser = async (id) => {
  if (!confirm('确定删除该用户？')) return
  try {
    await adminAPI.deleteUser(id)
    showToast('删除成功')
    loadUsers()
  } catch (e) {
    showToast('删除失败', 'error')
  }
}

const deleteFood = async (id) => {
  if (!confirm('确定删除该美食？')) return
  try {
    await adminAPI.deleteFood(id)
    showToast('删除成功')
    loadFoods()
  } catch (e) {
    showToast('删除失败', 'error')
  }
}

const deleteComment = async (id) => {
  if (!confirm('确定删除该评论？')) return
  try {
    await adminAPI.deleteComment(id)
    showToast('删除成功')
    loadComments()
  } catch (e) {
    showToast('删除失败', 'error')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

const handleLogout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(() => {
  if (isMerchant.value) {
    loadMyShops()
  } else {
    loadStats()
    loadPendingFoods()
    loadUsers()
    loadFoods()
    loadComments()
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>

<style scoped>
.admin-wrapper {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

/* 左侧边栏 */
.admin-sidebar {
  width: 240px;
  background: #001529;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.sidebar-header {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.sidebar-menu {
  flex: 1;
  padding: 16px 0;
  overflow-y: auto;
}

.menu-group {
  margin-bottom: 8px;
}

.menu-label {
  color: rgba(255,255,255,0.45);
  font-size: 12px;
  padding: 12px 16px 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: rgba(255,255,255,0.75);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  text-decoration: none;
}

.menu-item:hover {
  color: #fff;
  background: rgba(255,255,255,0.08);
}

.menu-item.active {
  color: #fff;
  background: #1890ff;
}

.menu-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.badge {
  margin-left: auto;
  background: #ff4d4d;
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: rgba(255,255,255,0.65);
  font-size: 12px;
  margin-bottom: 12px;
  padding: 0 4px;
}

.user-role {
  font-size: 11px;
  color: rgba(255,255,255,0.45);
}

.logout-btn {
  color: rgba(255,255,255,0.65);
}

.logout-btn:hover {
  color: #ff4d4d;
  background: rgba(255,77,77,0.15);
}

/* 主内容区 */
.admin-main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

.main-header {
  background: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.main-content {
  flex: 1;
  padding: 24px;
}

/* Toast */
.toast-container {
  position: fixed;
  top: 80px;
  right: 24px;
  z-index: 9999;
}

.toast-item {
  padding: 12px 20px;
  border-radius: 6px;
  color: #fff;
  font-size: 14px;
  animation: slideIn 0.3s ease;
}

.toast-item.success {
  background: #52c41a;
}

.toast-item.error {
  background: #ff4d4d;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-box {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.stat-icon {
  font-size: 32px;
}

.stat-data {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* 内容卡片 */
.content-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  overflow: hidden;
}

.card-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.card-body {
  padding: 24px;
}

.mt-16 {
  margin-top: 16px;
}

/* 商家表单 */
.form-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 32px;
}

.preview-section {
  position: relative;
}

.preview-box {
  width: 100%;
  height: 220px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.preview-box:hover {
  background: #eee;
}

.preview-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-empty {
  text-align: center;
  color: #bbb;
}

.preview-empty span {
  font-size: 48px;
  display: block;
  margin-bottom: 8px;
}

.preview-empty p {
  font-size: 12px;
}

.upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.upload-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group label {
  display: block;
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24,144,255,0.2);
}

.form-group textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

/* 店铺信息展示 */
.shop-info-layout {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 24px;
}

.shop-image {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
}

.shop-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

.shop-details {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item label {
  font-size: 12px;
  color: #999;
}

.detail-item span {
  font-size: 14px;
  color: #333;
}

.btn-edit {
  padding: 6px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
}

.btn-edit:hover {
  background: #40a9ff;
}

/* 店铺列表 */
.shop-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.shop-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.shop-item .shop-image {
  width: 80px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  background: #e8e8e8;
  flex-shrink: 0;
}

.shop-item .shop-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-item .shop-image .no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
}

.shop-item .shop-info {
  flex: 1;
  min-width: 0;
}

.shop-item .shop-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.shop-item .shop-address {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.shop-item .shop-desc {
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.shop-item .shop-actions {
  flex-shrink: 0;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  border-radius: 4px;
}

.modal-close:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* 成功提示横幅 */
.success-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 15px;
  font-weight: 500;
  animation: fadeIn 0.3s ease;
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.success-icon {
  font-size: 20px;
}

/* 审核列表 */
.audit-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.audit-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.audit-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.audit-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.audit-desc {
  font-size: 13px;
  color: #666;
}

.audit-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 数据表格 */
.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  background: #fafafa;
  font-size: 13px;
  font-weight: 500;
  color: #666;
}

.data-table td {
  font-size: 14px;
  color: #333;
}

.role-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.role-tag.admin {
  background: #fff1f0;
  color: #cf1322;
}

.role-tag.merchant {
  background: #f6ffed;
  color: #389e0d;
}

.role-tag.user {
  background: #f5f5f5;
  color: #666;
}

/* 待审核信息 */
.pending-info {
  font-size: 14px;
  color: #666;
}

.pending-num {
  font-size: 20px;
  font-weight: 600;
  color: #faad14;
  margin-left: 8px;
}

/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.comment-user {
  margin-bottom: 8px;
}

.comment-user strong {
  margin-right: 12px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.comment-ref {
  font-size: 12px;
  color: #999;
}

.comment-ref a {
  color: #1890ff;
  text-decoration: none;
}

.empty-state {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}

/* 按钮 */
.btn-primary {
  padding: 10px 24px;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-default {
  padding: 10px 24px;
  background: #fff;
  color: #666;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  display: inline-block;
}

.btn-default:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.btn-success {
  padding: 6px 16px;
  background: #52c41a;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}

.btn-danger {
  padding: 6px 16px;
  background: #ff4d4d;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}

.btn-sm {
  padding: 4px 12px;
  font-size: 12px;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateX(20px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
