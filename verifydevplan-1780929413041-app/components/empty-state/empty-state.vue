<template>
  <view class="empty-state">
    <u-icon :name="icon" color="#CCCCCC" size="48"></u-icon>
    <text class="empty-text">{{ text }}</text>
    <u-button
      v-if="buttonText"
      type="primary"
      :text="buttonText"
      shape="circle"
      size="small"
      @click="$emit('action')"
    ></u-button>
  </view>
</template>

<script setup>
defineProps({
  text: { type: String, default: '暂无数据' },
  icon: { type: String, default: 'file-text' },
  buttonText: { type: String, default: '' }
})
defineEmits(['action'])
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx var(--page-padding);
  gap: var(--spacing-md);
}
.empty-text {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}
</style>
