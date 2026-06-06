<template>
  <view class="bar-page">
    <view v-if="!partners.length && !loading" class="empty-wrap">
      <u-empty mode="list" text="附近暂无酒友" />
    </view>

    <view v-for="item in partners" :key="item.id" class="bar-card partner-card">
      <u-avatar :src="item.avatar" size="50" />
      <view class="info">
        <view class="name-row">
          <text class="nickname">{{ item.nickname }}</text>
          <u-tag :text="item.onlineStatus || '离线'" size="mini" :type="item.onlineStatus === '在线' ? 'success' : 'info'" />
        </view>
        <view class="tags">
          <u-tag v-for="tag in (item.tags || []).slice(0, 3)" :key="tag" :text="tag" size="mini" plain />
        </view>
        <text class="distance">{{ item.distance }}km</text>
      </view>
      <view class="actions">
        <u-button type="primary" size="mini" text="聊天" @click="goChat(item)" />
        <u-button type="warning" size="mini" text="详情" @click="goDetail(item.id)" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getPartnerNearby } from '@/api/social'

const partners = ref([])
const loading = ref(false)

function getLocation() {
  return new Promise((resolve) => {
    uni.getLocation({
      type: 'gcj02',
      success: (res) => resolve({ latitude: res.latitude, longitude: res.longitude }),
      fail: () => resolve({})
    })
  })
}

async function loadPartners() {
  loading.value = true
  try {
    const loc = await getLocation()
    const res = await getPartnerNearby(loc)
    partners.value = res?.list || res || []
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages/partner/detail?id=${id}` })
}

function goChat(item) {
  uni.navigateTo({ url: `/pages/chat/index?partnerId=${item.id}&name=${item.nickname}` })
}

onShow(() => loadPartners())
onPullDownRefresh(async () => { await loadPartners(); uni.stopPullDownRefresh() })
</script>

<style lang="scss" scoped>
.partner-card {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
}

.info {
  flex: 1;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.nickname {
  font-size: 28rpx;
  font-weight: bold;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin: 8rpx 0;
}

.distance {
  font-size: 22rpx;
  color: #999;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.empty-wrap {
  padding-top: 120rpx;
}
</style>
