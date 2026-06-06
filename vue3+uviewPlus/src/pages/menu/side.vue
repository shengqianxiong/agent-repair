<template>
  <view class="page">
    <u-cell-group>
      <u-cell
        v-for="item in menuList"
        :key="item.path"
        :title="item.title"
        :icon="item.icon"
        isLink
        @click="go(item.path)"
      />
    </u-cell-group>
  </view>
</template>

<script setup>
const menuList = [
  { title: '首页', icon: 'home', path: '/pages/index/index' },
  { title: '自助点餐', icon: 'shopping-cart', path: '/pages/order/index' },
  { title: '在线预约', icon: 'calendar', path: '/pages/booking/index' },
  { title: '酒水套餐', icon: 'gift', path: '/pages/package/list' },
  { title: '会员中心', icon: 'level', path: '/pages/member/index' },
  { title: '我的积分', icon: 'integral', path: '/pages/points/index' },
  { title: '团购核销', icon: 'coupon', path: '/pages/coupon/verify' },
  { title: '附近搭子', icon: 'man-add', path: '/pages/partner/list' },
  { title: '互动游戏', icon: 'play-circle', path: '/pages/game/index' },
  { title: '排行榜', icon: 'list', path: '/pages/rank/index' },
  { title: '消息通知', icon: 'bell', path: '/pages/notification/list' },
  { title: '个人中心', icon: 'account', path: '/pages/profile/index' }
]

function go(path) {
  const tabs = ['/pages/index/index', '/pages/order/index', '/pages/rank/index', '/pages/profile/index']
  if (tabs.includes(path)) {
    uni.switchTab({ url: path })
  } else {
    uni.navigateTo({ url: path })
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
}
</style>
