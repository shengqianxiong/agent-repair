<template>
  <view class="page" v-if="member">
    <view class="level-card">
      <u-tag :text="member.levelName || `LV.${member.level || 1}`" type="warning" size="large"></u-tag>
      <text class="level-title">{{ member.levelName || '普通会员' }}</text>
      <view class="progress-wrap">
        <text class="progress-label">升级进度 {{ member.currentPoints || 0 }}/{{ member.nextLevelPoints || 1000 }}</text>
        <u-line-progress
          :percentage="progressPercent"
          activeColor="#7c3aed"
          height="12"
        ></u-line-progress>
      </view>
    </view>

    <u-cell-group title="会员权益">
      <u-cell
        v-for="(priv, idx) in member.privileges || defaultPrivileges"
        :key="idx"
        :title="priv"
        icon="checkmark-circle"
      ></u-cell>
    </u-cell-group>
  </view>
  <u-loading-page v-else loading></u-loading-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getMemberInfo } from '@/api/index.js'

const member = ref(null)
const defaultPrivileges = ['生日专属优惠', '积分双倍日', '优先预约权', '专属客服']

const progressPercent = computed(() => {
  if (!member.value) return 0
  const cur = member.value.currentPoints || 0
  const next = member.value.nextLevelPoints || 1000
  return Math.min(100, Math.round((cur / next) * 100))
})

async function loadData() {
  try {
    member.value = await getMemberInfo()
  } catch (e) {
    member.value = { level: 1, levelName: '普通会员', privileges: defaultPrivileges }
  }
}

onMounted(loadData)
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #0f0f1a; }
.level-card {
  margin: 32rpx;
  padding: 48rpx;
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 20rpx;
  text-align: center;
}
.level-title { font-size: 40rpx; font-weight: 700; color: #fff; display: block; margin: 16rpx 0; }
.progress-wrap { margin-top: 32rpx; text-align: left; }
.progress-label { color: #a0a0b8; font-size: 24rpx; margin-bottom: 12rpx; display: block; }
</style>
