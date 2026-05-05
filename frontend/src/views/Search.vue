<template>
  <div class="container py-4">
    <div class="mb-4">
      <div class="input-group">
        <input
          type="text"
          class="form-control"
          v-model="keyword"
          placeholder="搜索美食..."
          @keyup.enter="search"
        >
        <button class="btn btn-primary" @click="search">搜索</button>
      </div>
    </div>

    <h4 v-if="keyword">搜索结果: "{{ keyword }}"</h4>
    <div class="row">
      <div class="col-md-3 mb-4" v-for="food in results.content" :key="food.id">
        <div class="food-card card" @click="goToDetail(food.id)">
          <img :src="getImageUrl(food.imageUrl) || 'https://via.placeholder.com/300x200'" class="card-img-top">
          <div class="card-body">
            <h6 class="card-title">{{ food.title }}</h6>
            <p class="card-text text-muted">{{ food.location }}</p>
            <div class="d-flex justify-content-between">
              <span class="text-warning">❤️ {{ food.likeCount }}</span>
              <small class="text-muted">{{ food.category }}</small>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="results.content?.length === 0 && searched" class="text-center py-5 text-muted">
      没有找到相关美食
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { foodAPI } from '../api'
import { getImageUrl } from '../utils'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const results = ref({ content: [] })
const searched = ref(false)

const search = async () => {
  if (!keyword.value.trim()) return
  searched.value = true
  try {
    const res = await foodAPI.search(keyword.value)
    results.value = res.data
  } catch (error) {
    console.error('搜索失败', error)
  }
}

const goToDetail = (id) => {
  router.push(`/food/${id}`)
}

onMounted(() => {
  if (route.query.keyword) {
    keyword.value = route.query.keyword
    search()
  }
})
</script>
