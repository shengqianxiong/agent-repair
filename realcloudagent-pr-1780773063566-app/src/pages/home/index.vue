<template>
  <view class="home-page">
    <view class="welcome-card">
      <text class="welcome-label">当前登录用户</text>
      <text class="welcome-account">{{ username }}</text>
    </view>

    <view class="info-card">
      <text class="info-text">欢迎使用极简登录系统</text>
      <text class="info-desc">您已成功登录，Token 有效期为 2 小时</text>
    </view>

    <u-button type="error" text="退出登录" @click="handleLogout" />
  </view>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getUserInfo } from '@/api/index.js'
import { getAccount, removeToken, setAccount } from '@/utils/auth.js'

const username = ref(getAccount())

onMounted(async () => {
  try {
    const data = await getUserInfo()
    username.value = data.username
    setAccount(data.username)
  } catch (e) {
    // 接口不可用时使用本地缓存
  }
})

function handleLogout() {
  removeToken()
  uni.reLaunch({ url: '/pages/login/index' })
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding: 48rpx 32rpx;
}
.welcome-card {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 24rpx;
  padding: 48rpx 32rpx;
  margin-bottom: 32rpx;
}
.welcome-label {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
}
.welcome-account {
  display: block;
  margin-top: 16rpx;
  font-size: 44rpx;
  font-weight: 600;
  color: #fff;
}
.info-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 48rpx;
}
.info-text {
  display: block;
  font-size: 30rpx;
  color: #303133;
  font-weight: 500;
}
.info-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: #909399;
}
</style>
