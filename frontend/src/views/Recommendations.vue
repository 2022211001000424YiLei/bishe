<template>
  <div class="chat-container">
    <div class="chat-header">
      <h4>🍜 AI 美食助手</h4>
      <p class="text-muted mb-0">告诉我你想吃什么，我给你推荐校园美食</p>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
        <div class="message-avatar">
          {{ msg.role === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="message-content">
          <div class="message-text" v-html="formatMessage(msg.content)"></div>
          <div v-if="msg.foods" class="message-foods">
            <div class="row">
              <div class="col-md-6 col-lg-4 mb-3" v-for="food in msg.foods" :key="food.id">
                <div class="food-card-mini" @click="goToDetail(food.id)">
                  <img :src="getImageUrl(food.imageUrl)" :alt="food.title">
                  <div class="food-info">
                    <h6>{{ food.title }}</h6>
                    <small>{{ food.location }}</small>
                    <span class="badge bg-primary">{{ food.category }}</span>
                  </div>
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
        placeholder="输入你的口味偏好，如：我想吃辣的，便宜的..."
        @keyup.enter="sendMessage"
        :disabled="loading"
      >
      <button @click="sendMessage" :disabled="loading || !userInput.trim()">
        {{ loading ? '...' : '发送' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { recommendationAPI } from '../api'
import { getImageUrl } from '../utils'

const router = useRouter()
const messages = ref([])
const userInput = ref('')
const loading = ref(false)
const messagesContainer = ref(null)

const welcomeMsg = `你好！我是校园美食AI助手 🎉

我可以根据你的口味偏好为你推荐美食，比如：
• "我想吃辣的"
• "推荐一些甜品"
• "便宜又好吃的"
• "食堂的美食有哪些"

告诉我你想吃什么吧~`

onMounted(() => {
  messages.value.push({
    role: 'bot',
    content: welcomeMsg,
    foods: null
  })
})

const sendMessage = async () => {
  if (!userInput.value.trim() || loading.value) return

  const input = userInput.value.trim()
  userInput.value = ''

  messages.value.push({
    role: 'user',
    content: input,
    foods: null
  })

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
.chat-container {
  max-width: 800px;
  margin: 20px auto;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(135deg, #ff9966, #ff5e62);
  color: white;
  padding: 20px;
  text-align: center;
}

.chat-header h4 {
  margin-bottom: 5px;
}

.chat-messages {
  height: 450px;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.message.user .message-avatar {
  background: #ff9966;
}

.message.bot .message-avatar {
  background: #4CAF50;
}

.message-content {
  max-width: 70%;
}

.message-text {
  background: #fff;
  padding: 12px 16px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  line-height: 1.6;
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
  margin-top: 12px;
}

.food-card-mini {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.food-card-mini:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.food-card-mini img {
  width: 100%;
  height: 100px;
  object-fit: cover;
}

.food-card-mini .food-info {
  padding: 10px;
}

.food-card-mini h6 {
  margin: 0 0 4px 0;
  font-size: 14px;
}

.food-card-mini small {
  display: block;
  color: #666;
  margin-bottom: 6px;
}

.typing {
  display: flex;
  gap: 4px;
  padding: 16px 20px;
}

.typing span {
  width: 8px;
  height: 8px;
  background: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-8px); opacity: 1; }
}

.chat-input {
  display: flex;
  padding: 16px;
  background: white;
  border-top: 1px solid #eee;
  gap: 12px;
}

.chat-input input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #eee;
  border-radius: 24px;
  outline: none;
  font-size: 15px;
  transition: border-color 0.2s;
}

.chat-input input:focus {
  border-color: #ff9966;
}

.chat-input button {
  padding: 12px 24px;
  background: linear-gradient(135deg, #ff9966, #ff5e62);
  color: white;
  border: none;
  border-radius: 24px;
  cursor: pointer;
  font-weight: 600;
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
