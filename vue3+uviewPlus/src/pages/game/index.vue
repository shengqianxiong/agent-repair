<template>
  <view class="page">
    <view v-if="list.length" class="game-list">
      <view v-for="item in list" :key="item.id" class="game-card" @click="goPlay(item)">
        <u-image :src="item.icon" width="120rpx" height="120rpx" radius="12" />
        <view class="info">
          <text class="name">{{ item.name }}</text>
          <text class="rules">{{ item.rules || '参与游戏赢积分' }}</text>
          <u-tag :text="`+${item.rewardPoints || 0} 积分`" size="mini" type="warning" />
        </view>
        <u-icon name="arrow-right" color="#6b6b80" />
      </view>
    </view>
    <u-empty v-else mode="list" text="暂无游戏" />

    <view class="rank-preview" @click="goRank">
      <u-cell title="查看排行榜" icon="list" isLink />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getGameList } from '@/api/game'

const list = ref([])

onShow(() => loadGames())
onPullDownRefresh(async () => {
  await loadGames()
  uni.stopPullDownRefresh()
})

async function loadGames() {
  try {
    const data = await getGameList()
    list.value = data?.list || data || []
  } catch {
    list.value = []
  }
}

function goPlay(item) {
  uni.navigateTo({ url: `/pages/game/play?id=${item.id}&name=${item.name}&reward=${item.rewardPoints || 0}` })
}

function goRank() {
  uni.switchTab({ url: '/pages/rank/index' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 16rpx;
}
.game-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
}
.info { flex: 1; }
.name {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
  display: block;
}
.rules {
  font-size: 24rpx;
  color: #6b6b80;
  margin: 8rpx 0;
  display: block;
}
.rank-preview {
  margin-top: 32rpx;
}
</style>
