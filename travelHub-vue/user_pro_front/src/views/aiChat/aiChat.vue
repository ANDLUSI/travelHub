<template>
  <div class="background-big">
    <div class="container">
      <!-- 头部 -->
      <header class="header">
        <div class="logo">
          <h1 class="title">旅游小助手</h1>
          <span class="version">1.0</span>
        </div>
      </header>

      <!-- 主体内容 -->
      <main class="main-content">
        <div class="chat-panel">
          <div class="chat-container" ref="chatContainer">
            <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
              <img v-if="message.type === 'bot' || message.type === 'thinking'" class="bot-avatar" src="../../assets/images/ai.jpg" />
              <div class="message-content">
                <span>{{ message.displayContent }}</span>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="input-area">
            <el-input
                v-model="userInput"
                placeholder="您想了解哪些旅游的信息？"
                @keyup.enter="onSend"
                :disabled="isTyping"
            />
            <el-button type="primary" @click="onSend" :disabled="isTyping">发送</el-button>
          </div>
        </div>

        <!-- 小贴士 -->
        <section class="tips-section">
          <div class="health-tips">
            <h3 class="tips-title">健康小贴士：</h3>
            <p class="tips-content">
              我可以为您提供旅游资讯方面的信息和知识，回答您的问题，帮助您解决困惑或疑问。
              无论您遇到什么旅游方面的疑问，我都会尽力给您最准确、详细的答案。
            </p>
          </div>
          <div class="disclaimer">
            如遇回答不完整，可以输入（请继续回答刚才未完成的答案）即可继续回复。
            由于系统基于国内AI接口混合开发，仅用于旅游知识回答，禁止一切非法活动。
          </div>
        </section>
      </main>
    </div>
  </div>

</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const sessionId = ref('')
const userInput = ref('')
const messages = ref([])
const isTyping = ref(false)
const typingSpeed = ref(50)
const chatContainer = ref(null)

// 页面加载
onMounted(async () => {
  let localSessionId = localStorage.getItem('sessionId')
  if (!localSessionId) {
    localSessionId = createSessionId()
    localStorage.setItem('sessionId', localSessionId)
  }
  sessionId.value = localSessionId

  try {
    const res = await fetch(`http://localhost:8080/session/getSessionData?sessionId=${sessionId.value}`)
    const data = await res.json()
    if (data.sessionData) {
      messages.value.push(...data.sessionData.map(msg => ({
        ...msg,
        displayContent: msg.content
      })))
    }
    scrollToBottom()
  } catch (err) {
    console.error('获取会话失败:', err)
  }
})

const createSessionId = () => {
  return 'session_' + Math.random().toString(36).substr(2, 9)
}

const onSend = () => {
  if (!userInput.value.trim() || isTyping.value) return
  sendMessage()
}

const sendMessage = async () => {
  const content = userInput.value.trim()
  if (!content) return

  // 添加用户消息
  const userMessage = { type: 'user', content, displayContent: content }
  messages.value.push(userMessage)
  userInput.value = ''
  scrollToBottom()

  // 添加机器人“正在思考中...”的消息
  const thinkingMessage = {
    type: 'thinking',
    content: '',
    displayContent: '机器人正在思考中...',
    thinkingInterval: null
  }
  messages.value.push(thinkingMessage)
  scrollToBottom()
  startThinkingDots(thinkingMessage)

  try {
    const payload = {
      assistant_id: '67f0c2c30e96300fa3874977',
      prompt: content
    }

    const res = await fetch('https://chatglm.cn/chatglm/assistant-api/v1/stream_sync', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTc0ODM0OTY2NywianRpIjoiMzM0YTZjNjYtYzhkOC00NWEzLWExZjgtZGVmNWI4MTkzMTE0IiwidHlwZSI6ImFjY2VzcyIsInN1YiI6IkFQSV82NzI0NjRhNjMxMDIzNTg1NDhjMDBhYzlfMWRjNzhhYzMiLCJuYmYiOjE3NDgzNDk2NjcsImV4cCI6MTc0OTIxMzY2NywidWlkIjoiNjc1NmQ2M2VkOGQ5ZmMwZmZhYzFjYWNlIiwidXBsYXRmb3JtIjoiIiwiYXBpX3JvbGUiOiJkZXZlbG9wZXIiLCJyb2xlcyI6WyJhdXRoZWRfdXNlciJdfQ.kdAN0RIX1uLHqnadBn6ny73Q1KdSzSUcn4ThLffwPUg'
      },
      body: JSON.stringify(payload)
    })

    const json = await res.json()
    const text = json?.result?.output?.[0]?.content?.[0]?.text

    if (!text) {
      ElMessage.error('未接收到有效响应数据')
      clearThinkingDots(thinkingMessage)
      return
    }

    // 替换“思考中”消息为真正回答
    clearThinkingDots(thinkingMessage)
    const thinkingIndex = messages.value.indexOf(thinkingMessage)
    if (thinkingIndex !== -1) {
      messages.value.splice(thinkingIndex, 1)
    }

    const botMessage = {
      type: 'bot',
      content: text,
      displayContent: text
    }
    messages.value.push(botMessage)
    typeMessage(messages.value.length - 1)
    saveSession()
  } catch (err) {
    console.error('请求失败', err)
    ElMessage.error('发送失败')
    clearThinkingDots(thinkingMessage)
  }
}

const typeMessage = (index) => {
  const message = messages.value[index]
  const fullText = message.content
  let currentIndex = 0
  isTyping.value = true

  const typeNextChar = () => {
    if (currentIndex < fullText.length) {
      message.displayContent = fullText.substring(0, currentIndex + 1)
      currentIndex++
      nextTick(() => scrollToBottom())
      setTimeout(typeNextChar, typingSpeed.value)
    } else {
      isTyping.value = false
    }
  }

  typeNextChar()
}

const startThinkingDots = (message) => {
  let dotCount = 1
  message.thinkingInterval = setInterval(() => {
    message.displayContent = 'AI助手正在思考中' + '.'.repeat(dotCount)
    dotCount = dotCount === 3 ? 1 : dotCount + 1

    // 确保 UI 渲染更新
    nextTick(() => scrollToBottom())
  }, 500)
}


const clearThinkingDots = (message) => {
  if (message.thinkingInterval) {
    clearInterval(message.thinkingInterval)
    message.thinkingInterval = null
  }
}

const saveSession = async () => {
  try {
    const res = await fetch('http://localhost:8080/session/UpdateSession', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        sessionId: sessionId.value,
        messages: messages.value
      })
    })
    console.log('保存会话成功:', await res.json())
  } catch (err) {
    console.error('保存会话失败:', err)
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    const container = chatContainer.value
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  })
}
</script>

<style scoped>
.background-big{
  width: 100%;
  height: 100%;
  background-image: url("../../assets/images/long_background.jpg");
  background-repeat: no-repeat;
}
.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 20px;
}

.logo {
  display: flex;
  justify-content: center;
  align-items: center;
}

.title {
  font-size: 26px;
  font-weight: bold;
}

.version {
  font-size: 16px;
  margin-left: 10px;
  color: #666;
}

.main-content {
  background-color: #f5f5f5;
  padding: 20px;
  border-radius: 12px;
}

.chat-panel {
  display: flex;
  flex-direction: column;
  height: 500px;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  background: #fff;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 10px;
}

.message {
  display: flex;
  margin-bottom: 16px;
}

.message.user {
  justify-content: flex-end;
}

.message.bot {
  justify-content: flex-start;
}

.message.thinking .message-content {
  background-color: #f0f0f0;
  color: #888;
  font-style: italic;
}

.bot-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.message-content {
  max-width: 60%;
  background-color: #007aff;
  color: white;
  padding: 10px 15px;
  border-radius: 8px;
}

.message.bot .message-content {
  background-color: #eee;
  color: #333;
}

.input-area {
  display: flex;
  gap: 10px;
}

.tips-section {
  margin-top: 20px;
  background-color: #fff;
  padding: 15px;
  border-radius: 10px;
}

.tips-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.tips-content {
  font-size: 14px;
  color: #555;
}

.disclaimer {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}
</style>
