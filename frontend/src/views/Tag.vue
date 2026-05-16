<template>
  <div class="container py-4">
    <h2 class="mb-4">{{ tag }} 分类</h2>
    <div class="row">
      <div class="col-md-3 mb-4" v-for="food in foods.content" :key="food.id">
        <div class="food-card card" @click="goToDetail(food.id)">
          <img :src="getImageUrl(food.imageUrl) || 'https://via.placeholder.com/300x200'" class="card-img-top">
          <div class="card-body">
            <h6 class="card-title">{{ food.title }}</h6>
            <p class="card-text text-muted">{{ food.location }}</p>
            <div class="d-flex justify-content-between">
              <span class="text-warning">❤️ {{ food.likeCount }}</span>
              <small class="text-muted">{{ food.tags }}</small>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="foods.content?.length === 0" class="text-center py-5 text-muted">
      暂无美食
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { foodAPI } from '../api'
import { getImageUrl } from '../utils'

const route = useRoute()
const router = useRouter()
const tag = ref(route.params.tag)
const foods = ref({ content: [] })

const loadFoods = async () => {
  try {
    const res = await foodAPI.getByTag(tag.value)
    foods.value = res.data
  } catch (error) {
    console.error('加载失败', error)
  }
}

const goToDetail = (id) => {
  router.push(`/food/${id}`)
}

watch(() => route.params.tag, (newTag) => {
  tag.value = newTag
  loadFoods()
})

onMounted(() => {
  loadFoods()
})
</script>
