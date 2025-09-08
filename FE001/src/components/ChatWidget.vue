<template>
  <div v-if="authStore.isAuthenticated" class="fixed bottom-20 right-6 z-50">
    <!-- Chat Window -->
    <div 
      v-if="isOpen" 
      class="bg-white rounded-lg shadow-2xl w-80 h-96 border border-gray-200 mb-4 transition-all duration-300 transform"
      :class="isOpen ? 'scale-100 opacity-100' : 'scale-95 opacity-0'"
    >
      <!-- Chat Header -->
      <div class="bg-blue-500 text-white p-4 rounded-t-lg flex items-center justify-between relative">
        <div class="flex items-center">
          <div 
            @click="toggleBotMenu" 
            class="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center mr-3 cursor-pointer hover:bg-blue-700 transition-colors relative"
          >
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.568 8.16c-.169 1.858-.896 6.728-.896 6.728-.302 1.408-1.125 1.653-2.29 1.027L11.45 14.05l-1.347 1.297c-.149.149-.275.275-.562.275l.2-2.831 5.154-4.653c.224-.2-.049-.312-.347-.112L7.862 12.32l-2.76-.918c-.6-.186-.612-.6.126-.889L20.11 7.19c.498-.184.936.112.778.889z"/>
            </svg>
            
            <!-- Bot Menu Dropdown -->
            <div 
              v-if="showBotMenu" 
              class="absolute top-full left-0 mt-2 bg-white text-gray-800 rounded-lg shadow-lg border border-gray-200 min-w-40 z-10"
              @click.stop
            >
              <button 
                @click="goToTelegram"
                class="w-full px-4 py-3 text-left hover:bg-gray-50 rounded-t-lg transition-colors flex items-center"
              >
                <svg class="w-4 h-4 mr-2 text-blue-500" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.568 8.16c-.169 1.858-.896 6.728-.896 6.728-.302 1.408-1.125 1.653-2.29 1.027L11.45 14.05l-1.347 1.297c-.149.149-.275.275-.562.275l.2-2.831 5.154-4.653c.224-.2-.049-.312-.347-.112L7.862 12.32l-2.76-.918c-.6-.186-.612-.6.126-.889L20.11 7.19c.498-.184.936.112.778.889z"/>
                </svg>
                Sang Telegram
              </button>
              <button 
                @click="closeChat"
                class="w-full px-4 py-3 text-left hover:bg-gray-50 rounded-b-lg transition-colors flex items-center"
              >
                <svg class="w-4 h-4 mr-2 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
                Đóng
              </button>
            </div>
          </div>
          <div>
            <h3 class="font-semibold text-sm">Web3 Support Bot</h3>
            <div class="flex items-center">
              <div class="w-2 h-2 bg-green-400 rounded-full mr-1"></div>
              <span class="text-xs opacity-90">Đang hoạt động</span>
            </div>
          </div>
        </div>
        <button @click="closeChat" class="text-white hover:text-gray-200">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>

      <!-- Chat Messages -->
      <div class="h-64 overflow-y-auto p-4 bg-gray-50" ref="messagesContainer">
        <div v-for="message in messages" :key="message.id" class="mb-3">
          <div 
            class="max-w-xs p-3 rounded-lg text-sm relative"
            :class="message.isBot ? 'bg-white text-gray-800 mr-auto' : 'bg-blue-500 text-white ml-auto'"
          >
            <div v-if="message.isBot" class="font-medium text-blue-600 text-xs mb-1">Bot Support</div>
            {{ message.text }}
            <div class="text-xs opacity-70 mt-1">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>
        <div v-if="isTyping" class="flex items-center text-gray-500 text-sm">
          <div class="flex space-x-1 mr-2">
            <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0ms"></div>
            <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 150ms"></div>
            <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 300ms"></div>
          </div>
          Bot đang trả lời...
        </div>
      </div>

      <!-- Chat Input -->
      <div class="p-4 border-t border-gray-200">
        <div class="flex space-x-2">
          <input
            v-model="currentMessage"
            @keypress.enter="sendMessage"
            type="text"
            placeholder="Nhập tin nhắn..."
            class="flex-1 border border-gray-300 rounded-full px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
          <button
            @click="sendMessage"
            :disabled="!currentMessage.trim()"
            class="bg-blue-500 text-white p-2 rounded-full hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path>
            </svg>
          </button>
        </div>
        
        <!-- Quick Actions -->
        <div class="mt-3 flex flex-wrap gap-2">
          <button
            v-for="action in quickActions"
            :key="action.text"
            @click="sendQuickMessage(action.text)"
            class="text-xs bg-gray-100 hover:bg-gray-200 text-gray-700 px-3 py-1 rounded-full transition-colors"
          >
            {{ action.text }}
          </button>
        </div>
      </div>
    </div>

    <!-- Chat Toggle Button - Only show when closed -->
    <button
      v-if="!isOpen"
      @click="toggleChat"
      class="bg-blue-500 hover:bg-blue-600 text-white p-4 rounded-full shadow-lg transition-all duration-300 transform hover:scale-110 group"
    >
      <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
        <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.568 8.16c-.169 1.858-.896 6.728-.896 6.728-.302 1.408-1.125 1.653-2.29 1.027L11.45 14.05l-1.347 1.297c-.149.149-.275.275-.562.275l.2-2.831 5.154-4.653c.224-.2-.049-.312-.347-.112L7.862 12.32l-2.76-.918c-.6-.186-.612-.6.126-.889L20.11 7.19c.498-.184.936.112.778.889z"/>
      </svg>
      
      <!-- Notification Badge -->
      <span v-if="unreadCount > 0" class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
        {{ unreadCount }}
      </span>
      
      <!-- Pulse animation -->
      <span class="absolute top-0 right-0 w-3 h-3 bg-green-400 rounded-full animate-ping"></span>
      <span class="absolute top-0 right-0 w-3 h-3 bg-green-500 rounded-full"></span>
    </button>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080'
const authStore = useAuthStore()

const getUserAddress = () => {
  return authStore.walletAddress || authStore.user?.address || 'default_address'
}

const isOpen = ref(false)
const currentMessage = ref('')
const isTyping = ref(false)
const unreadCount = ref(0)
const messagesContainer = ref(null)
const showBotMenu = ref(false)
const pollingInterval = ref(null)
const isPolling = ref(false)

const messages = ref([
  {
    id: 1,
    text: 'Xin chào! Tôi là Bot hỗ trợ Web3 Auth. Tôi có thể giúp gì cho bạn?',
    isBot: true,
    timestamp: new Date()
  }
])

const quickActions = ref([
  { text: '/help' },
  { text: '/status' },
  { text: '/info' },
  { text: 'Cần hỗ trợ đăng nhập' }
])

const goToTelegram = () => {
  const botUsername = 'buildweb3_bot'
  const telegramUrl = `https://t.me/${botUsername}`
  window.open(telegramUrl, '_blank')
  
  showBotMenu.value = false
  
  messages.value.push({
    id: Date.now(),
    text: '🚀 Đã mở Telegram Bot trong tab mới!\n\nBạn có thể tiếp tục chat ở đây hoặc chuyển sang Telegram để có trải nghiệm đầy đủ hơn.',
    isBot: true,
    timestamp: new Date()
  })
  
  scrollToBottom()
}


const fetchTelegramMessages = async () => {
  try {
    const userAddress = getUserAddress()
    const response = await axios.get(`${API_BASE_URL}/chat/messages`, {
      params: { userAddress }
    })
    
    const telegramMessages = response.data.data
    if (telegramMessages && telegramMessages.length > 0) {
      telegramMessages.forEach(msg => {
        messages.value.push({
          id: Date.now() + Math.random(),
          text: msg.message,
          isBot: msg.isBot,
          timestamp: new Date(msg.timestamp),
          fromTelegram: true
        })
      })
      
      if (!isOpen.value) {
        unreadCount.value += telegramMessages.length
      }
      
      scrollToBottom()
    }
  } catch (error) {
    console.error('Failed to fetch Telegram messages:', error)
  }
}

const startPolling = () => {
  if (isPolling.value) return
  isPolling.value = true
  pollingInterval.value = setInterval(fetchTelegramMessages, 3000)
}

const stopPolling = () => {
  if (pollingInterval.value) {
    clearInterval(pollingInterval.value)
    pollingInterval.value = null
  }
  isPolling.value = false
}

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    unreadCount.value = 0
    startPolling()
  } else {
    stopPolling()
  }
}

const closeChat = () => {
  isOpen.value = false
  showBotMenu.value = false
  stopPolling()
}

const toggleBotMenu = () => {
  showBotMenu.value = !showBotMenu.value
}

const sendMessageToBackend = async (message, userAddress) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/chat/send`, {
      message: message,
      userAddress: userAddress,
      chatId: `web_session_${Date.now()}`
    })
    return response.data.data
  } catch (error) {
    console.error('API call failed:', error)
    throw error
  }
}

const sendMessage = async () => {
  if (!currentMessage.value.trim()) return
  
  // Add user message to UI
  const userMessage = {
    id: Date.now(),
    text: currentMessage.value,
    isBot: false,
    timestamp: new Date()
  }
  messages.value.push(userMessage)
  
  const userText = currentMessage.value
  currentMessage.value = ''
  isTyping.value = true
  
  try {
    const userAddress = getUserAddress()
    const response = await sendMessageToBackend(userText, userAddress)
    
    messages.value.push({
      id: Date.now() + 1,
      text: response.message,
      isBot: true,
      timestamp: new Date(response.timestamp),
      sentToTelegram: response.sentToTelegram
    })
    
  } catch (error) {
    const botResponse = getBotResponse(userText)
    messages.value.push({
      id: Date.now() + 1,
      text: botResponse + '\n\n⚠️ (Offline mode)',
      isBot: true,
      timestamp: new Date()
    })
  } finally {
    isTyping.value = false
    scrollToBottom()
  }
}

const sendQuickMessage = (text) => {
  currentMessage.value = text
  sendMessage()
}

const getBotResponse = (userText) => {
  const text = userText.toLowerCase()
  
  if (text.includes('/help')) {
    return 'Các lệnh có sẵn:\n/help - Trợ giúp\n/status - Trạng thái hệ thống\n/info - Thông tin\n\nBạn cũng có thể hỏi về đăng nhập, ví, hoặc các vấn đề kỹ thuật!'
  } else if (text.includes('/status')) {
    return 'Hệ thống đang hoạt động bình thường!\nBackend: Kết nối thành công\nFrontend: Đang chạy\n' + new Date().toLocaleString('vi-VN')
  } else if (text.includes('/info')) {
    return 'Web3 Authentication System\nSpring Boot + Ethereum\nVue.js Frontend\nTelegram Bot Integration\n\nHệ thống xác thực phi tập trung sử dụng chữ ký MetaMask!'
  } else if (text.includes('đăng nhập') || text.includes('login')) {
    return 'Hỗ trợ đăng nhập:\n\n1. Đảm bảo MetaMask đã cài đặt\n2. Kết nối ví\n3. Ký thông điệp xác thực\n4. Hoàn tất đăng nhập\n\nBạn gặp lỗi ở bước nào?'
  } else if (text.includes('metamask') || text.includes('ví')) {
    return 'MetaMask Support:\n\n- Tải MetaMask: https://metamask.io\n- Tạo ví mới hoặc import\n- Kết nối với ứng dụng\n- Ký thông điệp để xác thực\n\nCần hỗ trợ cụ thể gì về ví?'
  } else if (text.includes('lỗi') || text.includes('error')) {
    return 'Hỗ trợ lỗi:\n\n• Lỗi kết nối ví\n• Lỗi chữ ký\n• Lỗi mạng\n• Lỗi server\n\nVui lòng mô tả chi tiết lỗi bạn gặp phải!'
  } else if (text.includes('cảm ơn') || text.includes('thanks')) {
    return 'Rất vui được hỗ trợ bạn! Nếu có thêm câu hỏi, đừng ngần ngại nhé!'
  } else {
    return 'Tôi hiểu bạn đang hỏi về: "' + userText + '"\n\nRe Send!'
  }
}

const formatTime = (timestamp) => {
  return timestamp.toLocaleTimeString('vi-VN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const handleClickOutside = (event) => {
  if (showBotMenu.value && !event.target.closest('.relative')) {
    showBotMenu.value = false
  }
}

onMounted(() => {
  scrollToBottom()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  stopPolling()
})
</script>