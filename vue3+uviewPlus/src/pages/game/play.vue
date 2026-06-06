<template>
  <view class="page">
    <view class="score-board">
      <text class="label">当前得分</text>
      <text class="score">{{ score }}</text>
      <text class="reward-tip">完成可获得 {{ rewardPoints }} 积分</text>
    </view>

    <view class="game-area">
      <canvas
        canvas-id="gameCanvas"
        class="game-canvas"
        @touchstart="onTap"
      />
      <text class="game-hint">点击屏幕增加分数</text>
    </view>

    <view class="actions">
      <u-button type="primary" text="提交成绩" :loading="submitting" @click="submit" />
      <u-button icon="share" text="分享" open-type="share" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { submitGameScore } from '@/api/game'

const gameId = ref('')
const gameName = ref('')
const rewardPoints = ref(0)
const score = ref(0)
const submitting = ref(false)

onLoad((options) => {
  gameId.value = options.id || ''
  gameName.value = options.name || '游戏'
  rewardPoints.value = Number(options.reward || 0)
  uni.setNavigationBarTitle({ title: gameName.value })
})

function onTap() {
  score.value += Math.floor(Math.random() * 10) + 1
}

async function submit() {
  if (score.value <= 0) {
    uni.showToast({ title: '请先参与游戏', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await submitGameScore({ gameId: gameId.value, score: score.value })
    uni.showToast({ title: `获得 ${rewardPoints.value} 积分`, icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } catch (e) {
    uni.showToast({ title: e.msg || '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
  padding-bottom: 160rpx;
}
.score-board {
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 32rpx;
  text-align: center;
}
.label {
  font-size: 26rpx;
  color: #a0a0b8;
  display: block;
}
.score {
  font-size: 64rpx;
  font-weight: 700;
  color: #7c3aed;
  display: block;
  margin: 12rpx 0;
}
.reward-tip {
  font-size: 24rpx;
  color: #f59e0b;
}
.game-area {
  margin: 32rpx 0;
  text-align: center;
}
.game-canvas {
  width: 100%;
  height: 400rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
}
.game-hint {
  font-size: 24rpx;
  color: #6b6b80;
  margin-top: 12rpx;
  display: block;
}
.actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #1a1a2e;
}
</style>
