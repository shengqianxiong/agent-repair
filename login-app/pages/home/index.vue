<template>
  <view class="home-page">
    <view class="welcome-card">
      <view class="avatar">
        <u-icon name="account-fill" color="#3b82f6" size="48" />
      </view>
      <text class="welcome-text">欢迎回来</text>
      <text class="username">{{ username }}</text>
      <text class="tip-text">您已成功登录系统</text>
    </view>

    <view class="action-card">
      <u-button
        type="error"
        text="退出登录"
        plain
        custom-style="height: 88rpx; border-radius: 12rpx;"
        @click="handleLogout"
      />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { clearAuth, getUsername, isLoggedIn } from '@/utils/auth.js'

const username = ref('')

onShow(() => {
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }
  username.value = getUsername()
})

/**
 * 退出登录并返回登录页
 */
function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        clearAuth()
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  padding: 48rpx 32rpx;
  box-sizing: border-box;
}

.welcome-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 64rpx 32rpx;
  text-align: center;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.06);
  margin-bottom: 32rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 32rpx;
}

.welcome-text {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #111827;
  margin-bottom: 12rpx;
}

.username {
  display: block;
  font-size: 32rpx;
  color: #3b82f6;
  margin-bottom: 16rpx;
}

.tip-text {
  display: block;
  font-size: 26rpx;
  color: #9ca3af;
}

.action-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.06);
}
</style>
