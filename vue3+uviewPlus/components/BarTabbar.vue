<template>
  <u-tabbar
    :value="current"
    :fixed="true"
    :placeholder="true"
    :safeAreaInsetBottom="true"
    activeColor="#6c5ce7"
    inactiveColor="#999"
    @change="onChange"
  >
    <u-tabbar-item text="首页" icon="home" name="home" />
    <u-tabbar-item text="点餐" icon="shopping-cart" name="order" />
    <u-tabbar-item text="排行榜" icon="list" name="rank" />
    <u-tabbar-item text="我的" icon="account" name="profile" />
  </u-tabbar>
</template>

<script setup>
const props = defineProps({
  current: {
    type: String,
    default: 'home'
  }
})

const routes = {
  home: '/pages/index/index',
  order: '/pages/order/index',
  rank: '/pages/rank/index',
  profile: '/pages/profile/index'
}

function onChange(name) {
  if (name === props.current) return
  const url = routes[name]
  if (url) {
    uni.reLaunch({ url })
  }
}
</script>
