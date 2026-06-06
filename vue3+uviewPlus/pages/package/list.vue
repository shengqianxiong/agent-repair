<template>
  <view class="bar-page">
    <view v-if="!packages.length && !loading" class="empty-wrap">
      <u-empty mode="list" text="暂无套餐" />
    </view>

    <view v-for="item in packages" :key="item.id" class="bar-card package-card" @click="goDetail(item)">
      <u-image :src="item.image" width="100%" height="280rpx" radius="8" />
      <view class="package-info">
        <text class="name">{{ item.name }}</text>
        <text class="desc u-line-2">{{ item.description }}</text>
        <view class="price-row">
          <text class="price-text">¥{{ formatPrice(item.price) }}</text>
          <text v-if="item.originalPrice" class="original">¥{{ formatPrice(item.originalPrice) }}</text>
        </view>
      </view>
    </view>

    <u-loadmore v-if="packages.length" :status="loadStatus" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getPackageList } from '@/api/order'
import { formatPrice } from '@/utils/request'

const packages = ref([])
const loading = ref(false)
const loadStatus = ref('loadmore')
const page = ref(1)
const total = ref(0)

async function loadList(reset = true) {
  if (reset) {
    page.value = 1
    packages.value = []
  }
  loading.value = true
  try {
    const res = await getPackageList({ page: page.value, pageSize: 10, status: 1 })
    const list = res?.list || []
    total.value = res?.total || 0
    packages.value = reset ? list : [...packages.value, ...list]
    loadStatus.value = packages.value.length >= total.value ? 'nomore' : 'loadmore'
  } finally {
    loading.value = false
  }
}

function goDetail(item) {
  uni.navigateTo({ url: `/pages/product/detail?id=${item.id}&type=package` })
}

onShow(() => loadList(true))
onPullDownRefresh(async () => { await loadList(true); uni.stopPullDownRefresh() })
onReachBottom(() => {
  if (loadStatus.value === 'loadmore') {
    page.value++
    loadList(false)
  }
})
</script>

<style lang="scss" scoped>
.package-card {
  padding: 0;
  overflow: hidden;
}

.package-info {
  padding: 20rpx;

  .name {
    font-size: 30rpx;
    font-weight: bold;
    display: block;
  }

  .desc {
    font-size: 24rpx;
    color: #999;
    margin: 8rpx 0;
  }
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}

.original {
  color: #999;
  font-size: 22rpx;
  text-decoration: line-through;
}

.empty-wrap {
  padding-top: 120rpx;
}
</style>
