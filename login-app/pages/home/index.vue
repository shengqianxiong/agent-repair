<template>
  <view class="home-page">
    <view class="welcome-card">
      <view class="avatar">
        <u-icon name="account-fill" size="48" color="#2563eb" />
      </view>
      <text class="welcome-text">你好，{{ username }}</text>
      <text class="welcome-desc">您已成功登录系统</text>
    </view>

    <view class="info-card">
      <view class="info-row">
        <text class="info-label">当前账号</text>
        <text class="info-value">{{ username }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">会话状态</text>
        <text class="info-value status-active">已登录</text>
      </view>
    </view>

    <u-button
      type="error"
      plain
      text="退出登录"
      custom-style="margin-top: 48rpx; height: 88rpx; border-radius: 12rpx;"
      @click="handleLogout"
    />
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getUserInfo, isLoggedIn, removeToken } from '@/utils/auth.js'

const username = computed(() => getUserInfo().username || '用户')

/** 未登录时跳转登录页 */
onShow(() => {
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/index' })
  }
})

/** 退出登录并返回登录页 */
function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        removeToken()
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
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #ffffff;
  border-radius: 24rpx;
  padding: 64rpx 32rpx 48rpx;
  margin-bottom: 32rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.04);
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
}

.welcome-text {
  font-size: 40rpx;
  font-weight: 600;
  color: #111827;
  margin-bottom: 12rpx;
}

.welcome-desc {
  font-size: 28rpx;
  color: #6b7280;
}

.info-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 8rpx 32rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.04);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f3f4f6;

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  font-size: 28rpx;
  color: #6b7280;
}

.info-value {
  font-size: 28rpx;
  color: #111827;
}

.status-active {
  color: #10b981;
}
</style>
