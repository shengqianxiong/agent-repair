<template>
  <view class="page">
    <u-navbar title="排行榜" :autoBack="false" bgColor="#1a1a2e" leftIcon="" />

    <u-tabs :list="tabList" :current="currentTab" @change="onTabChange" />

    <!-- 我的排名 -->
    <view class="my-rank-card" v-if="myRank">
      <text class="label">我的排名</text>
      <view class="my-rank-info">
        <text class="rank-num">第 {{ myRank.rank || '-' }} 名</text>
        <text class="score">{{ myRank.score || 0 }} 分</text>
      </view>
    </view>

    <!-- 排名列表 -->
    <view v-if="list.length" class="rank-list">
      <view v-for="(item, idx) in list" :key="item.id || idx" class="rank-item">
        <text class="rank" :class="{ top: idx < 3 }">{{ idx + 1 }}</text>
        <u-avatar :src="item.avatar" size="40" />
        <text class="nickname">{{ item.nickname || '匿名' }}</text>
        <text class="score">{{ item.score }}</text>
      </view>
    </view>
    <u-empty v-else mode="list" text="暂无排名数据" />

    <AppTabbar active="rank" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getRankList, getMyRank } from '@/api/rank'

const tabList = [
  { name: '消费榜' },
  { name: '积分榜' },
  { name: '游戏榜' }
]
const rankTypes = ['消费', '积分', '游戏']
const currentTab = ref(0)
const list = ref([])
const myRank = ref(null)

onShow(() => loadData())
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})

function onTabChange(index) {
  currentTab.value = typeof index === 'object' ? index.index : index
  loadData()
}

async function loadData() {
  const rankType = rankTypes[currentTab.value]
  await Promise.allSettled([loadList(rankType), loadMy(rankType)])
}

async function loadList(rankType) {
  try {
    const data = await getRankList({ rankType, period: '月' })
    list.value = data?.list || data || []
  } catch {
    list.value = []
  }
}

async function loadMy(rankType) {
  try {
    myRank.value = await getMyRank({ rankType, period: '月' })
  } catch {
    myRank.value = null
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 120rpx;
}
.my-rank-card {
  margin: 16rpx;
  background: linear-gradient(135deg, #2d1b69, #1a1a2e);
  border-radius: 12rpx;
  padding: 24rpx;
}
.label {
  font-size: 24rpx;
  color: #a0a0b8;
}
.my-rank-info {
  display: flex;
  justify-content: space-between;
  margin-top: 12rpx;
}
.rank-num {
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
}
.score {
  font-size: 32rpx;
  color: #f59e0b;
}
.rank-list {
  padding: 16rpx;
}
.rank-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 12rpx;
}
.rank {
  width: 48rpx;
  text-align: center;
  font-size: 28rpx;
  font-weight: 600;
  color: #6b6b80;
  &.top { color: #f59e0b; }
}
.nickname {
  flex: 1;
  font-size: 28rpx;
  color: #fff;
}
</style>
