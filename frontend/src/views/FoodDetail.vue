<template>
  <div class="container py-4" v-if="food">
    <div class="row">
      <div class="col-md-6">
        <img :src="getImageUrl(food.imageUrl) || 'https://via.placeholder.com/400x300'" class="food-detail-img" :alt="food.title">
      </div>
      <div class="col-md-6">
        <h2>{{ food.title }}</h2>
        <p class="text-muted">{{ food.location }} | {{ food.category }}</p>
        <p v-if="food.price" class="h4 text-primary">¥{{ food.price }}</p>

        <div class="d-flex align-items-center mb-3">
          <span
            class="heart-icon fs-4 me-3"
            :class="{ liked: food.liked }"
            @click="toggleLike"
          >
            {{ food.liked ? '❤️' : '🤍' }}
          </span>
          <span class="me-3">{{ food.likeCount }} 赞</span>

          <span
            class="heart-icon fs-4 me-3"
            :class="{ liked: food.favorited }"
            @click="toggleFavorite"
          >
            {{ food.favorited ? '⭐' : '☆' }}
          </span>
          <span>收藏</span>

          <span class="ms-3 text-muted">👁 {{ food.viewCount }} 浏览</span>
        </div>

        <div class="mb-3">
          <router-link :to="'/user/' + food.userId" class="text-decoration-none">
            <img :src="food.userAvatar || 'https://via.placeholder.com/40'" class="rounded-circle me-2" width="40" height="40">
            <span>{{ food.username }}</span>
          </router-link>
        </div>

        <p class="mt-3">{{ food.description }}</p>
      </div>
    </div>

    <div class="mt-5">
      <h4>评论 ({{ comments.totalElements }})</h4>
      <div v-if="token" class="mb-4">
        <textarea class="form-control" v-model="newComment" placeholder="说点什么..." rows="2"></textarea>
        <button class="btn btn-primary mt-2" @click="addComment" :disabled="!newComment.trim()">发表评论</button>
      </div>
      <div v-else class="alert alert-info">
        <router-link to="/login">登录</router-link>后发表评论
      </div>

      <div class="comments-list">
        <div class="comment-item" v-for="comment in comments.content" :key="comment.id">
          <div class="d-flex">
            <img :src="comment.userAvatar || 'https://via.placeholder.com/40'" class="rounded-circle me-2" width="40" height="40">
            <div class="flex-grow-1">
              <div class="d-flex justify-content-between">
                <strong>{{ comment.username }}</strong>
                <small class="text-muted">{{ formatDate(comment.createdAt) }}</small>
              </div>
              <p class="mb-0 mt-1">{{ comment.content }}</p>
            </div>
          </div>
        </div>
        <div v-if="comments.content?.length === 0" class="text-center text-muted py-4">
          暂无评论，快来抢沙发~
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { foodAPI, commentAPI, likeAPI, favoriteAPI } from '../api'
import { getImageUrl } from '../utils'

const route = useRoute()
const food = ref(null)
const comments = ref({ content: [], totalElements: 0 })
const newComment = ref('')
const token = localStorage.getItem('token')

const loadFood = async () => {
  try {
    const res = await foodAPI.getById(route.params.id)
    food.value = res.data
  } catch (error) {
    console.error('加载失败', error)
  }
}

const loadComments = async () => {
  try {
    const res = await commentAPI.getByFood(route.params.id)
    comments.value = res.data
  } catch (error) {
    console.error('加载评论失败', error)
  }
}

const toggleLike = async () => {
  if (!token || token === 'visitor') {
    alert('请先登录')
    return
  }
  try {
    const res = await likeAPI.toggle(food.value.id)
    food.value.liked = res.data.liked
    food.value.likeCount += res.data.liked ? 1 : -1
  } catch (error) {
    console.error('操作失败', error)
  }
}

const toggleFavorite = async () => {
  if (!token || token === 'visitor') {
    alert('请先登录')
    return
  }
  try {
    const res = await favoriteAPI.toggle(food.value.id)
    food.value.favorited = res.data.favorited
  } catch (error) {
    console.error('操作失败', error)
  }
}

const addComment = async () => {
  if (!token || token === 'visitor') {
    alert('请先登录')
    return
  }
  try {
    await commentAPI.add(food.value.id, { content: newComment.value })
    newComment.value = ''
    loadComments()
  } catch (error) {
    console.error('评论失败', error)
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadFood()
  loadComments()
})
</script>
