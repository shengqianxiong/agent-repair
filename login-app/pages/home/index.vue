<template>
  <view class="home-page">
    <view class="welcome-card">
      <text class="welcome-title">欢迎回来</text>
      <text class="welcome-desc">您已成功登录系统</text>
    </view>

    <view class="info-card" v-if="account">
      <view class="info-row">
        <text class="info-label">账号</text>
        <text class="info-value">{{ account.username }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">账号状态</text>
        <u-tag
          :text="statusText"
          :type="account.status === 1 ? 'success' : 'error'"
          size="mini"
        />
      </view>
      <view class="info-row">
        <text class="info-label">创建时间</text>
        <text class="info-value">{{ account.createTime || '-' }}</text>
      </view>
    </view>

    <view class="info-card loading-card" v-else-if="loading">
      <u-loading-icon text="加载中" />
    </view>

    <view class="info-card empty-card" v-else>
      <text class="empty-text">暂无账号信息</text>
    </view>

    <u-button
      type="error"
      text="退出登录"
      plain
      custom-style="margin-top: 48rpx;"
      @click="handleLogout"
    />
  </view>
</template>

<script setup>
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import { fetchAccountDetail } from '@/api/modules/account.js'
import { clearAuth, getUserInfo, isLoggedIn } from '@/utils/auth.js'

const account = ref(getUserInfo())
const loading = ref(false)

const statusText = computed(() => {
  if (!account.value) {
    return '-'
  }
  return account.value.status === 1 ? '正常' : '禁用'
})

onShow(() => {
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }
  loadAccountDetail()
})

onPullDownRefresh(async () => {
  await loadAccountDetail()
  uni.stopPullDownRefresh()
})

/** 拉取当前登录用户详情并刷新展示 */
async function loadAccountDetail() {
  loading.value = true
  try {
    const detail = await fetchAccountDetail()
    account.value = detail
  } catch (error) {
    if (error?.code !== 401) {
      uni.showToast({ title: error?.msg || '获取账号信息失败', icon: 'none' })
    }
  } finally {
    loading.value = false
  }
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (!res.confirm) {
        return
      }
      clearAuth()
      uni.reLaunch({ url: '/pages/login/index' })
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
  padding: 48rpx 40rpx;
  margin-bottom: 32rpx;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 24rpx;
  color: #ffffff;
}

.welcome-title {
  display: block;
  font-size: 40rpx;
  font-weight: 600;
}

.welcome-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  opacity: 0.9;
}

.info-card {
  padding: 16rpx 32rpx;
  background: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.04);
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #ebeef5;

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  font-size: 28rpx;
  color: #909399;
}

.info-value {
  font-size: 28rpx;
  color: #303133;
}

.loading-card,
.empty-card {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #909399;
}
</style>
