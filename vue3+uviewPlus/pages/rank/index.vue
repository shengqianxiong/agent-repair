<template>
  <view class="bar-page">
    <u-navbar title="排行榜" :autoBack="false" bgColor="#1a1a2e" titleColor="#fff" />

    <u-tabs :list="tabs" :current="currentTab" @change="onTabChange" lineColor="#6c5ce7" />

    <view class="my-rank bar-card" v-if="myRank">
      <text class="label">我的排名</text>
      <view class="my-rank-content">
        <text class="rank-no">第 {{ myRank.rank || '-' }} 名</text>
        <text class="score">{{ myRank.score || 0 }} 分</text>
      </view>
    </view>

    <view v-if="!rankList.length && !loading" class="empty-wrap">
      <u-empty mode="list" text="暂无排名数据" />
    </view>

    <view v-for="(item, idx) in rankList" :key="item.userId || idx" class="bar-card rank-item">
      <view class="rank-left">
        <text class="rank-index" :class="{ top: idx < 3 }">{{ idx + 1 }}</text>
        <u-avatar :src="item.avatar" size="40" />
        <text class="nickname">{{ item.nickname || '匿名用户' }}</text>
      </view>
      <text class="score">{{ item.score || 0 }}</text>
    </view>

    <BarTabbar current="rank" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import BarTabbar from '@/components/BarTabbar.vue'
import { getRankList, getMyRank } from '@/api/social'

const tabs = [
  { name: '消费榜' },
  { name: '积分榜' },
  { name: '游戏榜' }
]
const rankTypes = ['消费', '积分', '游戏']
const currentTab = ref(0)
const rankList = ref([])
const myRank = ref(null)
const loading = ref(false)

async function loadData() {
  loading.value = true
  const rankType = rankTypes[currentTab.value]
  try {
    const [list, mine] = await Promise.allSettled([
      getRankList({ rankType }),
      getMyRank({ rankType })
    ])
    if (list.status === 'fulfilled') {
      rankList.value = list.value?.list || list.value || []
    }
    if (mine.status === 'fulfilled') {
      myRank.value = mine.value
    }
  } finally {
    loading.value = false
  }
}

function onTabChange(e) {
  currentTab.value = e.index
  loadData()
}

onShow(() => loadData())
onPullDownRefresh(async () => { await loadData(); uni.stopPullDownRefresh() })
</script>

<style lang="scss" scoped>
.my-rank {
  .label {
    font-size: 24rpx;
    color: #999;
  }

  .my-rank-content {
    display: flex;
    justify-content: space-between;
    margin-top: 12rpx;
  }

  .rank-no {
    font-size: 32rpx;
    font-weight: bold;
    color: #6c5ce7;
  }
}

.rank-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
}

.rank-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.rank-index {
  width: 40rpx;
  text-align: center;
  font-weight: bold;
  color: #999;

  &.top {
    color: #fdcb6e;
    font-size: 32rpx;
  }
}

.nickname {
  font-size: 28rpx;
}

.score {
  font-size: 28rpx;
  font-weight: bold;
  color: #6c5ce7;
}

.empty-wrap {
  padding-top: 80rpx;
}
</style>
