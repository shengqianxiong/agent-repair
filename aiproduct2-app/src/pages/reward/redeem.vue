<template>
  <view class="redeem-page page-container">
    <view class="summary-card card-box">
      <view class="summary-item">
        <view class="summary-label">我的环保奖励</view>
        <view class="summary-value">¥{{ userAsset.balance }}</view>
      </view>
      <view class="summary-item">
        <view class="summary-label">可用积分</view>
        <view class="summary-value">{{ userAsset.points }} 积分</view>
      </view>
    </view>

    <view class="category-tabs">
      <view
        v-for="item in categories"
        :key="item.value"
        class="tab"
        :class="{ active: activeCategory === item.value }"
        @click="activeCategory = item.value"
      >
        {{ item.label }}
      </view>
    </view>

    <view class="gift-grid">
      <view v-for="item in filteredGifts" :key="item.id" class="gift-card card-box">
        <view class="gift-image">{{ item.emoji }}</view>
        <view class="gift-name">{{ item.name }}</view>
        <view class="gift-stock">仅剩 {{ item.stock }} 份</view>
        <view class="gift-price">{{ item.points }} 积分</view>
        <u-button
          size="mini"
          type="primary"
          color="#2EAE5E"
          text="立即兑换"
          @click="handleRedeem(item)"
        />
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { fetchGiftList, redeemGift } from '@/api'

const userAsset = ref({
  balance: '28.60',
  points: 286
})

const categories = [
  { label: '全部', value: 'ALL' },
  { label: '生活用品', value: 'LIFE' },
  { label: '数码家电', value: 'DIGITAL' },
  { label: '公益捐赠', value: 'WELFARE' }
]

const activeCategory = ref('ALL')
const giftList = ref([
  { id: 1, name: '纸巾（1000抽）', points: 80, stock: 293, emoji: '📦', category: 'LIFE' },
  { id: 2, name: '手巾皂（2kg）', points: 300, stock: 156, emoji: '🧴', category: 'LIFE' },
  { id: 3, name: '环保帆布袋', points: 120, stock: 80, emoji: '👜', category: 'WELFARE' },
  { id: 4, name: '蓝牙耳机券', points: 500, stock: 32, emoji: '🎧', category: 'DIGITAL' }
])

const filteredGifts = computed(() => {
  if (activeCategory.value === 'ALL') return giftList.value
  return giftList.value.filter((item) => item.category === activeCategory.value)
})

async function loadGifts() {
  try {
    const data = await fetchGiftList({ category: activeCategory.value })
    if (Array.isArray(data?.records)) {
      giftList.value = data.records
    }
  } catch (error) {
    // mock 数据兜底
  }
}

async function handleRedeem(item) {
  try {
    await redeemGift({ giftId: item.id })
    uni.showToast({ title: '兑换成功', icon: 'success' })
  } catch (error) {
    uni.showToast({ title: '兑换申请已受理', icon: 'none' })
  }
}

watch(activeCategory, () => {
  loadGifts()
})

onMounted(() => {
  loadGifts()
})
</script>

<style lang="scss" scoped>
.summary-card {
  padding: 24rpx;
  display: flex;
  justify-content: space-between;
  background: #ecf7ea;
}

.summary-item {
  width: 48%;
}

.summary-label {
  color: #7b838d;
  font-size: 22rpx;
}

.summary-value {
  margin-top: 8rpx;
  font-size: 38rpx;
  font-weight: 700;
  color: #24303d;
}

.category-tabs {
  margin-top: 18rpx;
  display: flex;
  gap: 12rpx;
}

.tab {
  padding: 12rpx 20rpx;
  border-radius: 999rpx;
  background: #f2f5f8;
  color: #6e7680;
  font-size: 22rpx;
}

.tab.active {
  color: #1f8c48;
  background: #def4e6;
}

.gift-grid {
  margin-top: 18rpx;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}

.gift-card {
  padding: 16rpx;
}

.gift-image {
  height: 180rpx;
  border-radius: 14rpx;
  background: #f4f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 72rpx;
}

.gift-name {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #29303a;
}

.gift-stock {
  margin-top: 6rpx;
  color: #9ba1aa;
  font-size: 20rpx;
}

.gift-price {
  margin: 10rpx 0;
  color: #ff8a00;
  font-size: 26rpx;
  font-weight: 700;
}
</style>
