<template>
  <div class="container py-4">
    <h2 class="mb-4">我的收藏</h2>
    <div class="row">
      <div class="col-md-3 mb-4" v-for="food in favorites.content" :key="food.id">
        <div class="food-card card" @click="goToDetail(food.id)">
          <img :src="getImageUrl(food.imageUrl) || 'https://via.placeholder.com/300x200'" class="card-img-top">
          <div class="card-body">
            <h6 class="card-title">{{ food.title }}</h6>
            <p class="card-text text-muted">{{ food.location }}</p>
            <div class="d-flex justify-content-between">
              <span class="text-warning">❤️ {{ food.likeCount }}</span>
              <button class="btn btn-sm btn-outline-secondary" @click.stop="removeFavorite(food.id)">取消收藏</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="favorites.content?.length === 0" class="text-center py-5 text-muted">
      还没有收藏，快去发现美食吧~
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { favoriteAPI } from '../api'
import { getImageUrl } from '../utils'

const router = useRouter()
const favorites = ref({ content: [] })

const loadFavorites = async () => {
  try {
    const res = await favoriteAPI.getAll()
    favorites.value = res.data
  } catch (error) {
    console.error('加载失败', error)
  }
}

const removeFavorite = async (id) => {
  try {
    await favoriteAPI.toggle(id)
    loadFavorites()
  } catch (error) {
    console.error('操作失败', error)
  }
}

const goToDetail = (id) => {
  router.push(`/food/${id}`)
}

onMounted(() => {
  loadFavorites()
})
</script>
