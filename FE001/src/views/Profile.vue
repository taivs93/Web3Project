<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
    <!-- Navigation -->
    <nav class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <h1 class="text-xl font-bold text-gray-900">Web3 Auth App</h1>
          </div>
          <div class="flex items-center space-x-4">
            <!-- Hiển thị thông tin user nếu đã đăng nhập -->
            <div v-if="authStore.isAuthenticated" class="flex items-center space-x-4">
              <div class="text-sm text-gray-700">
                <span class="font-medium">{{ authStore.user?.username || 'User' }}</span>
                <span class="text-gray-500 ml-2">({{ authStore.shortAddress }})</span>
                <span v-if="authStore.user?.telegramUserId" class="ml-2 text-blue-600">
                  <svg class="w-4 h-4 inline" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.568 8.16c-.169 1.858-.896 6.728-.896 6.728-.302 1.408-1.125 1.653-2.29 1.027L11.45 14.05l-1.347 1.297c-.149.149-.275.275-.562.275l.2-2.831 5.154-4.653c.224-.2-.049-.312-.347-.112L7.862 12.32l-2.76-.918c-.6-.186-.612-.6.126-.889L20.11 7.19c.498-.184.936.112.778.889z"/>
                  </svg>
                </span>
              </div>
              <router-link
                to="/"
                class="text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
              >
                Trang chủ
              </router-link>
              <button
                @click="logout"
                class="bg-red-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-red-700 transition-colors"
              >
                Đăng xuất
              </button>
            </div>
            <!-- Hiển thị nút đăng nhập nếu chưa đăng nhập -->
            <div v-else>
              <router-link
                to="/login"
                class="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700 transition-colors"
              >
                Đăng nhập
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Loading State -->
      <div v-if="authStore.isLoading" class="text-center py-12">
        <svg class="animate-spin h-12 w-12 text-indigo-600 mx-auto" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p class="mt-4 text-gray-600">Đang tải thông tin...</p>
      </div>

      <!-- Profile Content -->
      <div v-else class="space-y-8">
        <!-- Wallet Information -->
        <div class="bg-white shadow rounded-lg p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Thông tin Ví</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Địa chỉ ví</label>
              <div class="mt-1 flex items-center">
                <span class="font-mono text-sm text-gray-900">{{ authStore.walletAddress }}</span>
                <button
                  @click="copyAddress"
                  class="ml-2 text-indigo-600 hover:text-indigo-500"
                  title="Sao chép địa chỉ"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"></path>
                  </svg>
                </button>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Ngày đăng ký</label>
              <div class="mt-1">
                <span class="text-sm text-gray-900">{{ formatDate(authStore.user?.createdAt) }}</span>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Trạng thái</label>
              <div class="mt-1">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                  <svg class="w-2 h-2 mr-1" fill="currentColor" viewBox="0 0 8 8">
                    <circle cx="4" cy="4" r="3" />
                  </svg>
                  Đã kết nối
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- User Profile Form -->
        <div class="bg-white shadow rounded-lg p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-6">Thông tin cá nhân</h2>
          
          <!-- Current User Info Display -->
          <div v-if="authStore.user" class="mb-6 p-4 bg-gray-50 rounded-lg">
            <h3 class="text-sm font-medium text-gray-700 mb-3">Thông tin hiện tại:</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
              <div class="space-y-2">
                <div class="flex items-center">
                  <span class="font-medium w-24">ID:</span> 
                  <span class="text-gray-900">{{ authStore.user.id || 'Chưa có' }}</span>
                </div>
                <div class="flex items-center">
                  <span class="font-medium w-24">Tên:</span> 
                  <span class="text-gray-900">{{ authStore.user.username || 'Chưa có' }}</span>
                </div>
                <div class="flex items-center">
                  <span class="font-medium w-24">Email:</span> 
                  <span class="text-gray-900">{{ authStore.user.email || 'Chưa có' }}</span>
                </div>
                <div class="flex items-center">
                  <span class="font-medium w-24">Trạng thái:</span> 
                  <span :class="authStore.user.isActive ? 'text-green-600' : 'text-red-600'">
                    {{ authStore.user.isActive ? 'Hoạt động' : 'Không hoạt động' }}
                  </span>
                </div>
              </div>
              <div class="space-y-2">
                <div class="flex items-center">
                  <span class="font-medium w-24">Telegram:</span> 
                  <div class="flex items-center">
                    <span v-if="authStore.user.telegramUserId" class="text-blue-600 font-mono">
                      ID: {{ authStore.user.telegramUserId }}
                    </span>
                    <span v-else class="text-gray-500">Chưa liên kết</span>
                    <span v-if="authStore.user.telegramUserId" class="ml-2 text-green-500">
                      <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
                      </svg>
                    </span>
                  </div>
                </div>
                <div class="flex items-center">
                  <span class="font-medium w-24">Ví:</span> 
                  <span class="text-gray-900 font-mono text-xs">{{ authStore.walletAddress }}</span>
                </div>
                <div class="flex items-center">
                  <span class="font-medium w-24">Tạo lúc:</span> 
                  <span class="text-gray-900">{{ formatDate(authStore.user.createdAt) }}</span>
                </div>
                <div class="flex items-center">
                  <span class="font-medium w-24">Đăng nhập cuối:</span> 
                  <span class="text-gray-900">{{ formatDate(authStore.user.lastLoginAt) }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <form @submit.prevent="updateProfile" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label for="username" class="block text-sm font-medium text-gray-700">Tên người dùng</label>
                <input
                  id="username"
                  v-model="form.username"
                  type="text"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  placeholder="Nhập tên người dùng"
                />
              </div>

              <div>
                <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
                <input
                  id="email"
                  v-model="form.email"
                  type="email"
                  class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  placeholder="Nhập email"
                />
              </div>
            </div>

            <!-- Telegram Integration Section -->
            <div :class="authStore.user?.telegramUserId ? 'bg-green-50 border border-green-200' : 'bg-blue-50 border border-blue-200'" class="p-4 rounded-lg">
              <div class="flex items-center justify-between mb-3">
                <h3 class="text-sm font-medium" :class="authStore.user?.telegramUserId ? 'text-green-800' : 'text-blue-800'">
                  🤖 Liên kết Telegram Bot
                </h3>
                <div v-if="authStore.user?.telegramUserId" class="flex items-center text-green-600">
                  <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
                  </svg>
                  <span class="text-xs font-medium">Đã liên kết</span>
                </div>
              </div>
              
              <div v-if="authStore.user?.telegramUserId" class="text-sm text-green-700 mb-3">
                ✅ Tài khoản Telegram đã được liên kết thành công!<br>
                <span class="font-mono text-xs">ID: {{ authStore.user.telegramUserId }}</span>
              </div>
              <div v-else class="text-sm text-blue-700 mb-3">
                Liên kết tài khoản Telegram để nhận thông báo và chat với bot hỗ trợ.
              </div>
              
              <div class="flex items-center justify-between">
                <div class="text-xs" :class="authStore.user?.telegramUserId ? 'text-green-600' : 'text-blue-600'">
                  <span v-if="!authStore.user?.telegramUserId">
                    Gửi lệnh: <code class="bg-blue-100 px-2 py-1 rounded">/link_{{ authStore.walletAddress }}</code>
                  </span>
                  <span v-else>
                    Có thể chat trực tiếp với bot
                  </span>
                </div>
                <button
                  type="button"
                  @click="openTelegramBot"
                  :class="authStore.user?.telegramUserId ? 'bg-green-600 hover:bg-green-700' : 'bg-blue-600 hover:bg-blue-700'"
                  class="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                >
                  <svg class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 0C5.374 0 0 5.373 0 12s5.374 12 12 12 12-5.373 12-12S18.626 0 12 0zm5.568 8.16c-.169 1.858-.896 6.728-.896 6.728-.896 6.728-1.268 7.893-2.965 7.893-.897 0-1.596-.83-1.596-1.854 0-.896.598-1.567 1.326-2.295.728-.728 1.567-1.326 2.295-1.326.83 0 1.854.699 1.854 1.596 0 1.697-1.165 2.069-7.893 2.965 0 0-4.87.727-6.728.896-1.675.152-2.965-.598-2.965-2.965 0-1.858 1.29-3.117 2.965-2.965z"/>
                  </svg>
                  {{ authStore.user?.telegramUserId ? 'Mở Chat' : 'Liên kết Bot' }}
                </button>
              </div>
            </div>

            <div class="col-span-2">
              <label for="bio" class="block text-sm font-medium text-gray-700">Giới thiệu</label>
              <textarea
                id="bio"
                v-model="form.bio"
                rows="4"
                class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="Viết gì đó về bản thân..."
              ></textarea>
            </div>

            <div class="flex justify-end space-x-4">
              <button
                type="button"
                @click="resetForm"
                class="bg-gray-300 text-gray-700 px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-400 transition-colors"
              >
                Đặt lại
              </button>
              <button
                type="submit"
                :disabled="authStore.isLoading"
                class="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {{ authStore.isLoading ? 'Đang cập nhật...' : 'Cập nhật' }}
              </button>
            </div>
          </form>
        </div>

        <!-- User Stats -->
        <div class="bg-white shadow rounded-lg p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Thống kê tài khoản</h2>
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div class="text-center p-4 bg-indigo-50 rounded-lg">
              <div class="text-2xl font-bold text-indigo-600">{{ authStore.user?.id || 'N/A' }}</div>
              <div class="text-sm text-gray-600">User ID</div>
            </div>
            <div class="text-center p-4 bg-green-50 rounded-lg">
              <div class="text-2xl font-bold text-green-600">
                {{ authStore.user?.lastLoginAt ? formatDate(authStore.user.lastLoginAt) : 'N/A' }}
              </div>
              <div class="text-sm text-gray-600">Đăng nhập cuối</div>
            </div>
            <div class="text-center p-4 bg-blue-50 rounded-lg">
              <div class="text-2xl font-bold text-blue-600">
                {{ authStore.user?.telegramUserId ? '✅' : '❌' }}
              </div>
              <div class="text-sm text-gray-600">Telegram</div>
            </div>
            <div class="text-center p-4 bg-purple-50 rounded-lg">
              <div class="text-2xl font-bold" :class="authStore.user?.isActive ? 'text-green-600' : 'text-red-600'">
                {{ authStore.user?.isActive ? 'Hoạt động' : 'Tạm dừng' }}
              </div>
              <div class="text-sm text-gray-600">Trạng thái</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Chat Widget -->
    <ChatWidget />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import ChatWidget from '../components/ChatWidget.vue'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  username: '',
  email: '',
  bio: ''
})

const isLoadingBalance = ref(false)

// Methods

const copyAddress = async () => {
  try {
    await navigator.clipboard.writeText(authStore.walletAddress)
    alert('Đã sao chép địa chỉ ví!')
  } catch (error) {
    console.error('Lỗi sao chép:', error)
  }
}

const updateProfile = async () => {
  try {
    await authStore.updateProfile(form.value)
    alert('Cập nhật thành công!')
  } catch (error) {
    console.error('Lỗi cập nhật:', error)
    alert('Lỗi cập nhật: ' + error.message)
  }
}

const resetForm = () => {
  if (authStore.user) {
    form.value = {
      username: authStore.user.username || '',
      email: authStore.user.email || '',
      bio: authStore.user.bio || ''
    }
  }
}

const logout = () => {
  authStore.logout()
  router.push('/')
}

const openTelegramBot = async () => {
  // Mở Telegram bot
  const botUsername = 'buildweb3_bot'
  const telegramUrl = `https://t.me/${botUsername}`
  window.open(telegramUrl, '_blank')
  
  // Hiển thị thông báo
  alert('Đang mở Telegram Bot!\n\nGửi /start để bắt đầu và sau đó gửi:\n/link_' + authStore.walletAddress + '\n\nđể liên kết tài khoản Telegram với ví Web3 của bạn.')
  
  // Refresh user info sau 3 giây để cập nhật trạng thái liên kết
  setTimeout(async () => {
    try {
      await authStore.refreshUser()
    } catch (error) {
      console.error('Lỗi refresh user:', error)
    }
  }, 3000)
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  try {
    return new Date(dateString).toLocaleDateString('vi-VN')
  } catch (error) {
    return 'N/A'
  }
}

// Watch for user changes and update form
watch(() => authStore.user, (newUser) => {
  if (newUser) {
    form.value = {
      username: newUser.username || '',
      email: newUser.email || '',
      bio: newUser.bio || ''
    }
  }
}, { immediate: true })

onMounted(async () => {
  // Kiểm tra nếu chưa đăng nhập thì chuyển về login
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  // Load user profile nếu chưa có
  if (!authStore.user) {
    try {
      await authStore.getUserProfile()
    } catch (error) {
      console.error('Lỗi tải profile:', error)
      router.push('/login')
    }
  }

  // Profile loaded successfully
})
</script>
