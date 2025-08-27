<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Đăng nhập với MetaMask
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          Kết nối ví Ethereum của bạn để đăng nhập an toàn
        </p>
      </div>

      <div class="bg-white py-8 px-6 shadow-xl rounded-lg">
        <!-- MetaMask Status -->
        <div v-if="!isMetaMaskInstalled" class="mb-6">
          <div class="bg-red-50 border border-red-200 rounded-md p-4">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">MetaMask chưa được cài đặt</h3>
                <div class="mt-2 text-sm text-red-700">
                  <p>Vui lòng cài đặt MetaMask extension để sử dụng tính năng này.</p>
                </div>
                <div class="mt-4">
                  <a
                    href="https://metamask.io/download/"
                    target="_blank"
                    class="text-sm font-medium text-red-800 hover:text-red-900 underline"
                  >
                    Tải MetaMask →
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Wallet Connection Status -->
        <div v-if="authStore.walletAddress" class="mb-6">
          <div class="bg-green-50 border border-green-200 rounded-md p-4">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-green-800">Ví đã được kết nối</h3>
                <p class="text-sm text-green-700 font-mono">{{ authStore.shortAddress }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="authStore.error" class="mb-6">
          <div class="bg-red-50 border border-red-200 rounded-md p-4">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
                </svg>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">Lỗi</h3>
                <div class="mt-2 text-sm text-red-700">
                  <p>{{ authStore.error }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="space-y-4">
          <!-- Connect Wallet Button -->
          <button
            v-if="!authStore.walletAddress && isMetaMaskInstalled"
            @click="connectWallet"
            :disabled="authStore.isLoading"
            class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="authStore.isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <svg v-else class="w-5 h-5 mr-2" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
            {{ authStore.isLoading ? 'Đang kết nối...' : 'Kết nối MetaMask' }}
          </button>

          <!-- Login Button -->
          <button
            v-if="authStore.walletAddress"
            @click="login"
            :disabled="authStore.isLoading"
            class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="authStore.isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <svg v-else class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"></path>
            </svg>
            {{ authStore.isLoading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
          </button>

          <!-- Back to Home -->
          <div class="text-center">
            <router-link
              to="/"
              class="text-sm text-indigo-600 hover:text-indigo-500"
            >
              ← Quay về trang chủ
            </router-link>
          </div>
        </div>

        <!-- Instructions -->
        <div class="mt-8 border-t border-gray-200 pt-6">
          <h3 class="text-sm font-medium text-gray-900 mb-3">Hướng dẫn:</h3>
          <ol class="text-sm text-gray-600 space-y-2">
            <li class="flex items-start">
              <span class="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-600 rounded-full flex items-center justify-center text-xs font-medium mr-2">1</span>
              <span>Nhấn "Kết nối MetaMask" để kết nối ví</span>
            </li>
            <li class="flex items-start">
              <span class="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-600 rounded-full flex items-center justify-center text-xs font-medium mr-2">2</span>
              <span>Chọn tài khoản trong MetaMask popup</span>
            </li>
            <li class="flex items-start">
              <span class="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-600 rounded-full flex items-center justify-center text-xs font-medium mr-2">3</span>
              <span>Ký thông điệp xác thực khi được yêu cầu</span>
            </li>
            <li class="flex items-start">
              <span class="flex-shrink-0 w-6 h-6 bg-indigo-100 text-indigo-600 rounded-full flex items-center justify-center text-xs font-medium mr-2">4</span>
              <span>Hoàn tất đăng nhập và chuyển đến trang Profile</span>
            </li>
          </ol>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isMetaMaskInstalled = computed(() => {
  return typeof window !== 'undefined' && window.ethereum
})

const connectWallet = async () => {
  try {
    await authStore.connectWallet()
  } catch (error) {
    console.error('Lỗi kết nối ví:', error)
  }
}

const login = async () => {
  try {
    await authStore.login()
    router.push('/profile')
  } catch (error) {
    console.error('Lỗi đăng nhập:', error)
  }
}

onMounted(() => {
  // Kiểm tra nếu đã đăng nhập thì chuyển đến profile
  if (authStore.isAuthenticated) {
    router.push('/profile')
  }
})
</script>
