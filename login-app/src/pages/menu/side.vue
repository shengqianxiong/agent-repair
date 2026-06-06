<template>
  <view class="page">
    <u-navbar title="导航菜单" :autoBack="true" bgColor="#1a1a2e" titleStyle="color:#fff"></u-navbar>
    <u-popup :show="true" mode="left" :overlay="true" @close="goBack">
      <view class="drawer">
        <view class="drawer-header">
          <text class="drawer-title">云享生活</text>
          <text class="drawer-sub">酒吧点餐小程序</text>
        </view>
        <u-cell-group>
          <u-cell
            v-for="item in menuList"
            :key="item.path"
            :title="item.title"
            :icon="item.icon"
            isLink
            @click="navigate(item)"
          ></u-cell>
        </u-cell-group>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
const menuList = [
  { title: '首页', icon: 'home', path: '/pages/index/index', tab: true },
  { title: '自助点餐', icon: 'bag', path: '/pages/order/index', tab: true },
  { title: '在线预约', icon: 'calendar', path: '/pages/booking/index' },
  { title: '酒水套餐', icon: 'gift', path: '/pages/package/list' },
  { title: '精选活动', icon: 'star', path: '/pages/index/index', tab: true },
  { title: '会员中心', icon: 'level', path: '/pages/member/index' },
  { title: '我的积分', icon: 'integral', path: '/pages/points/index' },
  { title: '团购核销', icon: 'scan', path: '/pages/coupon/verify' },
  { title: '附近搭子', icon: 'account', path: '/pages/partner/list' },
  { title: '互动游戏', icon: 'play-circle', path: '/pages/game/index' },
  { title: '排行榜', icon: 'list', path: '/pages/rank/index', tab: true },
  { title: '消息通知', icon: 'bell', path: '/pages/notification/list' },
  { title: '个人中心', icon: 'account-fill', path: '/pages/profile/index', tab: true }
]

function navigate(item) {
  if (item.tab) {
    uni.switchTab({ url: item.path })
  } else {
    uni.navigateTo({ url: item.path })
  }
}

function goBack() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; }
.drawer {
  width: 560rpx;
  height: 100vh;
  background: #1a1a2e;
  padding-top: 80rpx;
}
.drawer-header {
  padding: 32rpx;
  border-bottom: 1rpx solid #2a2a40;
}
.drawer-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
  display: block;
}
.drawer-sub {
  font-size: 24rpx;
  color: #6b6b80;
  margin-top: 8rpx;
  display: block;
}
</style>
