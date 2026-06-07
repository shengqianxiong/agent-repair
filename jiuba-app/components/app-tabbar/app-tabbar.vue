<template>
  <view class="tabbar-placeholder"></view>
  <view class="app-tabbar">
    <view
      v-for="item in tabs"
      :key="item.name"
      class="tab-item"
      :class="{ active: current === item.name }"
      @click="onTab(item)"
    >
      <view class="tab-icon-wrap">
        <u-icon :name="item.icon" :color="current === item.name ? '#7B61FF' : '#999999'" size="22"></u-icon>
        <view v-if="current === item.name" class="tab-dot"></view>
      </view>
      <text class="tab-text">{{ item.text }}</text>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  current: { type: String, default: 'home' }
})

const tabs = [
  { name: 'home', text: '首页', icon: 'home', path: '/pages/home/index' },
  { name: 'menu', text: '点餐', icon: 'bag', path: '/pages/menu/index' },
  { name: 'square', text: '排行榜', icon: 'list', path: '/pages/square/index' },
  { name: 'my', text: '我的', icon: 'account', path: '/pages/my/index' }
]

function onTab(item) {
  if (item.name === props.current) return
  uni.switchTab({ url: item.path })
}
</script>

<style lang="scss" scoped>
.tabbar-placeholder {
  height: 120rpx;
  padding-bottom: env(safe-area-inset-bottom);
}
.app-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  display: flex;
  height: 100rpx;
  padding-bottom: env(safe-area-inset-bottom);
  background: #FFFFFF;
  border-top: 1rpx solid #EEEEEE;
  box-shadow: var(--shadow-bottom-bar);
}
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
}
.tab-icon-wrap {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tab-dot {
  position: absolute;
  bottom: -6rpx;
  width: 8rpx;
  height: 8rpx;
  border-radius: 50%;
  background: #7B61FF;
}
.tab-text {
  font-size: 22rpx;
  color: #999999;
}
.tab-item.active .tab-text {
  color: #7B61FF;
  font-weight: 500;
}
</style>
