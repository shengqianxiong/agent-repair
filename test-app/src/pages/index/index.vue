<template>
  <view class="page page-fixed">
    <u-navbar title="云享生活" :autoBack="false" bgColor="#1a1a2e" titleStyle="color:#fff">
      <template #left>
        <view class="nav-left" @click="openMenu">
          <u-icon name="list" color="#fff" size="22"></u-icon>
        </view>
      </template>
      <template #right>
        <view class="nav-right" @click="goNotification">
          <u-icon name="bell" color="#fff" size="22"></u-icon>
          <u-badge v-if="unreadCount > 0" :value="unreadCount" absolute :offset="[-4, -4]"></u-badge>
        </view>
      </template>
    </u-navbar>

    <scroll-view scroll-y class="page-body content" :show-scrollbar="false" @scrolltolower="loadMore">
      <!-- 欢迎卡 -->
      <view class="welcome-card">
        <view class="welcome-info">
          <u-avatar :src="userInfo.avatar || ''" size="56"></u-avatar>
          <view class="welcome-text">
            <text class="nickname">{{ userInfo.nickname || '欢迎光临' }}</text>
            <u-tag :text="userInfo.memberLevelName || '普通会员'" type="warning" size="mini" plain></u-tag>
          </view>
        </view>
        <view class="points-row">
          <text class="points-label">积分余额</text>
          <text class="points-value">{{ pointsBalance }}</text>
        </view>
      </view>

      <!-- 快捷入口 -->
      <view class="quick-actions">
        <view class="quick-card" @click="goPage('/pages/order/index', true)">
          <u-icon name="bag" color="#7c3aed" size="36"></u-icon>
          <text>自助点餐</text>
        </view>
        <view class="quick-card" @click="goPage('/pages/booking/index')">
          <u-icon name="calendar" color="#f59e0b" size="36"></u-icon>
          <text>在线预约</text>
        </view>
      </view>

      <!-- 功能宫格 -->
      <view class="section-title">更多服务</view>
      <u-grid :col="4" border>
        <u-grid-item v-for="item in functionGrid" :key="item.path" @click="goPage(item.path)">
          <u-icon :name="item.icon" color="#a78bfa" size="28"></u-icon>
          <text class="grid-text">{{ item.name }}</text>
        </u-grid-item>
      </u-grid>

      <!-- 精选活动 -->
      <view class="section-title">精选活动</view>
      <u-swiper
        v-if="activities.length"
        :list="activitySwiperList"
        keyName="image"
        height="320rpx"
        radius="12"
        indicator
        circular
        @click="onActivityClick"
      ></u-swiper>
      <u-empty v-else mode="data" text="暂无活动"></u-empty>
    </scroll-view>

    <!-- 悬浮按钮 -->
    <view class="fab" @click="goPage('/pages/cart/index')">
      <u-button type="primary" shape="circle" icon="shopping-cart" customStyle="width:100rpx;height:100rpx"></u-button>
    </view>

    <app-tabbar current="home"></app-tabbar>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getHomeIndex, getUnreadCount, getPointsBalance, getUserInfo } from '@/api/index.js'
import { switchTab } from '@/utils/common.js'

const userInfo = ref({})
const pointsBalance = ref(0)
const unreadCount = ref(0)
const activities = ref([])

const functionGrid = [
  { name: '酒水套餐', icon: 'gift', path: '/pages/package/list' },
  { name: '附近搭子', icon: 'account-fill', path: '/pages/partner/list' },
  { name: '团购核销', icon: 'scan', path: '/pages/coupon/verify' },
  { name: '互动游戏', icon: 'play-circle', path: '/pages/game/index' },
  { name: '会员中心', icon: 'star', path: '/pages/member/index' },
  { name: '我的积分', icon: 'integral', path: '/pages/points/index' },
  { name: '我的订单', icon: 'order', path: '/pages/order/list' },
  { name: '消息通知', icon: 'bell', path: '/pages/notification/list' }
]

const activitySwiperList = computed(() =>
  activities.value.map((a) => ({
    ...a,
    image: a.image || 'https://cdn.uviewui.com/uview/swiper/1.jpg',
    title: a.title
  }))
)

async function loadData() {
  try {
    const [home, unread, points, user] = await Promise.allSettled([
      getHomeIndex(),
      getUnreadCount(),
      getPointsBalance(),
      getUserInfo()
    ])
    if (home.status === 'fulfilled' && home.value) {
      activities.value = home.value.activities || home.value.activityList || []
      if (home.value.user) userInfo.value = home.value.user
    }
    if (unread.status === 'fulfilled') unreadCount.value = unread.value?.count ?? unread.value ?? 0
    if (points.status === 'fulfilled') pointsBalance.value = points.value?.balance ?? points.value ?? 0
    if (user.status === 'fulfilled' && user.value) userInfo.value = user.value
  } catch (e) {
    console.error(e)
  }
}

function goPage(url, isTab = false) {
  if (isTab) {
    switchTab(url)
  } else {
    uni.navigateTo({ url })
  }
}

function openMenu() {
  uni.navigateTo({ url: '/pages/menu/side' })
}

function goNotification() {
  uni.navigateTo({ url: '/pages/notification/list' })
}

function onActivityClick(index) {
  const item = activities.value[index]
  if (item?.id) {
    uni.navigateTo({ url: `/pages/activity/detail?id=${item.id}` })
  }
}

function loadMore() {}

onMounted(loadData)
onShow(loadData)
</script>

<style lang="scss" scoped>
.page {
  background: #0f0f1a;
}
.content {
  padding: 24rpx;
}
.nav-left, .nav-right {
  position: relative;
  padding: 0 16rpx;
}
.welcome-card {
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}
.welcome-info {
  display: flex;
  align-items: center;
  gap: 20rpx;
}
.welcome-text {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}
.nickname {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}
.points-row {
  margin-top: 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.points-label { color: #a0a0b8; }
.points-value { font-size: 40rpx; font-weight: 700; color: #f59e0b; }
.quick-actions {
  display: flex;
  gap: 20rpx;
  margin-bottom: 32rpx;
}
.quick-card {
  flex: 1;
  background: #1a1a2e;
  border-radius: 16rpx;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  color: #e8e8f0;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  margin: 24rpx 0 16rpx;
  color: #e8e8f0;
}
.grid-text {
  font-size: 24rpx;
  margin-top: 8rpx;
  color: #a0a0b8;
}
.fab {
  position: fixed;
  right: 32rpx;
  bottom: 160rpx;
  z-index: 99;
}
</style>
