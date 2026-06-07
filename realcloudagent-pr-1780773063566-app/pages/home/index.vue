<template>
  <view class="home-page">
    <view class="home-header">
      <text class="home-title">欢迎回来</text>
      <text class="home-subtitle">您已成功登录用户端</text>
    </view>

    <view class="user-card">
      <view class="user-row">
        <text class="user-label">用户名</text>
        <text class="user-value">{{ displayName }}</text>
      </view>
      <view class="user-row">
        <text class="user-label">用户 ID</text>
        <text class="user-value">{{ userIdText }}</text>
      </view>
    </view>

    <u-button
      class="logout-button"
      type="error"
      plain
      text="退出登录"
      :loading="loggingOut"
      @click="handleLogout"
    />
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { clearAuth, getUserInfo, isLoggedIn } from '@/utils/auth.js'

const loggingOut = ref(false)
const localUser = ref(getUserInfo())

const displayName = computed(() => {
  return localUser.value?.nickname || localUser.value?.username || '-'
})

const userIdText = computed(() => {
  const userId = localUser.value?.userId
  return userId !== undefined && userId !== null ? String(userId) : '-'
})

onShow(() => {
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }

  // 展示登录接口返回并缓存在本地的用户信息
  localUser.value = getUserInfo()
})

function handleLogout() {
  if (loggingOut.value) {
    return
  }

  loggingOut.value = true
  clearAuth()
  uni.showToast({ title: '已退出登录', icon: 'none' })
  setTimeout(() => {
    loggingOut.value = false
    uni.reLaunch({ url: '/pages/login/index' })
  }, 300)
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  padding: 120rpx 48rpx 48rpx;
  background: #f5f7fa;
  box-sizing: border-box;
}

.home-header {
  margin-bottom: 48rpx;
}

.home-title {
  display: block;
  font-size: 48rpx;
  font-weight: 600;
  color: #303133;
}

.home-subtitle {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  color: #909399;
}

.user-card {
  padding: 40rpx;
  background: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.04);
}

.user-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #ebeef5;

  &:last-child {
    border-bottom: none;
  }
}

.user-label {
  font-size: 28rpx;
  color: #909399;
}

.user-value {
  font-size: 30rpx;
  color: #303133;
  font-weight: 500;
}

.logout-button {
  margin-top: 64rpx;
}
</style>
