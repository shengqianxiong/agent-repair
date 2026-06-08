<template>
  <view class="page page-dark">
    <scroll-view scroll-y class="page-scroll page-pad" @scrolltolower="loadMore">
    <view v-if="packages.length" class="package-list">
      <view v-for="item in packages" :key="item.id" class="package-card" @click="goDetail(item.id)">
        <u-image :src="item.image || defaultImg" width="100%" height="280rpx" radius="12"></u-image>
        <view class="card-body">
          <text class="name">{{ item.name }}</text>
          <text class="desc">{{ item.description || '精选酒水组合' }}</text>
          <view class="price-row">
            <text class="price">¥{{ formatPrice(item.price) }}</text>
            <text v-if="item.originalPrice" class="original">¥{{ formatPrice(item.originalPrice) }}</text>
          </view>
        </view>
      </view>
      <u-loadmore :status="loadStatus"></u-loadmore>
    </view>
    <u-empty v-else mode="list" text="暂无套餐"></u-empty>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getPackageList } from '@/api/index.js'
import { formatPrice } from '@/utils/common.js'

const defaultImg = 'https://cdn.uviewui.com/uview/album/2.jpg'
const packages = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')

async function loadData(reset = false) {
  if (reset) {
    page.value = 1
    packages.value = []
  }
  loadStatus.value = 'loading'
  try {
    const data = await getPackageList({ page: page.value, pageSize: 10 })
    const items = data?.list || data || []
    packages.value = reset ? items : [...packages.value, ...items]
    loadStatus.value = items.length < 10 ? 'nomore' : 'loadmore'
  } catch (e) {
    loadStatus.value = 'loadmore'
  }
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}&type=package` })
}

onMounted(() => loadData(true))
onPullDownRefresh(async () => {
  await loadData(true)
  uni.stopPullDownRefresh()
})
function loadMore() {
  if (loadStatus.value === 'loadmore') {
    page.value++
    loadData()
  }
}
</script>

<style lang="scss" scoped>
.page-pad { padding: 24rpx; }
.package-card {
  background: #1a1a2e;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 24rpx;
}
.card-body { padding: 24rpx; }
.name { font-size: 30rpx; font-weight: 600; color: #fff; display: block; }
.desc { font-size: 24rpx; color: #6b6b80; margin: 8rpx 0; display: block; }
.price-row { display: flex; align-items: baseline; gap: 12rpx; }
.price { color: #f59e0b; font-size: 36rpx; font-weight: 700; }
.original { color: #6b6b80; text-decoration: line-through; font-size: 24rpx; }
</style>
