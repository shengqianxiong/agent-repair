<template>
  <u-tabbar
    :value="current"
    :fixed="true"
    :placeholder="true"
    :safeAreaInsetBottom="true"
    activeColor="#7c3aed"
    inactiveColor="#6b6b80"
    @change="onChange"
  >
    <u-tabbar-item name="home" text="首页" icon="home"></u-tabbar-item>
    <u-tabbar-item name="order" text="点餐" icon="bag"></u-tabbar-item>
    <u-tabbar-item name="rank" text="排行榜" icon="list"></u-tabbar-item>
    <u-tabbar-item name="profile" text="我的" icon="account"></u-tabbar-item>
  </u-tabbar>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  current: {
    type: String,
    default: 'home'
  }
})

const tabMap = {
  home: '/pages/index/index',
  order: '/pages/order/index',
  rank: '/pages/rank/index',
  profile: '/pages/profile/index'
}

function onChange(name) {
  if (name === props.current) return
  uni.switchTab({ url: tabMap[name] })
}
</script>
