<template>
  <view class="home-page">
    <view class="welcome-card">
      <view class="avatar-wrap">
        <u-icon name="account-fill" color="#2979ff" size="40"></u-icon>
      </view>
      <view class="welcome-info">
        <text class="greeting">欢迎回来</text>
        <text class="username">{{ accountInfo.username || userInfo.username || '-' }}</text>
      </view>
    </view>

    <view class="info-card">
      <view class="info-item">
        <text class="label">账号状态</text>
        <u-tag
          :text="accountInfo.statusText || '正常'"
          :type="accountInfo.status === 0 ? 'error' : 'success'"
          size="mini"
        ></u-tag>
      </view>
      <view class="info-item">
        <text class="label">创建时间</text>
        <text class="value">{{ formatTime(accountInfo.createTime) }}</text>
      </view>
      <view class="info-item">
        <text class="label">会话有效期</text>
        <text class="value">2 小时</text>
      </view>
    </view>

    <view class="tip-card">
      <u-icon name="info-circle" color="#909399" size="16"></u-icon>
      <text class="tip-text">登录成功，您已进入系统首页。Token 过期后需重新登录。</text>
    </view>

    <u-button
      type="error"
      text="退出登录"
      plain
      customStyle="margin-top: 48rpx;"
      @click="handleLogout"
    ></u-button>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getAccountInfo } from '@/api/modules/account.js'
import { isLoggedIn, getUserInfo, clearAuth } from '@/utils/auth.js'

const userInfo = ref({})
const accountInfo = ref({})
const loading = ref(false)

onShow(() => {
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }
  userInfo.value = getUserInfo() || {}
  loadAccountInfo()
})

/**
 * 拉取当前登录账号详情。
 */
async function loadAccountInfo() {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    accountInfo.value = await getAccountInfo() || {}
  } catch {
    // 401 时 request 层会自动跳转登录页
  } finally {
    loading.value = false
  }
}

function formatTime(time) {
  if (!time) {
    return '-'
  }
  const date = new Date(time)
  if (Number.isNaN(date.getTime())) {
    return time
  }
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

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
  padding: 32rpx;
}

.welcome-card {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #2979ff 0%, #5cadff 100%);
  border-radius: 24rpx;
  padding: 40rpx 32rpx;
  margin-bottom: 32rpx;
  color: #ffffff;
}

.avatar-wrap {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.welcome-info {
  display: flex;
  flex-direction: column;
}

.greeting {
  font-size: 28rpx;
  opacity: 0.9;
  margin-bottom: 8rpx;
}

.username {
  font-size: 40rpx;
  font-weight: 600;
}

.info-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 16rpx 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.label {
  font-size: 28rpx;
  color: #606266;
}

.value {
  font-size: 28rpx;
  color: #303133;
}

.tip-card {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  background: #f0f5ff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.tip-text {
  flex: 1;
  font-size: 24rpx;
  color: #606266;
  line-height: 1.6;
}
</style>
