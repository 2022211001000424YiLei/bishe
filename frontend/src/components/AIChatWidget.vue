<template>
  <div class="chat-widget">
    <!-- 悬浮按钮 -->
    <button class="chat-toggle" @click="toggleChat" v-if="!isOpen">
      <span class="chat-toggle-icon">💬</span>
      <span class="chat-toggle-badge" v-if="hasNewMessage">1</span>
    </button>

    <!-- 聊天窗口 -->
    <div class="chat-window" v-if="isOpen">
      <div class="chat-header" @click="toggleChat">
        <div class="chat-header-info">
          <span class="chat-header-icon">🤖</span>
          <div>
            <h6>AI 美食助手</h6>
            <small>随时为你推荐美食</small>
          </div>
        </div>
        <button class="chat-close">−</button>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
          <div class="message-avatar">
            {{ msg.role === 'user' ? '👤' : '🤖' }}
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(msg.content)"></div>
            <div v-if="msg.foods && msg.foods.length > 0" class="message-foods">
              <div class="food-grid">
                <div class="food-item" v-for="food in msg.foods" :key="food.id" @click="goToDetail(food.id)">
                  <img :src="getImageUrl(food.imageUrl)" :alt="food.title">
                  <div class="food-item-info">
                    <span class="food-title">{{ food.title }}</span>
                    <span class="food-price">{{ food.price }}元</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="loading" class="message bot">
          <div class="message-avatar">🤖</div>
          <div class="message-content">
            <div class="message-text typing">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <input
          type="text"
          v-model="userInput"
          placeholder="说说你想吃什么..."
          @keyup.enter="sendMessage"
          :disabled="loading"
        >
        <button @click="sendMessage" :disabled="loading || !userInput.trim()">
          {{ loading ? '...' : '➤' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { recommendationAPI } from '../api'
import { getImageUrl } from '../utils'

const router = useRouter()
const isOpen = ref(false)
const messages = ref([])
const userInput = ref('')
const loading = ref(false)
const hasNewMessage = ref(false)
const messagesContainer = ref(null)

const welcomeMsg = `你好！我是校园美食AI助手 🍜

告诉我你想吃什么，我帮你推荐：
• "想吃辣的"
• "推荐甜品"
• "便宜的食物"`

watch(isOpen, (newVal) => {
  if (newVal && messages.value.length === 0) {
    messages.value.push({ role: 'bot', content: welcomeMsg, foods: [] })
  }
})

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    hasNewMessage.value = false
  }
}

const sendMessage = async () => {
  if (!userInput.value.trim() || loading.value) return

  const input = userInput.value.trim()
  userInput.value = ''

  messages.value.push({ role: 'user', content: input, foods: [] })
  await scrollToBottom()
  loading.value = true

  try {
    const res = await recommendationAPI.chat({ message: input })
    const data = res.data

    messages.value.push({
      role: 'bot',
      content: data.response,
      foods: data.foods || []
    })

    if (!isOpen.value) {
      hasNewMessage.value = true
    }
  } catch (err) {
    messages.value.push({
      role: 'bot',
      content: '抱歉，AI服务暂时不可用，请稍后再试。',
      foods: []
    })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

const formatMessage = (text) => {
  if (!text) return ''
  return text.replace(/\n/g, '<br>')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const goToDetail = (id) => {
  router.push(`/food/${id}`)
}
</script>

<style scoped>
.chat-widget {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 9999;
}

.chat-toggle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9966, #ff5e62);
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(255, 99, 102, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: transform 0.3s, box-shadow 0.3s;
}

.chat-toggle:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(255, 99, 102, 0.5);
}

.chat-toggle-icon {
  font-size: 28px;
}

.chat-toggle-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 20px;
  height: 20px;
  background: #ff4d8d;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-window {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 380px;
  height: 520px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chat-header {
  background: linear-gradient(135deg, #ff9966, #ff5e62);
  color: white;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.chat-header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-header-icon {
  font-size: 28px;
}

.chat-header-info h6 {
  margin: 0;
  font-size: 15px;
}

.chat-header-info small {
  opacity: 0.9;
}

.chat-close {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.2s;
}

.chat-close:hover {
  background: rgba(255,255,255,0.3);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f5f5;
}

.message {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.message.user .message-avatar {
  background: #ff9966;
}

.message.bot .message-avatar {
  background: #4CAF50;
}

.message-content {
  max-width: 75%;
}

.message-text {
  background: #fff;
  padding: 10px 14px;
  border-radius: 14px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  line-height: 1.5;
  font-size: 14px;
  white-space: pre-wrap;
}

.message.user .message-text {
  background: #ff9966;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.bot .message-text {
  border-bottom-left-radius: 4px;
}

.message-foods {
  margin-top: 8px;
}

.food-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.food-item {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.food-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 10px rgba(0,0,0,0.15);
}

.food-item img {
  width: 100%;
  height: 70px;
  object-fit: cover;
}

.food-item-info {
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.food-title {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.food-price {
  font-size: 11px;
  color: #ff9966;
  font-weight: 600;
}

.typing {
  display: flex;
  gap: 4px;
  padding: 14px 18px;
}

.typing span {
  width: 6px;
  height: 6px;
  background: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

.chat-input {
  display: flex;
  padding: 12px;
  background: white;
  border-top: 1px solid #eee;
  gap: 8px;
}

.chat-input input {
  flex: 1;
  padding: 10px 14px;
  border: 2px solid #eee;
  border-radius: 20px;
  outline: none;
  font-size: 14px;
  transition: border-color 0.2s;
}

.chat-input input:focus {
  border-color: #ff9966;
}

.chat-input button {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9966, #ff5e62);
  color: white;
  border: none;
  cursor: pointer;
  font-size: 18px;
  transition: opacity 0.2s;
}

.chat-input button:hover {
  opacity: 0.9;
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
