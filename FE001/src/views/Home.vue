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
            <router-link
              to="/login"
              class="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700 transition-colors"
            >
              Đăng nhập
            </router-link>
          </div>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-6">
          Chào mừng đến với Web3 Authentication
        </h1>
        <p class="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
          Đăng nhập an toàn bằng ví Ethereum của bạn. Sử dụng MetaMask để xác thực danh tính và quản lý hồ sơ cá nhân.
        </p>
        
        <div class="flex justify-center space-x-4">
          <router-link
            to="/login"
            class="bg-indigo-600 text-white px-8 py-3 rounded-lg text-lg font-medium hover:bg-indigo-700 transition-colors"
          >
            Bắt đầu ngay
          </router-link>
        </div>
      </div>

      <!-- Features -->
      <div class="mt-16 grid grid-cols-1 md:grid-cols-3 gap-8">
        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-indigo-100 rounded-lg flex items-center justify-center mb-4">
            <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Bảo mật cao</h3>
          <p class="text-gray-600">Xác thực bằng chữ ký số từ ví Ethereum, đảm bảo an toàn tuyệt đối.</p>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mb-4">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Nhanh chóng</h3>
          <p class="text-gray-600">Đăng nhập chỉ với một cú nhấp chuột, không cần nhớ mật khẩu.</p>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-md">
          <div class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mb-4">
            <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Quản lý hồ sơ</h3>
          <p class="text-gray-600">Cập nhật và quản lý thông tin cá nhân một cách dễ dàng.</p>
        </div>
      </div>

      <!-- How it works -->
      <div class="mt-16">
        <h2 class="text-3xl font-bold text-gray-900 text-center mb-8">Cách thức hoạt động</h2>
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div class="text-center">
            <div class="w-16 h-16 bg-indigo-600 text-white rounded-full flex items-center justify-center mx-auto mb-4 text-xl font-bold">
              1
            </div>
            <h3 class="font-semibold text-gray-900 mb-2">Kết nối MetaMask</h3>
            <p class="text-gray-600">Kết nối ví Ethereum của bạn</p>
          </div>
          <div class="text-center">
            <div class="w-16 h-16 bg-indigo-600 text-white rounded-full flex items-center justify-center mx-auto mb-4 text-xl font-bold">
              2
            </div>
            <h3 class="font-semibold text-gray-900 mb-2">Ký thông điệp</h3>
            <p class="text-gray-600">Xác nhận danh tính bằng chữ ký số</p>
          </div>
          <div class="text-center">
            <div class="w-16 h-16 bg-indigo-600 text-white rounded-full flex items-center justify-center mx-auto mb-4 text-xl font-bold">
              3
            </div>
            <h3 class="font-semibold text-gray-900 mb-2">Xác thực</h3>
            <p class="text-gray-600">Hệ thống xác minh chữ ký</p>
          </div>
          <div class="text-center">
            <div class="w-16 h-16 bg-indigo-600 text-white rounded-full flex items-center justify-center mx-auto mb-4 text-xl font-bold">
              4
            </div>
            <h3 class="font-semibold text-gray-900 mb-2">Truy cập</h3>
            <p class="text-gray-600">Đăng nhập thành công</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Component logic here if needed
</script>
