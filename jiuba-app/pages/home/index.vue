<template>
  <view class="page">
    <LoadingOverlay :show="loading" />
    <view class="navbar">
      <view class="nav-left" @click="openMenu"><u-icon name="list" color="#7B61FF" size="22"></u-icon></view>
      <text class="nav-title">云享生活</text>
      <view class="nav-right" @click="goNotify">
        <u-icon name="bell" color="#7B61FF" size="22"></u-icon>
        <view v-if="unreadCount" class="red-dot"></view>
      </view>
    </view>

    <scroll-view scroll-y class="content">
      <view class="hero-card">
        <text class="hero-welcome">WELCOME BACK</text>
        <text class="hero-sub">开启今日云享之旅</text>
        <view class="hero-badge">LV.5 高级会员</view>
        <view class="hero-status">
          <text>{{ userInfo.bookingStatus || '在线预约中' }}</text>
          <text class="hero-divider">|</text>
          <text>{{ userInfo.points || 1240 }}积分</text>
        </view>
      </view>

      <view class="action-row">
        <view class="action-card primary" @click="goTab('/pages/menu/index')">
          <u-icon name="bag" color="#FFFFFF" size="32"></u-icon>
          <text>自助点餐</text>
        </view>
        <view class="action-card secondary" @click="showToast('预约功能开发中')">
          <u-icon name="calendar" color="#7B61FF" size="32"></u-icon>
          <text>在线预约</text>
        </view>
      </view>

      <view class="service-grid">
        <view v-for="item in services" :key="item.name" class="service-item" @click="goPage(item.path)">
          <view class="service-icon" :style="{ background: item.bg }">
            <u-icon :name="item.icon" color="#7B61FF" size="24"></u-icon>
          </view>
          <text>{{ item.name }}</text>
        </view>
      </view>

      <view class="section-header">
        <text class="section-title">精选活动</text>
        <text class="section-more" @click="showToast('查看全部')">查看全部</text>
      </view>
      <scroll-view scroll-x class="activity-scroll">
        <view v-for="act in activities" :key="act.id" class="activity-card" @click="goActivity(act.id)">
          <image class="activity-img" :src="act.image" mode="aspectFill" />
          <view v-if="act.tag" class="activity-tag">{{ act.tag }}</view>
          <view class="activity-info">
            <text class="activity-name">{{ act.title }}</text>
            <view class="activity-price">
              <text class="price-now">¥{{ act.price }}</text>
              <text class="price-old">¥{{ act.originalPrice }}</text>
            </view>
            <view class="buy-btn">立即抢购</view>
          </view>
        </view>
      </scroll-view>
    </scroll-view>

    <view class="fab" @click="goTab('/pages/menu/index')">
      <u-icon name="plus" color="#FFFFFF" size="24"></u-icon>
    </view>
    <AppTabbar current="home" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { getHomeIndex } from '@/api/index.js'
import { showToast } from '@/utils/common.js'

const loading = ref(false)
const unreadCount = ref(0)
const userInfo = ref({})
const activities = ref([])

const services = [
  { name: '酒水套餐', icon: 'gift', path: '/pages/menu/index', bg: '#F0EDFF' },
  { name: '附近搭子', icon: 'account-fill', path: '/pages/square/index', bg: '#FFEDED' },
  { name: '会员中心', icon: 'star', path: '/pages/my/index', bg: '#F0EDFF' },
  { name: '我的积分', icon: 'integral', path: '/pages/points/index', bg: '#FFEDED' },
  { name: '团购', icon: 'coupon', path: '/pages/menu/index', bg: '#F0EDFF' },
  { name: '互动游戏', icon: 'play-circle', path: '/pages/table/interaction', bg: '#FFEDED' }
]

async function loadData() {
  loading.value = true
  try {
    const home = await getHomeIndex()
    userInfo.value = home?.user || {}
    activities.value = home?.activities || []
    unreadCount.value = home?.unreadCount ?? 0
  } finally {
    loading.value = false
  }
}

function goTab(url) { uni.switchTab({ url }) }
function goPage(url) {
  if (url.includes('square') || url.includes('menu') || url.includes('my') || url.includes('home')) {
    uni.switchTab({ url })
  } else {
    uni.navigateTo({ url })
  }
}
function goActivity(id) { showToast(`活动 ${id}`) }
function openMenu() { showToast('菜单') }
function goNotify() { showToast('通知') }

onMounted(loadData)
onShow(loadData)
onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh() })
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: var(--color-background); }
.navbar {
  display: flex; align-items: center; height: 88rpx; padding: 0 32rpx; background: #FFFFFF;
  .nav-title { flex: 1; text-align: center; font-size: 36rpx; font-weight: 700; color: #7B61FF; }
  .nav-left, .nav-right { width: 60rpx; position: relative; }
  .red-dot { position: absolute; top: 0; right: 0; width: 12rpx; height: 12rpx; border-radius: 50%; background: #FF4D4F; }
}
.content { height: calc(100vh - 188rpx); padding: 24rpx 32rpx; }
.hero-card {
  background: linear-gradient(135deg, #7246F2 0%, #9D7BFF 100%);
  border-radius: 48rpx; padding: 40rpx; color: #FFFFFF; margin-bottom: 24rpx;
  .hero-welcome { font-size: 24rpx; opacity: 0.9; display: block; }
  .hero-sub { font-size: 40rpx; font-weight: 700; display: block; margin: 12rpx 0 20rpx; }
  .hero-badge { display: inline-block; background: rgba(255,255,255,0.2); padding: 8rpx 20rpx; border-radius: 200rpx; font-size: 24rpx; }
  .hero-status { margin-top: 20rpx; font-size: 26rpx; opacity: 0.9; }
  .hero-divider { margin: 0 16rpx; }
}
.action-row { display: flex; gap: 24rpx; margin-bottom: 24rpx; }
.action-card {
  flex: 1; height: 240rpx; border-radius: 48rpx; display: flex; flex-direction: column;
  align-items: center; justify-content: center; gap: 16rpx; font-size: 30rpx; font-weight: 600;
  &.primary { background: #7B61FF; color: #FFFFFF; box-shadow: var(--shadow-button); }
  &.secondary { background: #FFFFFF; color: #7B61FF; box-shadow: var(--shadow-card); }
}
.service-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 24rpx; background: #F8F9FB;
  border-radius: 48rpx; padding: 32rpx; margin-bottom: 32rpx;
  .service-item { display: flex; flex-direction: column; align-items: center; gap: 12rpx; font-size: 24rpx; color: #333; }
  .service-icon { width: 80rpx; height: 80rpx; border-radius: 24rpx; display: flex; align-items: center; justify-content: center; }
}
.section-header { display: flex; justify-content: space-between; margin-bottom: 20rpx; }
.section-title { font-size: 32rpx; font-weight: 700; color: #1A1A1A; }
.section-more { font-size: 26rpx; color: #7B61FF; }
.activity-scroll { white-space: nowrap; padding-bottom: 20rpx; }
.activity-card {
  display: inline-block; width: 480rpx; margin-right: 24rpx; border-radius: 40rpx; overflow: hidden;
  background: #FFFFFF; box-shadow: var(--shadow-card); vertical-align: top; position: relative;
  .activity-img { width: 100%; height: 240rpx; }
  .activity-tag { position: absolute; top: 16rpx; left: 16rpx; background: #FF4D4F; color: #FFF; font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 20rpx; }
  .activity-info { padding: 20rpx; }
  .activity-name { font-size: 28rpx; font-weight: 600; display: block; margin-bottom: 12rpx; }
  .activity-price { display: flex; align-items: baseline; gap: 12rpx; margin-bottom: 16rpx; }
  .price-now { font-size: 36rpx; font-weight: 700; color: #7B61FF; }
  .price-old { font-size: 24rpx; color: #999; text-decoration: line-through; }
  .buy-btn { background: #7B61FF; color: #FFF; text-align: center; padding: 12rpx; border-radius: 200rpx; font-size: 26rpx; }
}
.fab {
  position: fixed; right: 32rpx; bottom: 160rpx; width: 112rpx; height: 112rpx; border-radius: 50%;
  background: linear-gradient(135deg, #7246F2, #9D7BFF); display: flex; align-items: center; justify-content: center;
  box-shadow: var(--shadow-fab); z-index: 100;
}
</style>
