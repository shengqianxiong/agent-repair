<template>
  <view class="bar-page" v-if="partner">
    <view class="bar-card profile-card">
      <u-avatar :src="partner.avatar" size="80" />
      <text class="nickname">{{ partner.nickname }}</text>
      <view class="tags">
        <u-tag v-for="tag in partner.tags" :key="tag" :text="tag" size="mini" />
      </view>
      <text class="distance">距离 {{ partner.distance }}km</text>
    </view>

    <view class="bottom-bar safe-bottom">
      <u-button type="primary" text="发起邀约" :loading="inviting" @click="invite" />
      <u-button type="warning" text="开始聊天" @click="goChat" />
    </view>
  </view>
  <u-loading-page v-else loading />
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPartnerDetail, invitePartner } from '@/api/social'

const partner = ref(null)
const partnerId = ref('')
const inviting = ref(false)

async function loadDetail() {
  partner.value = await getPartnerDetail(partnerId.value)
}

async function invite() {
  inviting.value = true
  try {
    await invitePartner(partnerId.value)
    uni.showToast({ title: '邀约已发送', icon: 'success' })
  } finally {
    inviting.value = false
  }
}

function goChat() {
  uni.navigateTo({
    url: `/pages/chat/index?partnerId=${partnerId.value}&name=${partner.value.nickname}`
  })
}

onLoad((opts) => {
  partnerId.value = opts.id
  loadDetail()
})
</script>

<style lang="scss" scoped>
.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 24rpx;
  gap: 16rpx;
}

.nickname {
  font-size: 36rpx;
  font-weight: bold;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  justify-content: center;
}

.distance {
  color: #999;
  font-size: 24rpx;
}

.bottom-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 40rpx;
  display: flex;
  gap: 20rpx;
}
</style>
