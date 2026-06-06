<template>
  <view class="bar-page">
    <view v-if="!games.length && !loading" class="empty-wrap">
      <u-empty mode="list" text="暂无游戏" />
    </view>

    <view v-for="item in games" :key="item.id" class="bar-card game-card">
      <u-image :src="item.icon" width="100rpx" height="100rpx" radius="12" />
      <view class="info">
        <text class="name">{{ item.name }}</text>
        <text class="reward">奖励 {{ item.rewardPoints || 0 }} 积分</text>
        <text class="rules u-line-2">{{ item.rules }}</text>
      </view>
      <u-button type="primary" size="mini" text="开始" @click="goPlay(item)" />
    </view>

    <view class="bar-card">
      <u-cell title="查看排行榜" isLink @click="goRank" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getGameList } from '@/api/social'

const games = ref([])
const loading = ref(false)

async function loadGames() {
  loading.value = true
  try {
    const res = await getGameList()
    games.value = res?.list || res || []
  } finally {
    loading.value = false
  }
}

function goPlay(item) {
  uni.navigateTo({ url: `/pages/game/play?id=${item.id}&name=${item.name}&reward=${item.rewardPoints || 0}` })
}

function goRank() {
  uni.reLaunch({ url: '/pages/rank/index' })
}

onShow(() => loadGames())
onPullDownRefresh(async () => { await loadGames(); uni.stopPullDownRefresh() })
</script>

<style lang="scss" scoped>
.game-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.info {
  flex: 1;

  .name {
    display: block;
    font-size: 30rpx;
    font-weight: bold;
  }

  .reward {
    font-size: 24rpx;
    color: #6c5ce7;
    margin: 8rpx 0;
    display: block;
  }

  .rules {
    font-size: 22rpx;
    color: #999;
  }
}

.empty-wrap {
  padding-top: 120rpx;
}
</style>
