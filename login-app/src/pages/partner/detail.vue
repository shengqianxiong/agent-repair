<template>
  <view class="page" v-if="partner">
    <scroll-view scroll-y class="page-body">
    <view class="header">
      <u-avatar :src="partner.avatar || ''" size="80"></u-avatar>
      <text class="nickname">{{ partner.nickname }}</text>
      <view class="tags">
        <u-tag v-for="tag in partner.tags || []" :key="tag" :text="tag" type="primary" size="mini" plain></u-tag>
      </view>
      <text class="status">{{ partner.onlineStatus || '离线' }}</text>
    </view>
    <view class="actions">
      <u-button type="primary" text="发起邀约" :loading="inviting" @click="invite"></u-button>
      <u-button type="info" text="开始聊天" @click="goChat"></u-button>
    </view>
    </scroll-view>
  </view>
  <u-loading-page v-else loading></u-loading-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPartnerDetail, invitePartner } from '@/api/index.js'

const partner = ref(null)
const partnerId = ref('')
const inviting = ref(false)

async function loadDetail(id) {
  try {
    partner.value = await getPartnerDetail(id)
  } catch (e) {
    uni.navigateBack()
  }
}

async function invite() {
  inviting.value = true
  try {
    await invitePartner(partnerId.value)
    uni.showToast({ title: '邀约已发送', icon: 'success' })
  } catch (e) {
  } finally {
    inviting.value = false
  }
}

function goChat() {
  uni.navigateTo({ url: `/pages/chat/index?partnerId=${partnerId.value}` })
}

onMounted(() => {
  const pages = getCurrentPages()
  const opts = pages[pages.length - 1].options || {}
  partnerId.value = opts.id || ''
  if (partnerId.value) loadDetail(partnerId.value)
})
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.page-body { padding: 48rpx 32rpx; }
.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}
.nickname { font-size: 36rpx; font-weight: 700; color: #fff; }
.tags { display: flex; gap: 8rpx; flex-wrap: wrap; justify-content: center; }
.status { color: #6b6b80; font-size: 24rpx; }
.actions {
  margin-top: 64rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
</style>
