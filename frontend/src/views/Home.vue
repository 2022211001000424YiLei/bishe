<template>
  <div class="home-page">
    <div class="menu-toggle" @click="toggleMenu">
      {{ showMenu ? 'X' : '=' }}
    </div>
    <nav class="home-nav" v-if="showMenu">
      <div class="container">
        <router-link class="navbar-brand" to="/home">🍜 校园美食</router-link>
        <ul class="navbar-nav ms-auto">
          <!-- 商家菜单 -->
          <template v-if="isMerchant">
            <li class="nav-item"><router-link class="nav-link" to="/admin">店铺管理</router-link></li>
            <li class="nav-item"><a class="nav-link" href="#" @click.prevent="logout">退出</a></li>
          </template>
          <!-- 管理员菜单 -->
          <template v-else-if="isAdmin">
            <li class="nav-item"><router-link class="nav-link" to="/admin">管理</router-link></li>
            <li class="nav-item"><a class="nav-link" href="#" @click.prevent="logout">退出</a></li>
          </template>
          <!-- 普通用户菜单 -->
          <template v-else>
            <li class="nav-item"><router-link class="nav-link" to="/home">首页</router-link></li>
            <li class="nav-item"><router-link class="nav-link" to="/create">发布美食</router-link></li>
            <li class="nav-item"><router-link class="nav-link" to="/favorites">收藏</router-link></li>
            <li class="nav-item"><router-link class="nav-link" to="/profile">我的</router-link></li>
            <li class="nav-item"><a class="nav-link" href="#" @click.prevent="logout">退出</a></li>
          </template>
        </ul>
      </div>
    </nav>

    <div class="hero-carousel">
      <div
        class="hero-slide"
        v-for="(slide, index) in heroSlides"
        :key="index"
        :style="{ backgroundImage: `linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url(${slide.image})` }"
        v-show="currentSlide === index"
      >
        <div class="hero-content">
          <h1>{{ slide.title }}</h1>
          <p class="lead">{{ slide.subtitle }}</p>
        </div>
      </div>
      <div class="hero-indicators">
        <span
          v-for="(_, index) in heroSlides"
          :key="index"
          :class="{ active: currentSlide === index }"
          @click="goToSlide(index)"
        ></span>
      </div>
      <button class="hero-arrow hero-arrow-left" @click="prevSlide">❮</button>
      <button class="hero-arrow hero-arrow-right" @click="nextSlide">❯</button>
    </div>

    <div class="container">
      <div class="mb-4">
        <div class="row justify-content-center">
          <div class="col-md-3 col-6 mb-3" v-for="category in categories" :key="category.name">
            <router-link :to="'/tag/' + category.name" class="text-decoration-none">
              <div class="card category-card" :style="{ background: category.color }">
                <div class="card-body text-white text-center">
                  <h3>{{ category.icon }}</h3>
                  <h5>{{ category.name }}</h5>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar-wrapper">
        <div class="home-search-bar">
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="搜索美食..."
            @keyup.enter="doSearch"
          >
          <button @click="doSearch">🔍</button>
        </div>
      </div>

      <h4 class="section-title">
        <span class="title-accent">🔥</span> 热门美食
      </h4>
      <div class="food-editorial-grid">
        <article
          class="food-editorial-card"
          v-for="(food, index) in topFoods"
          :key="food.id"
          :style="{ animationDelay: (index * 0.08) + 's' }"
          @click="goToDetail(food.id)"
        >
          <div class="food-image-container">
            <img :src="getImageUrl(food.imageUrl)" :alt="food.title" loading="lazy">
            <div class="food-image-overlay"></div>
            <div class="food-category-pill">{{ food.category }}</div>
          </div>
          <div class="food-details">
            <h3 class="food-name">{{ food.title }}</h3>
            <p class="food-place">
              <svg class="location-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              {{ food.location }}
            </p>
            <div class="food-meta-row">
              <span class="food-hearts">
                <svg viewBox="0 0 24 24" fill="#ff6b6b" stroke="none">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
                {{ food.likeCount }}
              </span>
              <span class="food-price-tag">{{ food.price }}元</span>
            </div>
          </div>
        </article>
      </div>

      <h4 class="section-title">
        <span class="title-accent">📝</span> 最新发布
      </h4>
      <div class="food-editorial-grid">
        <article
          class="food-editorial-card"
          v-for="(food, index) in latestFoods"
          :key="food.id"
          :style="{ animationDelay: (index * 0.08) + 's' }"
          @click="goToDetail(food.id)"
        >
          <div class="food-image-container">
            <img :src="getImageUrl(food.imageUrl)" :alt="food.title" loading="lazy">
            <div class="food-image-overlay"></div>
            <div class="food-category-pill">{{ food.category }}</div>
          </div>
          <div class="food-details">
            <h3 class="food-name">{{ food.title }}</h3>
            <p class="food-place">
              <svg class="location-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              {{ food.location }}
            </p>
            <div class="food-meta-row">
              <span class="food-hearts">
                <svg viewBox="0 0 24 24" fill="#ff6b6b" stroke="none">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
                {{ food.likeCount }}
              </span>
              <span class="food-price-tag">{{ food.price }}元</span>
            </div>
          </div>
        </article>
      </div>

      <nav v-if="totalPages > 1">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: page === 0 }">
            <a class="page-link" href="#" @click.prevent="changePage(page - 1)">上一页</a>
          </li>
          <li class="page-item" v-for="i in totalPages" :key="i" :class="{ active: page === i - 1 }">
            <a class="page-link" href="#" @click.prevent="changePage(i - 1)">{{ i }}</a>
          </li>
          <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
            <a class="page-link" href="#" @click.prevent="changePage(page + 1)">下一页</a>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { foodAPI } from '../api'
import { getImageUrl } from '../utils'
import { useAuth } from '../store/auth'

const router = useRouter()
const { state, clearAuth } = useAuth()
const topFoods = ref([])
const latestFoods = ref([])
const page = ref(0)
const totalPages = ref(0)
const currentSlide = ref(0)
const showMenu = ref(false)
const searchKeyword = ref('')

const doSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: searchKeyword.value } })
    searchKeyword.value = ''
  }
}

const toggleMenu = () => {
  showMenu.value = !showMenu.value
}

const isAdmin = computed(() => state.role === 'ADMIN')
const isMerchant = computed(() => state.role === 'MERCHANT')

const logout = () => {
  clearAuth()
  showMenu.value = false
  router.push('/')
}

const heroSlides = [
  { image: 'https://java-abcde.oss-cn-beijing.aliyuncs.com/1686903314791843.png', title: '发现校园美食', subtitle: '分享美味，发现美食' },
  { image: 'https://java-abcde.oss-cn-beijing.aliyuncs.com/8953.jpg_wh860.jpg', title: '美食推荐', subtitle: '精选校园美食' }
]

let slideTimer = null

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % heroSlides.length
}

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + heroSlides.length) % heroSlides.length
}

const goToSlide = (index) => {
  currentSlide.value = index
}

const startAutoPlay = () => {
  slideTimer = setInterval(nextSlide, 4000)
}

const stopAutoPlay = () => {
  if (slideTimer) {
    clearInterval(slideTimer)
    slideTimer = null
  }
}

const categories = [
  { name: '主食', icon: '🍚', color: 'linear-gradient(135deg, rgba(30,60,90,0.9), rgba(60,90,140,0.9))' },
  { name: '小吃', icon: '🍟', color: 'linear-gradient(135deg, rgba(40,60,100,0.9), rgba(70,100,150,0.9))' },
  { name: '饮品', icon: '🧋', color: 'linear-gradient(135deg, rgba(20,50,80,0.9), rgba(50,80,130,0.9))' },
  { name: '甜品', icon: '🍰', color: 'linear-gradient(135deg, rgba(35,55,95,0.9), rgba(65,95,145,0.9))' }
]

const loadFoods = async () => {
  try {
    const [topRes, latestRes] = await Promise.all([
      foodAPI.getTop(8),
      foodAPI.getAll(page.value, 12)
    ])
    topFoods.value = topRes.data
    latestFoods.value = latestRes.data.content
    totalPages.value = latestRes.data.totalPages
  } catch (error) {
    console.error('加载失败', error)
  }
}

const goToDetail = (id) => {
  router.push(`/food/${id}`)
}

const changePage = (newPage) => {
  page.value = newPage
  loadFoods()
}

onMounted(() => {
  loadFoods()
  startAutoPlay()
})

onUnmounted(() => {
  stopAutoPlay()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: transparent;
}

.menu-toggle {
  position: fixed;
  top: 20px;
  left: 20px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 24px;
  cursor: pointer;
  z-index: 99999;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.home-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(20px);
  padding: 12px 0;
  animation: slideDown 0.3s ease;
}

.home-nav .container {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: nowrap;
}

.home-nav .navbar-brand {
  font-weight: 700;
  color: #fff !important;
  font-size: 1.3rem;
  text-decoration: none;
}

.home-nav .nav-link {
  color: rgba(255, 255, 255, 0.9) !important;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s;
}

.home-nav .nav-link:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff !important;
}

.home-nav .navbar-nav {
  display: flex;
  flex-direction: row;
  list-style: none;
  gap: 8px;
  margin: 0;
  padding: 0;
  margin-left: auto;
}

.home-nav .nav-item {
  display: flex;
}

@keyframes slideDown {
  from { transform: translateY(-100%); }
  to { transform: translateY(0); }
}

@keyframes slideDown {
  from { transform: translateY(-100%); }
  to { transform: translateY(0); }
}

.navbar .container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.navbar-brand {
  font-weight: 700;
  color: #fff !important;
  font-size: 1.3rem;
  text-decoration: none;
}

.navbar-nav {
  display: flex;
  list-style: none;
  gap: 8px;
  margin: 0;
  padding: 0;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9) !important;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff !important;
}

.hero-carousel {
  position: relative;
  width: 100%;
  height: 85vh;
  min-height: 550px;
  overflow: hidden;
}

.hero-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 1s ease;
}

.hero-slide::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: linear-gradient(to bottom, transparent, #faf9f7);
  pointer-events: none;
}

.hero-content {
  text-align: center;
  color: white;
  z-index: 1;
  animation: fadeInUp 1s ease;
}

.hero-content h1 {
  font-size: 4rem;
  font-weight: 800;
  margin-bottom: 1rem;
  letter-spacing: 0.05em;
  color: #fff;
  text-shadow: 0 2px 20px rgba(0,0,0,0.5);
}

.hero-content .lead {
  font-size: 1.5rem;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0,0,0,0.5);
}

.hero-indicators {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 2;
}

.hero-indicators span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}

.hero-indicators span.active {
  background: white;
  transform: scale(1.3);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 56px;
  height: 56px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 2;
  backdrop-filter: blur(4px);
}

.hero-arrow:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: translateY(-50%) scale(1.1);
}

.hero-arrow-left {
  left: 30px;
}

.hero-arrow-right {
  right: 30px;
}

.category-card {
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.category-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.search-bar-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}

.home-search-bar {
  display: flex;
  background: white;
  border-radius: 30px;
  padding: 6px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.12);
  width: 100%;
  max-width: 500px;
}

.home-search-bar input {
  flex: 1;
  border: none;
  padding: 10px 20px;
  font-size: 15px;
  outline: none;
  border-radius: 30px;
  background: transparent;
}

.home-search-bar button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  padding: 10px 24px;
  border-radius: 24px;
  cursor: pointer;
  font-size: 16px;
}

.home-search-bar button:hover {
  opacity: 0.9;
}

.section-title {
  font-family: var(--font-display);
  font-size: 1.6rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 56px 0 36px;
  display: flex;
  align-items: center;
  gap: 14px;
  letter-spacing: -0.02em;
}

.title-accent {
  font-size: 1.2rem;
}

.food-editorial-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 28px;
}

@media (max-width: 1200px) {
  .food-editorial-grid { gap: 20px; }
}

@media (max-width: 992px) {
  .food-editorial-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 768px) {
  .food-editorial-grid { grid-template-columns: repeat(2, 1fr); gap: 16px; }
}

.food-editorial-card {
  cursor: pointer;
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  background: #fefefe;
  box-shadow:
    0 2px 8px rgba(0,0,0,0.04),
    0 8px 24px rgba(0,0,0,0.06);
  transition: all 0.5s cubic-bezier(0.23, 1, 0.32, 1);
  animation: fadeInUp 0.6s ease-out both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.food-editorial-card:hover {
  transform: translateY(-12px);
  box-shadow:
    0 4px 12px rgba(0,0,0,0.06),
    0 20px 50px rgba(0,0,0,0.12);
}

.food-editorial-card:hover .food-image-container img {
  transform: scale(1.08);
}

.food-editorial-card:hover .food-image-overlay {
  opacity: 0.3;
}

.food-image-container {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.food-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.7s cubic-bezier(0.23, 1, 0.32, 1);
}

.food-image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    transparent 0%,
    transparent 50%,
    rgba(0,0,0,0.6) 100%
  );
  opacity: 0.5;
  transition: opacity 0.5s ease;
}

.food-category-pill {
  position: absolute;
  top: 16px;
  left: 16px;
  background: rgba(255,255,255,0.95);
  color: #c75b39;
  font-size: 0.7rem;
  font-weight: 700;
  padding: 6px 14px;
  border-radius: 30px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  backdrop-filter: blur(4px);
}

.food-details {
  padding: 20px;
}

.food-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 8px;
  line-height: 1.35;
  letter-spacing: -0.01em;
}

.food-place {
  font-size: 0.82rem;
  color: #888;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.location-icon {
  width: 14px;
  height: 14px;
  color: #c75b39;
  opacity: 0.7;
}

.food-meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  border-top: 1px solid #f0f0f0;
}

.food-hearts {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #555;
}

.food-hearts svg {
  width: 16px;
  height: 16px;
}

.food-price-tag {
  font-size: 1.05rem;
  font-weight: 800;
  color: #c75b39;
  letter-spacing: -0.02em;
}
</style>
