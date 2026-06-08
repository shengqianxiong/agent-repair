<template>
  <app-tabbar :current="current"></app-tabbar>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const current = ref('home')

const routeMap = {
  'pages/index/index': 'home',
  'pages/order/index': 'order',
  'pages/rank/index': 'rank',
  'pages/profile/index': 'profile'
}

onShow(() => {
  const pages = getCurrentPages()
  const route = pages[pages.length - 1]?.route || ''
  current.value = routeMap[route] || 'home'
})
</script>
