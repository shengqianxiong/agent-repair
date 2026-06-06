<template>
  <view class="bar-page">
    <u-navbar title="云享生活" :autoBack="false" bgColor="#1a1a2e" titleColor="#fff" leftIconColor="#fff">
      <template #left>
        <view class="nav-left">
          <u-icon name="list" color="#fff" size="22" @click="showMenu = true" />
        </view>
      </template>
      <template #right>
        <view class="nav-right" @click="goNotification">
          <u-icon name="bell" color="#fff" size="22" />
          <u-badge v-if="unreadCount > 0" :value="unreadCount" absolute :offset="[-4, -4]" />
        </view>
      </template>
    </u-navbar>

    <SideMenu v-model:show="showMenu" />

    <view class="welcome-card bar-gradient-header">
      <view class="welcome-inner">
        <u-avatar :src="userInfo.avatar" size="50" />
        <view class="welcome-text">
          <text class="greeting">Hi，{{ userInfo.nickname || '欢迎光临' }}</text>
          <view class="tags">
            <u-tag v-if="userInfo.memberLevelName" :text="userInfo.memberLevelName" type="warning" size="mini" />
            <text class="points">积分 {{ pointsBalance }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="quick-actions">
      <view class="quick-card" @click="goPage('/pages/order/index')">
        <u-icon name="shopping-cart-fill" color="#6c5ce7" size="36" />
        <text>自助点餐</text>
      </view>
      <view class="quick-card" @click="goPage('/pages/booking/index')">
        <u-icon name="calendar-fill" color="#fdcb6e" size="36" />
        <text>在线预约</text>
      </view>
    </view>

    <view class="bar-card">
      <view class="section-title">功能服务</view>
      <u-grid :col="4" :border="false">
        <u-grid-item v-for="item in gridItems" :key="item.path" @click="goPage(item.path)">
          <u-icon :name="item.icon" color="#6c5ce7" size="28" />
          <text class="grid-text">{{ item.name }}</text>
        </u-grid-item>
      </u-grid>
    </view>

    <view class="bar-card" v-if="activities.length">
      <view class="section-title">精选活动</view>
      <u-swiper :list="activitySwiper" keyName="url" height="160" radius="8" @click="onActivityClick" />
    </view>

    <u-button class="fab" type="primary" shape="circle" icon="plus" @click="goPage('/pages/order/index')" />

    <BarTabbar current="home" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import BarTabbar from '@/components/BarTabbar.vue'
import SideMenu from '@/components/SideMenu.vue'
import { getHomeIndex, login, getUserInfo } from '@/api/home'
import { getUnreadCount, getPointsBalance } from '@/api/marketing'
import { getToken, setToken, setUser, isLoggedIn } from '@/utils/auth'

const showMenu = ref(false)
const userInfo = ref({})
const pointsBalance = ref(0)
const unreadCount = ref(0)
const activities = ref([])
const activitySwiper = ref([])

const gridItems = [
  { name: '酒水套餐', icon: 'gift-fill', path: '/pages/package/list' },
  { name: '附近搭子', icon: 'account-fill', path: '/pages/partner/list' },
  { name: '团购核销', icon: 'scan', path: '/pages/coupon/verify' },
  { name: '互动游戏', icon: 'play-circle-fill', path: '/pages/game/index' },
  { name: '会员中心', icon: 'level', path: '/pages/member/index' },
  { name: '我的积分', icon: 'red-packet-fill', path: '/pages/points/index' },
  { name: '我的订单', icon: 'order', path: '/pages/order/list' },
  { name: '消息通知', icon: 'bell-fill', path: '/pages/notification/list' }
]

async function ensureLogin() {
  if (isLoggedIn()) return
  try {
    const data = await login({ openid: 'test001', nickname: '酒友' })
    if (data?.token) {
      setToken(data.token)
      if (data.user) setUser(data.user)
    }
  } catch (e) {
    console.warn('login failed', e)
  }
}

async function loadData() {
  await ensureLogin()
  try {
    const [home, user, unread, points] = await Promise.allSettled([
      getHomeIndex(),
      getUserInfo(),
      getUnreadCount(),
      getPointsBalance()
    ])
    if (home.status === 'fulfilled' && home.value) {
      activities.value = home.value.activities || home.value.activityList || []
      activitySwiper.value = activities.value.map((a) => ({
        url: a.image,
        title: a.title,
        id: a.id
      }))
      if (home.value.user) {
        userInfo.value = home.value.user
        setUser(home.value.user)
      }
    }
    if (user.status === 'fulfilled' && user.value) {
      userInfo.value = user.value
      setUser(user.value)
    }
    if (unread.status === 'fulfilled') {
      unreadCount.value = unread.value?.count ?? unread.value ?? 0
    }
    if (points.status === 'fulfilled') {
      pointsBalance.value = points.value?.balance ?? points.value ?? 0
    }
  } catch (e) {
    console.warn('load home failed', e)
  }
}

function goPage(url) {
  uni.navigateTo({ url })
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

onShow(() => {
  loadData()
})

onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.nav-left, .nav-right {
  position: relative;
  padding: 0 16rpx;
}

.welcome-card {
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
}

.welcome-inner {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.welcome-text {
  flex: 1;

  .greeting {
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
    display: block;
    margin-bottom: 12rpx;
  }

  .tags {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .points {
    color: rgba(255, 255, 255, 0.85);
    font-size: 24rpx;
  }
}

.quick-actions {
  display: flex;
  gap: 24rpx;
  padding: 0 24rpx;
  margin-bottom: 24rpx;
}

.quick-card {
  flex: 1;
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);

  text {
    font-size: 28rpx;
    color: #333;
  }
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  color: #333;
}

.grid-text {
  font-size: 22rpx;
  color: #666;
  margin-top: 8rpx;
}

.fab {
  position: fixed;
  right: 40rpx;
  bottom: 180rpx;
  width: 100rpx !important;
  height: 100rpx !important;
  z-index: 99;
}
</style>
