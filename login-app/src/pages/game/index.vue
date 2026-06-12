<template>
  <view class="page">
    <scroll-view scroll-y class="page-body">
    <view v-if="games.length" class="game-list">
      <view v-for="item in games" :key="item.id" class="game-card" @click="goPlay(item)">
        <u-image :src="item.icon || defaultIcon" width="100rpx" height="100rpx" radius="12"></u-image>
        <view class="info">
          <text class="name">{{ item.name }}</text>
          <text class="reward">奖励 {{ item.rewardPoints || 0 }} 积分</text>
        </view>
        <u-icon name="arrow-right" color="#6b6b80"></u-icon>
      </view>
    </view>
    <u-empty v-else mode="list" text="暂无游戏"></u-empty>

    <view class="rank-preview" @click="goRank">
      <u-cell title="查看排行榜" isLink icon="list"></u-cell>
    </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getGameList } from '@/api/index.js'

const defaultIcon = 'https://cdn.uviewui.com/uview/album/4.jpg'
const games = ref([])

async function loadData() {
  try {
    const data = await getGameList()
    games.value = data?.list || data || []
  } catch (e) {
    games.value = []
  }
}

function goPlay(item) {
  uni.navigateTo({ url: `/pages/game/play?id=${item.id}&name=${encodeURIComponent(item.name)}&points=${item.rewardPoints || 0}` })
}

function goRank() {
  uni.switchTab({ url: '/pages/rank/index' })
}

onMounted(loadData)
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.game-list { padding: 24rpx; }
.game-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}
.info { flex: 1; }
.name { font-size: 28rpx; font-weight: 600; color: #fff; display: block; }
.reward { color: #f59e0b; font-size: 24rpx; margin-top: 8rpx; display: block; }
.rank-preview { margin: 24rpx 24rpx 0; }
</style>
