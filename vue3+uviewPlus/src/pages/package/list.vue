<template>
  <view class="page">
    <u-dropdown>
      <u-dropdown-item v-model="filterStatus" title="状态" :options="statusOptions" @change="refresh" />
      <u-dropdown-item v-model="sortBy" title="排序" :options="sortOptions" @change="refresh" />
    </u-dropdown>

    <view v-if="list.length" class="package-list">
      <view v-for="item in list" :key="item.id" class="package-card" @click="goDetail(item.id)">
        <u-image :src="item.image" width="200rpx" height="200rpx" radius="8" />
        <view class="info">
          <text class="name">{{ item.name }}</text>
          <text class="desc">{{ item.description }}</text>
          <view class="price-row">
            <text class="price">¥{{ formatPrice(item.price) }}</text>
            <u-tag v-if="item.originalPrice" :text="`省¥${formatPrice(item.originalPrice - item.price)}`" size="mini" type="error" />
          </view>
        </view>
      </view>
      <u-loadmore :status="loadStatus" />
    </view>
    <u-empty v-else mode="list" text="暂无套餐" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getPackageList } from '@/api/package'
import { formatPrice } from '@/utils/navigate'

const list = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')
const filterStatus = ref(1)
const sortBy = ref('default')

const statusOptions = [
  { label: '上架中', value: 1 },
  { label: '全部', value: '' }
]
const sortOptions = [
  { label: '默认', value: 'default' },
  { label: '价格升序', value: 'price_asc' },
  { label: '价格降序', value: 'price_desc' }
]

onShow(() => refresh())
onPullDownRefresh(async () => {
  await refresh()
  uni.stopPullDownRefresh()
})

async function refresh() {
  page.value = 1
  list.value = []
  await fetchList()
}

async function fetchList() {
  loadStatus.value = 'loading'
  try {
    const data = await getPackageList({
      page: page.value,
      pageSize: 20,
      status: filterStatus.value,
      sortBy: sortBy.value
    })
    list.value = data?.list || []
    loadStatus.value = 'nomore'
  } catch {
    list.value = []
    loadStatus.value = 'nomore'
  }
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/product/detail?id=${id}&type=package` })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
}
.package-list {
  padding: 16rpx;
}
.package-card {
  display: flex;
  gap: 16rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 16rpx;
  margin-bottom: 16rpx;
}
.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.name {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
}
.desc {
  font-size: 24rpx;
  color: #6b6b80;
  margin: 8rpx 0;
}
.price-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.price {
  font-size: 32rpx;
  color: #f59e0b;
  font-weight: 600;
}
</style>
