<template>
  <div v-if="showDebug" class="fixed top-4 right-4 bg-black bg-opacity-90 text-white p-4 rounded-lg text-xs max-w-md z-50">
    <div class="flex justify-between items-center mb-2">
      <h3 class="font-bold">Debug Info</h3>
      <button @click="showDebug = false" class="text-red-400 hover:text-red-300">×</button>
    </div>
    
    <div class="space-y-2">
      <div>
        <strong>Auth Status:</strong> {{ authStore.isAuthenticated ? 'Đã đăng nhập' : 'Chưa đăng nhập' }}
      </div>
      
      <div v-if="authStore.user">
        <strong>User Data:</strong>
        <pre class="bg-gray-800 p-2 rounded mt-1 overflow-auto max-h-32">{{ JSON.stringify(authStore.user, null, 2) }}</pre>
      </div>
      
      <div>
        <strong>Wallet Address:</strong> {{ authStore.walletAddress || 'Chưa có' }}
      </div>
      
      <div v-if="authStore.user?.telegramUserId">
        <strong>Telegram ID:</strong> {{ authStore.user.telegramUserId }}
      </div>
    </div>
  </div>
  
  <button 
    v-if="!showDebug"
    @click="showDebug = true"
    class="fixed top-4 right-4 bg-blue-600 text-white p-2 rounded-full text-xs z-50"
  >
    Debug
  </button>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const showDebug = ref(false)
</script>
