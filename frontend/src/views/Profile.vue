<template>
  <div class="container py-4">
    <div class="card mb-4">
      <div class="card-body">
        <div class="row align-items-center">
          <div class="col-md-2 text-center">
            <img :src="getImageUrl(user.avatar) || 'https://via.placeholder.com/100'" class="rounded-circle" width="100" height="100">
          </div>
          <div class="col-md-8">
            <h3>{{ user.username }}</h3>
            <p class="text-muted">{{ user.bio || '暂无简介' }}</p>
            <p class="text-muted">邮箱: {{ user.email }}</p>
            <p class="text-muted">角色: {{ user.role }}</p>
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-primary" @click="showEdit = true">编辑资料</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showEdit" class="card mb-4">
      <div class="card-body">
        <h5>编辑资料</h5>
        <div class="mb-3">
          <label class="form-label">头像上传</label>
          <input type="file" class="form-control" ref="avatarInput" @change="handleAvatarChange" accept="image/*">
          <div v-if="avatarPreview" class="mt-2">
            <img :src="avatarPreview" class="rounded-circle" width="80" height="80">
          </div>
        </div>
        <div class="mb-3">
          <label class="form-label">简介</label>
          <textarea class="form-control" v-model="editForm.bio" rows="3"></textarea>
        </div>
        <button class="btn btn-primary me-2" @click="updateProfile">保存</button>
        <button class="btn btn-secondary" @click="cancelEdit">取消</button>
      </div>
    </div>

    <h4>我发布的美食</h4>
    <div class="row">
      <div class="col-md-3 mb-4" v-for="food in myFoods.content" :key="food.id">
        <div class="food-card card" @click="goToDetail(food.id)">
          <img :src="getImageUrl(food.imageUrl) || 'https://via.placeholder.com/300x200'" class="card-img-top">
          <div class="card-body">
            <h6 class="card-title">{{ food.title }}</h6>
            <p class="card-text text-muted">{{ food.location }}</p>
            <div class="d-flex justify-content-between">
              <span class="text-warning">❤️ {{ food.likeCount }}</span>
              <button class="btn btn-sm btn-outline-danger" @click.stop="deleteFood(food.id)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI, foodAPI, uploadAPI } from '../api'
import { getImageUrl } from '../utils'

const router = useRouter()
const user = ref({})
const myFoods = ref({ content: [] })
const showEdit = ref(false)
const editForm = ref({ avatar: '', bio: '' })
const avatarPreview = ref('')
const avatarInput = ref(null)

const loadUser = async () => {
  try {
    const res = await authAPI.getCurrentUser()
    user.value = res.data
    editForm.value.bio = res.data.bio || ''
  } catch (error) {
    console.error('加载失败', error)
  }
}

const loadMyFoods = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const res = await foodAPI.getUserFoods(userId)
    myFoods.value = res.data
  } catch (error) {
    console.error('加载失败', error)
  }
}

const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    const res = await uploadAPI.upload(file)
    editForm.value.avatar = res.data
    avatarPreview.value = 'http://localhost:8080' + res.data
  } catch (error) {
    console.error('上传头像失败', error)
    alert('上传失败')
  }
}

const updateProfile = async () => {
  try {
    await authAPI.updateProfile(editForm.value)
    showEdit.value = false
    avatarPreview.value = ''
    loadUser()
  } catch (error) {
    console.error('更新失败', error)
  }
}

const cancelEdit = () => {
  showEdit.value = false
  avatarPreview.value = ''
}

const deleteFood = async (id) => {
  if (!confirm('确定删除？')) return
  try {
    await foodAPI.delete(id)
    loadMyFoods()
  } catch (error) {
    console.error('删除失败', error)
  }
}

const goToDetail = (id) => {
  router.push(`/food/${id}`)
}

onMounted(() => {
  loadUser()
  loadMyFoods()
})
</script>
