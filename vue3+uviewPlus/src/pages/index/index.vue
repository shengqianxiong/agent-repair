<template>
  <view class="page">
    <u-navbar title="云享生活" :autoBack="false" bgColor="#1a1a2e" leftIcon="">
      <template #left>
        <u-icon name="list" size="22" color="#fff" @click="showMenu = true" />
      </template>
      <template #right>
        <view class="nav-right" @click="goNotification">
          <u-icon name="bell" size="22" color="#fff" />
          <u-badge v-if="unreadCount > 0" :value="unreadCount" :offset="[-4, -4]" absolute />
        </view>
      </template>
    </u-navbar>

    <scroll-view scroll-y class="content" @scrolltolower="() => {}">
      <!-- 会员欢迎卡 -->
      <view class="welcome-card">
        <u-avatar :src="userInfo.avatar" size="48" />
        <view class="welcome-info">
          <text class="welcome-text">欢迎回来，{{ userInfo.nickname || '酒友' }}</text>
          <view class="welcome-tags">
            <u-tag :text="userInfo.memberLevelName || '普通会员'" size="mini" type="warning" />
            <u-tag :text="`${points} 积分`" size="mini" type="success" plain />
          </view>
        </view>
      </view>

      <!-- 快捷入口 -->
      <view class="quick-actions">
        <view class="quick-card" @click="goPage('/pages/order/index')">
          <u-icon name="shopping-cart" size="36" color="#7c3aed" />
          <text>自助点餐</text>
        </view>
        <view class="quick-card" @click="goPage('/pages/booking/index')">
          <u-icon name="calendar" size="36" color="#10b981" />
          <text>在线预约</text>
        </view>
      </view>

      <!-- 功能宫格 -->
      <view class="section-title">功能服务</view>
      <u-grid :col="4" :border="false">
        <u-grid-item v-for="item in gridList" :key="item.path" @click="goPage(item.path)">
          <u-icon :name="item.icon" size="28" color="#a78bfa" />
          <text class="grid-text">{{ item.name }}</text>
        </u-grid-item>
      </u-grid>

      <!-- 精选活动轮播 -->
      <view class="section-title">精选活动</view>
      <u-swiper
        v-if="activities.length"
        :list="activitySwiper"
        keyName="image"
        height="300rpx"
        radius="12"
        indicator
        @click="onActivityClick"
      />
      <u-empty v-else mode="data" text="暂无活动" marginTop="20" />
    </scroll-view>

    <!-- 悬浮按钮 -->
    <view class="fab" @click="goPage('/pages/order/index')">
      <u-button type="primary" shape="circle" icon="plus" />
    </view>

    <SideMenu v-model:show="showMenu" />
    <AppTabbar active="home" />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getHomeIndex } from '@/api/home'
import { getUserInfo } from '@/api/user'
import { getPointsBalance } from '@/api/points'
import { getActivityList } from '@/api/activity'
import { getUnreadCount } from '@/api/notification'
import { goPage } from '@/utils/navigate'

const showMenu = ref(false)
const userInfo = ref({})
const points = ref(0)
const unreadCount = ref(0)
const activities = ref([])

const gridList = [
  { name: '酒水套餐', icon: 'gift', path: '/pages/package/list' },
  { name: '附近搭子', icon: 'man-add', path: '/pages/partner/list' },
  { name: '团购核销', icon: 'coupon', path: '/pages/coupon/verify' },
  { name: '互动游戏', icon: 'play-circle', path: '/pages/game/index' },
  { name: '会员中心', icon: 'level', path: '/pages/member/index' },
  { name: '我的积分', icon: 'integral', path: '/pages/points/index' },
  { name: '排行榜', icon: 'list', path: '/pages/rank/index' },
  { name: '消息通知', icon: 'bell', path: '/pages/notification/list' }
]

const activitySwiper = computed(() =>
  activities.value.map((a) => ({
    ...a,
    image: a.image || '/static/default-activity.png',
    title: a.title
  }))
)

onShow(() => loadData())
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})

async function loadData() {
  await Promise.allSettled([
    loadHome(),
    loadUser(),
    loadPoints(),
    loadUnread(),
    loadActivities()
  ])
}

async function loadHome() {
  try {
    const data = await getHomeIndex()
    if (data) {
      if (data.user) userInfo.value = { ...userInfo.value, ...data.user }
      if (data.activities) activities.value = data.activities
      if (data.points !== undefined) points.value = data.points
    }
  } catch { /* 首页聚合接口待服务端实现 */ }
}

async function loadUser() {
  try {
    userInfo.value = await getUserInfo() || {}
  } catch {
    userInfo.value = { nickname: '酒友', memberLevelName: '普通会员' }
  }
}

async function loadPoints() {
  try {
    const data = await getPointsBalance()
    points.value = data?.balance ?? data ?? 0
  } catch { /* */ }
}

async function loadUnread() {
  try {
    const data = await getUnreadCount()
    unreadCount.value = data?.count ?? data ?? 0
  } catch { /* */ }
}

async function loadActivities() {
  try {
    const data = await getActivityList({ page: 1, pageSize: 5 })
    activities.value = data?.list || []
  } catch { /* */ }
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
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 120rpx;
}
.content {
  height: calc(100vh - 88rpx - 100rpx);
  padding: 24rpx;
}
.nav-right {
  position: relative;
  padding: 8rpx;
}
.welcome-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 16rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
}
.welcome-info { flex: 1; }
.welcome-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
  display: block;
  margin-bottom: 12rpx;
}
.welcome-tags {
  display: flex;
  gap: 12rpx;
}
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
  font-size: 28rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
  margin: 24rpx 0 16rpx;
}
.grid-text {
  font-size: 24rpx;
  color: #a0a0b8;
  margin-top: 8rpx;
}
.fab {
  position: fixed;
  right: 40rpx;
  bottom: 180rpx;
  z-index: 99;
}
</style>
