<template>
  <view class="page" v-if="member">
    <view class="level-card">
      <u-icon name="level" size="48" color="#f59e0b" />
      <text class="level-name">{{ member.levelName || '普通会员' }}</text>
      <text class="level-num">LV.{{ member.level || 1 }}</text>
    </view>

    <view class="progress-section">
      <text class="progress-label">升级进度</text>
      <u-line-progress
        :percentage="progressPercent"
        activeColor="#7c3aed"
        :showText="true"
      />
      <text class="progress-tip">还需 {{ member.pointsToNext || 0 }} 积分升级</text>
    </view>

    <view class="section-title">会员权益</view>
    <u-cell-group>
      <u-cell
        v-for="(item, idx) in member.privileges || []"
        :key="idx"
        :title="item"
        icon="checkmark-circle"
      />
    </u-cell-group>
    <u-empty v-if="!(member.privileges || []).length" mode="list" text="暂无权益" />
  </view>
  <u-loading-page :loading="loading" />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMemberInfo } from '@/api/member'

const member = ref(null)
const loading = ref(true)

const progressPercent = computed(() => {
  if (!member.value) return 0
  const current = member.value.currentPoints || 0
  const next = member.value.nextLevelPoints || 1
  return Math.min(100, Math.round((current / next) * 100))
})

onShow(() => loadMember())

async function loadMember() {
  loading.value = true
  try {
    member.value = await getMemberInfo()
  } catch {
    member.value = {
      level: 1,
      levelName: '普通会员',
      privileges: ['生日礼遇', '积分加倍'],
      currentPoints: 0,
      nextLevelPoints: 1000,
      pointsToNext: 1000
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
}
.level-card {
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 16rpx;
  padding: 48rpx;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}
.level-name {
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
}
.level-num {
  font-size: 28rpx;
  color: #f59e0b;
}
.progress-section {
  margin: 24rpx 0;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 24rpx;
}
.progress-label {
  font-size: 28rpx;
  color: #fff;
  display: block;
  margin-bottom: 16rpx;
}
.progress-tip {
  font-size: 24rpx;
  color: #6b6b80;
  margin-top: 12rpx;
  display: block;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
  margin: 24rpx 0 16rpx;
}
</style>
