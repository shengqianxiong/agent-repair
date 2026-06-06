<template>
  <view class="page" v-if="partner">
    <view class="profile-card">
      <u-avatar :src="partner.avatar" size="80" />
      <text class="nickname">{{ partner.nickname }}</text>
      <u-tag :text="partner.onlineStatus || '离线'" size="mini" />
      <view class="tags">
        <u-tag v-for="tag in partner.tags || []" :key="tag" :text="tag" size="mini" plain />
      </view>
      <text class="distance">距离 {{ partner.distance }}km</text>
    </view>

    <view class="actions">
      <u-button type="primary" text="发起邀约" :loading="inviting" @click="doInvite" />
      <u-button text="开始聊天" @click="goChat" />
    </view>
  </view>
  <u-loading-page :loading="loading" />
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPartnerDetail, invitePartner } from '@/api/partner'

const partner = ref(null)
const loading = ref(true)
const inviting = ref(false)
let partnerId = null

onLoad((options) => {
  partnerId = options.id
  loadDetail()
})

async function loadDetail() {
  loading.value = true
  try {
    partner.value = await getPartnerDetail(partnerId)
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function doInvite() {
  inviting.value = true
  try {
    await invitePartner(partnerId)
    uni.showToast({ title: '邀约已发送', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: e.msg || '邀约失败', icon: 'none' })
  } finally {
    inviting.value = false
  }
}

function goChat() {
  uni.navigateTo({
    url: `/pages/chat/index?partnerId=${partnerId}&name=${partner.value?.nickname || ''}`
  })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
}
.profile-card {
  background: #1a1a2e;
  border-radius: 16rpx;
  padding: 48rpx;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}
.nickname {
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
}
.tags {
  display: flex;
  gap: 8rpx;
  flex-wrap: wrap;
  justify-content: center;
}
.distance {
  font-size: 26rpx;
  color: #6b6b80;
}
.actions {
  margin-top: 48rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}
</style>
