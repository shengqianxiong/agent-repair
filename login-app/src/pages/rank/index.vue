<template>
  <view class="page">
    <u-navbar title="排行榜" :autoBack="false" bgColor="#1a1a2e" titleStyle="color:#fff"></u-navbar>

    <u-tabs :list="tabList" :current="currentTab" @change="onTabChange" lineColor="#7c3aed"></u-tabs>

    <!-- 我的排名 -->
    <view v-if="myRank" class="my-rank-card">
      <text class="label">我的排名</text>
      <view class="my-rank-info">
        <text class="rank-num">#{{ myRank.rank || '-' }}</text>
        <u-avatar :src="myRank.avatar || ''" size="48"></u-avatar>
        <text class="nickname">{{ myRank.nickname || '我' }}</text>
        <text class="score">{{ myRank.score || 0 }}</text>
      </view>
    </view>

    <view v-if="rankList.length" class="rank-list">
      <view v-for="(item, idx) in rankList" :key="item.userId || idx" class="rank-item">
        <text class="rank" :class="{ top: idx < 3 }">{{ idx + 1 }}</text>
        <u-avatar :src="item.avatar || ''" size="40"></u-avatar>
        <text class="name">{{ item.nickname }}</text>
        <text class="score">{{ item.score }}</text>
      </view>
    </view>
    <u-empty v-else mode="list" text="暂无排名数据"></u-empty>

    <app-tabbar current="rank"></app-tabbar>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getRankList, getMyRank } from '@/api/index.js'

const tabList = [
  { name: '消费榜' },
  { name: '积分榜' },
  { name: '游戏榜' }
]
const currentTab = ref(0)
const rankList = ref([])
const myRank = ref(null)

const rankTypes = ['消费', '积分', '游戏']

async function loadData() {
  const rankType = rankTypes[currentTab.value]
  try {
    const [list, my] = await Promise.allSettled([
      getRankList({ rankType, period: '月' }),
      getMyRank({ rankType, period: '月' })
    ])
    if (list.status === 'fulfilled') {
      rankList.value = list.value?.list || list.value || []
    }
    if (my.status === 'fulfilled') {
      myRank.value = my.value
    }
  } catch (e) {
    rankList.value = []
  }
}

function onTabChange(e) {
  currentTab.value = e.index
  loadData()
}

onMounted(loadData)
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 120rpx;
}
.my-rank-card {
  margin: 24rpx;
  padding: 24rpx;
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 16rpx;
}
.label { color: #a0a0b8; font-size: 24rpx; }
.my-rank-info {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 16rpx;
}
.rank-num { font-size: 36rpx; font-weight: 700; color: #f59e0b; min-width: 60rpx; }
.nickname { flex: 1; color: #fff; }
.score { color: #7c3aed; font-weight: 600; }
.rank-list { padding: 0 24rpx; }
.rank-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #2a2a40;
}
.rank {
  width: 48rpx;
  text-align: center;
  color: #6b6b80;
  font-weight: 600;
  &.top { color: #f59e0b; }
}
.name { flex: 1; color: #e8e8f0; }
</style>
