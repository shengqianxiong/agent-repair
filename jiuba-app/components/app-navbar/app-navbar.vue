<template>
  <view class="app-navbar" :style="{ background: bgColor }">
    <view class="navbar-inner" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-left" @click="onBack">
        <u-icon v-if="showBack" name="arrow-left" :color="color" size="20"></u-icon>
        <slot v-else name="left"></slot>
      </view>
      <view class="navbar-title" :style="{ color }">{{ title }}</view>
      <view class="navbar-right" @click="onRight">
        <slot name="right">
          <u-icon v-if="rightIcon" :name="rightIcon" :color="color" size="20"></u-icon>
        </slot>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  title: { type: String, default: '' },
  showBack: { type: Boolean, default: true },
  color: { type: String, default: '#7B61FF' },
  bgColor: { type: String, default: 'transparent' },
  rightIcon: { type: String, default: '' }
})

const emit = defineEmits(['back', 'right'])

const statusBarHeight = ref(0)

onMounted(() => {
  const info = uni.getSystemInfoSync()
  statusBarHeight.value = info.statusBarHeight || 0
})

function onBack() {
  if (props.showBack) {
    emit('back')
    uni.navigateBack({ delta: 1, fail: () => uni.switchTab({ url: '/pages/home/index' }) })
  }
}

function onRight() {
  emit('right')
}
</script>

<style lang="scss" scoped>
.app-navbar {
  position: relative;
  z-index: 100;
}
.navbar-inner {
  display: flex;
  align-items: center;
  height: 88rpx;
  padding: 0 32rpx;
}
.navbar-left,
.navbar-right {
  width: 80rpx;
  display: flex;
  align-items: center;
}
.navbar-right {
  justify-content: flex-end;
}
.navbar-title {
  flex: 1;
  text-align: center;
  font-size: 36rpx;
  font-weight: 600;
}
</style>
