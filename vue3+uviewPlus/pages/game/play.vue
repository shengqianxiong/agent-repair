<template>
  <view class="bar-page play-page">
    <view class="score-board">
      <text class="label">当前得分</text>
      <text class="score">{{ score }}</text>
      <text class="timer">剩余 {{ timeLeft }}s</text>
    </view>

    <view class="game-area" @click="tapGame">
      <canvas canvas-id="gameCanvas" class="game-canvas" />
      <text class="hint">快速点击屏幕得分！</text>
    </view>

    <view class="bottom-bar safe-bottom">
      <u-button type="primary" text="提交成绩" :disabled="!finished" :loading="submitting" @click="submit" />
    </view>
  </view>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { submitGameScore } from '@/api/social'

const gameId = ref('')
const gameName = ref('')
const reward = ref(0)
const score = ref(0)
const timeLeft = ref(30)
const finished = ref(false)
const submitting = ref(false)
let timer = null

function tapGame() {
  if (finished.value) return
  score.value++
}

function startTimer() {
  timer = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 0) {
      finished.value = true
      clearInterval(timer)
    }
  }, 1000)
}

async function submit() {
  submitting.value = true
  try {
    await submitGameScore({ gameId: gameId.value, score: score.value })
    uni.showToast({ title: `获得 ${reward.value} 积分`, icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } finally {
    submitting.value = false
  }
}

onLoad((opts) => {
  gameId.value = opts.id
  gameName.value = opts.name
  reward.value = Number(opts.reward) || 0
  uni.setNavigationBarTitle({ title: gameName.value || '游戏中' })
  startTimer()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style lang="scss" scoped>
.play-page {
  padding-bottom: 160rpx;
}

.score-board {
  background: linear-gradient(135deg, #1a1a2e, #6c5ce7);
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  text-align: center;
  color: #fff;

  .label {
    font-size: 24rpx;
    opacity: 0.8;
    display: block;
  }

  .score {
    font-size: 72rpx;
    font-weight: bold;
    display: block;
    margin: 12rpx 0;
  }

  .timer {
    font-size: 26rpx;
  }
}

.game-area {
  margin: 24rpx;
  background: #fff;
  border-radius: 16rpx;
  height: 500rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.game-canvas {
  width: 100%;
  height: 400rpx;
}

.hint {
  color: #999;
  font-size: 26rpx;
  margin-top: 16rpx;
}

.bottom-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 40rpx;
}
</style>
