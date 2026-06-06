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
    <u-tabbar-item text="首页" icon="home" name="home" />
    <u-tabbar-item text="点餐" icon="shopping-cart" name="order" />
    <u-tabbar-item text="排行榜" icon="list" name="rank" />
    <u-tabbar-item text="我的" icon="account" name="profile" />
  </u-tabbar>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  active: {
    type: String,
    default: 'home'
  }
})

const routeMap = {
  home: '/pages/index/index',
  order: '/pages/order/index',
  rank: '/pages/rank/index',
  profile: '/pages/profile/index'
}

const current = computed(() => props.active)

function onChange(name) {
  const url = routeMap[name]
  if (url) {
    uni.switchTab({ url })
  }
}
</script>
