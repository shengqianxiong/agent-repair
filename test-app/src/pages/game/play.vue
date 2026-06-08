<template>
  <view class="page page-dark">
    <scroll-view scroll-y class="page-scroll page-pad">
    <view class="score-board">
      <text class="label">当前得分</text>
      <text class="score">{{ score }}</text>
      <text class="reward-tip">完成可获得 {{ rewardPoints }} 积分</text>
    </view>

    <view class="game-area">
      <canvas canvas-id="gameCanvas" class="canvas" @tap="onTap"></canvas>
      <text class="hint">点击屏幕增加分数，30 秒后自动结束</text>
    </view>

    <view class="actions">
      <u-button type="primary" text="提交成绩" :loading="submitting" :disabled="!started" @click="submit"></u-button>
      <u-button type="info" text="分享" open-type="share"></u-button>
    </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { submitGameScore } from '@/api/index.js'

const gameId = ref('')
const gameName = ref('')
const rewardPoints = ref(0)
const score = ref(0)
const started = ref(false)
const submitting = ref(false)
let timer = null
let countdown = null

function onTap() {
  if (!started.value) {
    started.value = true
    countdown = setTimeout(() => {
      uni.showToast({ title: '游戏结束', icon: 'none' })
    }, 30000)
  }
  score.value += Math.floor(Math.random() * 5) + 1
}

async function submit() {
  submitting.value = true
  try {
    await submitGameScore({ gameId: gameId.value, score: score.value })
    uni.showToast({ title: `获得 ${rewardPoints.value} 积分`, icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const opts = pages[pages.length - 1].options || {}
  gameId.value = opts.id || ''
  gameName.value = decodeURIComponent(opts.name || '互动游戏')
  rewardPoints.value = Number(opts.points) || 0
  uni.setNavigationBarTitle({ title: gameName.value })
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  if (countdown) clearTimeout(countdown)
})
</script>

<style lang="scss" scoped>
.page-pad { padding: 32rpx; }
.score-board { text-align: center; padding: 32rpx; }
.label { color: #a0a0b8; display: block; }
.score { font-size: 80rpx; font-weight: 700; color: #7c3aed; display: block; margin: 16rpx 0; }
.reward-tip { color: #f59e0b; font-size: 24rpx; }
.game-area { margin: 32rpx 0; text-align: center; }
.canvas {
  width: 100%;
  height: 400rpx;
  background: #1a1a2e;
  border-radius: 16rpx;
}
.hint { color: #6b6b80; font-size: 24rpx; margin-top: 16rpx; display: block; }
.actions { display: flex; gap: 20rpx; margin-top: 32rpx; }
</style>
