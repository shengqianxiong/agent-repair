<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <view class="navbar">
      <view class="nav-left"><u-icon name="list" color="#7B61FF" size="22"></u-icon></view>
      <text class="nav-title">云享生活</text>
      <view class="nav-right"><u-icon name="bell" color="#7B61FF" size="20"></u-icon></view>
    </view>

    <scroll-view scroll-y class="content">
      <view class="user-section">
        <view class="avatar-wrap">
          <image class="avatar" :src="user.avatar" mode="aspectFill" />
          <view class="level-tag">LV.{{ user.memberLevel || 5 }}</view>
        </view>
        <view class="user-info">
          <text class="nickname">{{ user.nickname || '高级会员' }}</text>
          <text class="user-id">ID: {{ user.id || '10086' }}</text>
        </view>
        <view class="settings-btn" @click="goEdit"><u-icon name="setting" color="#999" size="22"></u-icon></view>
      </view>

      <view class="vip-banner">
        <view class="vip-text">
          <text class="vip-title">尊享会员特权</text>
          <text class="vip-desc">开通享专属折扣与积分加倍</text>
        </view>
        <view class="vip-btn" @click="showToast('开通会员')">立即开通</view>
      </view>

      <view class="stat-cards">
        <view class="stat-item" @click="goPage('/pages/wallet/index')">
          <u-icon name="rmb-circle" color="#7B61FF" size="28"></u-icon>
          <text>账户余额</text>
        </view>
        <view class="stat-item" @click="goPage('/pages/wine/stored')">
          <u-icon name="gift" color="#7B61FF" size="28"></u-icon>
          <text>快速取酒</text>
        </view>
        <view class="stat-item" @click="goPage('/pages/order/list')">
          <u-icon name="order" color="#7B61FF" size="28"></u-icon>
          <text>订单中心</text>
        </view>
      </view>

      <view class="points-card">
        <view class="points-header">
          <text class="points-label">我的积分</text>
          <text class="points-value">{{ user.points || 12450 }}</text>
        </view>
        <view class="progress-bar"><view class="progress-fill" style="width:65%"></view></view>
        <view class="points-footer">
          <text class="upgrade-tip">再积 2550 分升级 LV.6</text>
          <text class="earn-link" @click="goPage('/pages/points/index')">去赚积分 ></text>
        </view>
      </view>

      <view class="menu-card">
        <view v-for="item in menuList" :key="item.name" class="menu-item" @click="goPage(item.path)">
          <view class="menu-icon" :style="{ background: item.bg }">
            <u-icon :name="item.icon" :color="item.color" size="20"></u-icon>
          </view>
          <text class="menu-name">{{ item.name }}</text>
          <u-icon name="arrow-right" color="#CCC" size="14"></u-icon>
        </view>
      </view>

      <view class="footer-info">
        <text>云享生活 jiuba</text>
        <text>Version 3.4.0</text>
      </view>
    </scroll-view>
    <AppTabbar current="my" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getUserInfo } from '@/api/index.js'
import { showToast } from '@/utils/common.js'

const loading = ref(false)
const user = ref({})

const menuList = [
  { name: '邀请好友', icon: 'share', path: '/pages/distribution/index', bg: '#F0EDFF', color: '#7B61FF' },
  { name: '我的积分', icon: 'integral', path: '/pages/points/index', bg: '#FFEDED', color: '#FF69B4' },
  { name: '分销中心', icon: 'red-packet', path: '/pages/distribution/index', bg: '#F0EDFF', color: '#7B61FF' },
  { name: '我的存酒', icon: 'gift', path: '/pages/wine/stored', bg: '#FFEDED', color: '#FF69B4' },
  { name: '在线客服', icon: 'server-man', path: '', bg: '#F0EDFF', color: '#7B61FF' },
  { name: '帮助中心', icon: 'question-circle', path: '', bg: '#FFEDED', color: '#FF69B4' },
  { name: '隐私与安全', icon: 'lock', path: '', bg: '#F0EDFF', color: '#7B61FF' }
]

async function loadUser() {
  loading.value = true
  try { user.value = await getUserInfo() || {} } finally { loading.value = false }
}

function goPage(path) {
  if (!path) { showToast('功能开发中'); return }
  uni.navigateTo({ url: path })
}
function goEdit() { uni.navigateTo({ url: '/pages/profile/edit' }) }

onMounted(loadUser)
onShow(loadUser)
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #F8F9FB; }
.navbar {
  display: flex; align-items: center; height: 88rpx; padding: 0 32rpx;
  .nav-title { flex: 1; text-align: center; font-size: 36rpx; font-weight: 700; color: #7B61FF; }
  .nav-left, .nav-right { width: 60rpx; }
}
.content { height: calc(100vh - 188rpx); padding: 0 32rpx; }
.user-section {
  display: flex; align-items: center; padding: 40rpx 0; gap: 24rpx;
  .avatar-wrap { position: relative; }
  .avatar { width: 120rpx; height: 120rpx; border-radius: 50%; }
  .level-tag { position: absolute; bottom: 0; right: -8rpx; background: #7B61FF; color: #FFF; font-size: 18rpx; padding: 2rpx 10rpx; border-radius: 200rpx; }
  .user-info { flex: 1; }
  .nickname { font-size: 36rpx; font-weight: 700; color: #1A1A1A; display: block; }
  .user-id { font-size: 24rpx; color: #999; margin-top: 8rpx; display: block; }
}
.vip-banner {
  display: flex; align-items: center; justify-content: space-between;
  background: linear-gradient(135deg, #7246F2, #9D7BFF); border-radius: 32rpx; padding: 32rpx; margin-bottom: 24rpx;
  .vip-title { font-size: 30rpx; font-weight: 700; color: #FFF; display: block; }
  .vip-desc { font-size: 24rpx; color: rgba(255,255,255,0.8); margin-top: 8rpx; display: block; }
  .vip-btn { background: #FFF; color: #7B61FF; padding: 12rpx 28rpx; border-radius: 200rpx; font-size: 26rpx; font-weight: 600; }
}
.stat-cards {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 24rpx; margin-bottom: 24rpx;
  .stat-item { background: #FFF; border-radius: 32rpx; padding: 28rpx 16rpx; display: flex; flex-direction: column; align-items: center; gap: 12rpx; font-size: 24rpx; color: #333; box-shadow: var(--shadow-card); }
}
.points-card {
  background: #FFF; border-radius: 32rpx; padding: 32rpx; margin-bottom: 24rpx; box-shadow: var(--shadow-card);
  .points-header { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 16rpx; }
  .points-label { font-size: 28rpx; color: #666; }
  .points-value { font-size: 40rpx; font-weight: 700; color: #7B61FF; }
  .progress-bar { height: 12rpx; background: #F0F0F0; border-radius: 200rpx; overflow: hidden; }
  .progress-fill { height: 100%; background: #7B61FF; border-radius: 200rpx; }
  .points-footer { display: flex; justify-content: space-between; margin-top: 16rpx; font-size: 24rpx; }
  .upgrade-tip { color: #999; }
  .earn-link { color: #7B61FF; }
}
.menu-card {
  background: #FFF; border-radius: 32rpx; overflow: hidden; margin-bottom: 32rpx; box-shadow: var(--shadow-card);
  .menu-item { display: flex; align-items: center; padding: 28rpx 32rpx; border-bottom: 1rpx solid #F0F0F0;
    &:last-child { border-bottom: none; }
    .menu-icon { width: 64rpx; height: 64rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 20rpx; }
    .menu-name { flex: 1; font-size: 28rpx; color: #333; }
  }
}
.footer-info { text-align: center; padding: 32rpx 0 48rpx; font-size: 24rpx; color: #CCC; display: flex; flex-direction: column; gap: 8rpx; }
</style>
