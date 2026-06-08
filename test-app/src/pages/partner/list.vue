<template>
  <view class="page page-fixed">
    <scroll-view scroll-y class="page-body list-scroll" :show-scrollbar="false">
    <view v-if="partners.length" class="partner-list">
      <view v-for="item in partners" :key="item.id" class="partner-card" @click="goDetail(item.id)">
        <u-avatar :src="item.avatar || ''" size="56"></u-avatar>
        <view class="info">
          <text class="nickname">{{ item.nickname }}</text>
          <view class="tags">
            <u-tag v-for="tag in (item.tags || []).slice(0, 3)" :key="tag" :text="tag" size="mini" plain></u-tag>
          </view>
          <text class="distance">{{ item.distance ? `${item.distance}km` : '' }} · {{ item.onlineStatus || '离线' }}</text>
        </view>
        <u-button type="primary" size="mini" text="聊天" @click.stop="goChat(item.id)"></u-button>
      </view>
    </view>
    <u-empty v-else mode="list" text="附近暂无酒友"></u-empty>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNearbyPartners } from '@/api/index.js'

const partners = ref([])

async function loadData() {
  try {
    const location = await getLocation()
    const data = await getNearbyPartners({
      latitude: location.latitude,
      longitude: location.longitude
    })
    partners.value = data?.list || data || []
  } catch (e) {
    partners.value = []
  }
}

function getLocation() {
  return new Promise((resolve, reject) => {
    uni.getLocation({
      type: 'gcj02',
      success: resolve,
      fail: () => {
        uni.showModal({
          title: '提示',
          content: '需要位置权限才能发现附近酒友',
          confirmText: '去授权',
          success: (res) => {
            if (res.confirm) {
              uni.openSetting({})
            }
          }
        })
        reject(new Error('no location'))
      }
    })
  })
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/partner/detail?id=${id}` })
}

function goChat(id) {
  uni.navigateTo({ url: `/pages/chat/index?partnerId=${id}` })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.list-scroll { padding: 24rpx; }
.partner-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}
.info { flex: 1; }
.nickname { font-size: 28rpx; font-weight: 600; color: #fff; display: block; }
.tags { display: flex; gap: 8rpx; margin: 8rpx 0; flex-wrap: wrap; }
.distance { font-size: 22rpx; color: #6b6b80; }
</style>
