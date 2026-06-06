<template>
  <SideMenu :show="true" @close="goBack" />
</template>

<script setup>
import SideMenu from '@/components/SideMenu.vue'

function goBack() {
  uni.navigateBack()
}
</script>
