<template>
  <app-tabbar :current="current"></app-tabbar>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const current = ref('task')

const routeMap = {
  'pages/task/index': 'task',
  'pages/user/records': 'records',
  'pages/user/center': 'center'
}

onShow(() => {
  const pages = getCurrentPages()
  const route = pages[pages.length - 1]?.route || ''
  current.value = routeMap[route] || 'task'
})
</script>
