<template>
  <div>
    <nav class="navbar navbar-expand-lg" v-if="!isLoginPage">
      <div class="container">
        <router-link class="navbar-brand" to="/home">🍜 校园美食</router-link>

        <!-- 搜索栏居中 -->
        <div class="navbar-search">
          <input
            type="text"
            class="search-input"
            v-model="searchKeyword"
            placeholder="搜索美食..."
            @keyup.enter="doSearch"
          >
          <button class="search-btn" @click="doSearch">🔍</button>
        </div>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <router-link class="nav-link" to="/home">首页</router-link>
            </li>
            <template v-if="state.token && state.token !== 'visitor'">
              <li class="nav-item">
                <router-link class="nav-link" to="/create">发布美食</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/favorites">收藏</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/profile">我的</router-link>
              </li>
              <li class="nav-item" v-if="state.role === 'ADMIN'">
                <router-link class="nav-link" to="/admin">管理</router-link>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="#" @click.prevent="logout">退出</a>
              </li>
            </template>
            <template v-else-if="state.token === 'visitor'">
              <li class="nav-item">
                <span class="nav-link text-muted">游客</span>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="#" @click.prevent="logout">登录</a>
              </li>
            </template>
            <template v-else>
              <li class="nav-item">
                <router-link class="nav-link" to="/login">登录</router-link>
              </li>
            </template>
          </ul>
        </div>
      </div>
    </nav>
    <router-view />
    <AIChatWidget v-if="state.token && state.token !== 'visitor' && state.role === 'USER'" />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuth } from './store/auth'
import AIChatWidget from './components/AIChatWidget.vue'

const router = useRouter()
const route = useRoute()
const { state, clearAuth } = useAuth()
const searchKeyword = ref('')

const isLoginPage = computed(() => ['/', '/login', '/register', '/home', '/admin'].includes(route.path))

const logout = () => {
  clearAuth()
  router.push('/')
}

const doSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: searchKeyword.value } })
    searchKeyword.value = ''
  }
}
</script>
