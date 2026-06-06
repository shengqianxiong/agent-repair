<template>
  <view class="bar-page" v-if="member">
    <view class="member-header bar-gradient-header">
      <u-tag :text="member.levelName || '普通会员'" type="warning" size="large" />
      <text class="level">LV.{{ member.level || 1 }}</text>
      <u-line-progress :percentage="progress" activeColor="#fdcb6e" height="12" />
      <text class="progress-text">距离下一等级还需 {{ member.nextLevelPoints || 0 }} 积分</text>
    </view>

    <view class="bar-card">
      <view class="section-title">会员权益</view>
      <u-cell v-for="(p, idx) in privileges" :key="idx" :title="p" icon="checkmark-circle" />
      <u-empty v-if="!privileges.length" mode="list" text="暂无权益" />
    </view>
  </view>
  <u-loading-page v-else loading />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getMemberInfo } from '@/api/marketing'

const member = ref(null)

const privileges = computed(() => member.value?.privileges || [])
const progress = computed(() => {
  if (!member.value) return 0
  const cur = member.value.currentPoints || 0
  const next = member.value.nextLevelMinPoints || 100
  return Math.min(100, Math.round((cur / next) * 100))
})

async function loadMember() {
  member.value = await getMemberInfo()
}

onShow(() => loadMember())
onPullDownRefresh(async () => { await loadMember(); uni.stopPullDownRefresh() })
</script>

<style lang="scss" scoped>
.member-header {
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 40rpx 32rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;

  .level {
    color: #fff;
    font-size: 48rpx;
    font-weight: bold;
  }

  .progress-text {
    color: rgba(255, 255, 255, 0.8);
    font-size: 22rpx;
  }
}

.section-title {
  font-weight: bold;
  margin-bottom: 16rpx;
}
</style>
