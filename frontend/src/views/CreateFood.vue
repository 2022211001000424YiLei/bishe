<template>
  <div class="create-food-page">
    <div class="create-card">
      <div class="card-header">
        <h2>分享美食店铺</h2>
        <p>推荐你喜欢的店铺给大家吧</p>
      </div>
      <form @submit.prevent="handleSubmit" class="card-body">
        <!-- 店铺选择 -->
        <div class="form-group">
          <label>选择店铺 *</label>
          <select v-model="form.shopId" required @change="onShopChange">
            <option value="">请选择店铺</option>
            <option v-for="shop in shops" :key="shop.id" :value="shop.id">
              {{ shop.name }}
            </option>
          </select>
          <div v-if="shops.length === 0 && !loadingShops" class="tip">
            暂无入驻店铺，请稍后再试
          </div>
        </div>

        <!-- 标题 -->
        <div class="form-group">
          <label>推荐标题 *</label>
          <input type="text" v-model="form.title" required placeholder="如：黄焖鸡米饭真好吃！">
        </div>

        <!-- 主观感受 -->
        <div class="form-group">
          <label>真实感受 *</label>
          <textarea v-model="form.description" required rows="5"
            placeholder="说说你的用餐体验：味道如何、分量多少、环境怎样..."></textarea>
        </div>

        <!-- 价格 -->
        <div class="form-group">
          <label>你花的价格（元）</label>
          <input type="number" v-model="form.price" step="0.01" placeholder="你这次消费了多少钱">
        </div>

        <!-- 地址（自动填充） -->
        <div class="form-group">
          <label>店铺地址</label>
          <input type="text" v-model="form.location" placeholder="地址">
        </div>

        <!-- 标签选择 -->
        <div class="form-group">
          <label>选择标签（可多选）</label>
          <div class="tags-grid">
            <span
              v-for="tag in availableTags"
              :key="tag"
              class="tag-item"
              :class="{ selected: form.tags.includes(tag) }"
              @click="toggleTag(tag)"
            >
              {{ tag }}
            </span>
          </div>
        </div>

        <!-- 图片 -->
        <div class="form-group">
          <label>上传图片</label>
          <div class="upload-area" @click="triggerFile" v-if="!form.imageUrl">
            <div class="upload-icon">📷</div>
            <div class="upload-text">点击上传图片</div>
          </div>
          <div class="preview-area" v-else>
            <img :src="imagePreview" alt="预览">
            <button type="button" class="remove-btn" @click="removeImage">×</button>
          </div>
          <input type="file" ref="fileInput" accept="image/*" @change="handleFileChange" hidden>
          <div v-if="uploading" class="uploading">上传中...</div>
        </div>

        <div class="error-msg" v-if="error">{{ error }}</div>

        <button type="submit" class="btn-submit" :disabled="loading || uploading">
          {{ loading ? '发布中...' : '发布推荐' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { foodAPI, shopAPI, uploadAPI } from '../api'

const router = useRouter()
const fileInput = ref(null)
const shops = ref([])
const loadingShops = ref(true)
const uploading = ref(false)
const loading = ref(false)
const error = ref('')

const form = ref({
  shopId: '',
  title: '',
  description: '',
  imageUrl: '',
  location: '',
  price: null,
  tags: []
})

const availableTags = ['主食', '小吃', '饮品', '甜品']

const toggleTag = (tag) => {
  const idx = form.value.tags.indexOf(tag)
  if (idx === -1) {
    form.value.tags.push(tag)
  } else {
    form.value.tags.splice(idx, 1)
  }
}

const imagePreview = computed(() => {
  return form.value.imageUrl ? `http://localhost:8080${form.value.imageUrl}` : ''
})

const loadShops = async () => {
  try {
    const res = await shopAPI.getAll()
    shops.value = res.data || []
  } catch (e) {
    console.error('加载店铺失败', e)
  } finally {
    loadingShops.value = false
  }
}

const onShopChange = () => {
  if (form.value.shopId) {
    const shop = shops.value.find(s => s.id == form.value.shopId)
    if (shop && shop.address) {
      form.value.location = shop.address
    }
  }
}

const triggerFile = () => {
  fileInput.value.click()
}

const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  uploading.value = true
  error.value = ''

  try {
    const res = await uploadAPI.upload(file)
    form.value.imageUrl = res.data
  } catch (err) {
    error.value = '图片上传失败'
  } finally {
    uploading.value = false
  }
}

const removeImage = () => {
  form.value.imageUrl = ''
}

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  try {
    const payload = {
      shopId: form.value.shopId,
      title: form.value.title,
      description: form.value.description,
      price: form.value.price,
      location: form.value.location,
      imageUrl: form.value.imageUrl,
      tags: form.value.tags.join(',')
    }
    await foodAPI.create(payload)
    router.push('/home')
  } catch (err) {
    error.value = err.response?.data?.message || '发布失败'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadShops()
})
</script>

<style scoped>
.create-food-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.create-card {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 560px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
  overflow: hidden;
}

.card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px;
  text-align: center;
}

.card-header h2 {
  margin: 0 0 8px;
  font-size: 22px;
}

.card-header p {
  margin: 0;
  opacity: 0.85;
  font-size: 14px;
}

.card-body {
  padding: 32px;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.tip {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
}

.tags-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 13px;
  background: #f5f5f5;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.tag-item:hover {
  background: #e8e8e8;
}

.tag-item.selected {
  background: #667eea;
  color: white;
}

.upload-area {
  border: 2px dashed #e0e0e0;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-area:hover {
  border-color: #667eea;
  background: #fafafa;
}

.upload-icon {
  font-size: 36px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: #666;
}

.preview-area {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.preview-area img {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  display: block;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(0,0,0,0.6);
  color: white;
  border: none;
  font-size: 18px;
  cursor: pointer;
  line-height: 1;
}

.uploading {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.error-msg {
  color: #f44336;
  font-size: 14px;
  margin-bottom: 16px;
  padding: 10px;
  background: #fff1f0;
  border-radius: 6px;
}

.btn-submit {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-submit:hover {
  opacity: 0.9;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
