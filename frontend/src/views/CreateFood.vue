<template>
  <div class="container py-4">
    <div class="card">
      <div class="card-body">
        <h3 class="mb-4">发布美食</h3>
        <form @submit.prevent="handleSubmit">
          <div class="mb-3">
            <label class="form-label">标题 *</label>
            <input type="text" class="form-control" v-model="form.title" required>
          </div>
          <div class="mb-3">
            <label class="form-label">描述</label>
            <textarea class="form-control" v-model="form.description" rows="3"></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">图片上传</label>
            <input type="file" class="form-control" accept="image/*" @change="handleFileChange">
            <div v-if="form.imageUrl" class="mt-2">
              <img :src="imagePreview" alt="预览" style="max-width: 200px; max-height: 200px;">
              <button type="button" class="btn btn-sm btn-outline-danger ms-2" @click="removeImage">删除</button>
            </div>
            <div v-if="uploading" class="mt-2 text-muted">上传中...</div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <label class="form-label">位置</label>
              <input type="text" class="form-control" v-model="form.location" placeholder="如：学校食堂一楼">
            </div>
            <div class="col-md-6">
              <label class="form-label">分类</label>
              <select class="form-select" v-model="form.category">
                <option value="">请选择</option>
                <option value="主食">主食</option>
                <option value="小吃">小吃</option>
                <option value="饮品">饮品</option>
                <option value="甜品">甜品</option>
              </select>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <label class="form-label">价格</label>
              <input type="number" class="form-control" v-model="form.price" step="0.01" placeholder="0.00">
            </div>
          </div>
          <div class="alert alert-danger" v-if="error">{{ error }}</div>
          <button type="submit" class="btn btn-primary" :disabled="loading || uploading">
            {{ loading ? '发布中...' : '发布' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { foodAPI, uploadAPI } from '../api'

const router = useRouter()
const form = ref({
  title: '',
  description: '',
  imageUrl: '',
  location: '',
  category: '',
  price: null
})
const selectedFile = ref(null)
const uploading = ref(false)
const loading = ref(false)
const error = ref('')

const imagePreview = computed(() => {
  if (form.value.imageUrl) {
    return 'http://localhost:8080' + form.value.imageUrl
  }
  return ''
})

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  selectedFile.value = file
  uploading.value = true
  error.value = ''

  try {
    const res = await uploadAPI.upload(file)
    form.value.imageUrl = res.data
  } catch (err) {
    error.value = '图片上传失败'
    selectedFile.value = null
  } finally {
    uploading.value = false
  }
}

const removeImage = () => {
  form.value.imageUrl = ''
  selectedFile.value = null
}

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  try {
    await foodAPI.create(form.value)
    router.push('/home')
  } catch (err) {
    error.value = err.response?.data?.message || '发布失败'
  } finally {
    loading.value = false
  }
}
</script>
