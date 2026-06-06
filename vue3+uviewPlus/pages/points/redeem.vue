<template>
  <view class="bar-page">
    <view v-if="!items.length && !loading" class="empty-wrap">
      <u-empty mode="list" text="暂无可兑换商品" />
    </view>

    <view v-for="item in items" :key="item.id" class="bar-card redeem-item">
      <u-image :src="item.image" width="120rpx" height="120rpx" radius="8" />
      <view class="info">
        <text class="name">{{ item.name }}</text>
        <text class="points">{{ item.pointsCost || item.points }} 积分</text>
      </view>
      <u-button type="primary" size="mini" text="兑换" @click="openRedeem(item)" />
    </view>

    <u-modal
      :show="showModal"
      title="确认兑换"
      :content="modalContent"
      showCancelButton
      @confirm="doRedeem"
      @cancel="showModal = false"
    />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getRedeemList, redeemPoints } from '@/api/marketing'

const items = ref([])
const loading = ref(false)
const showModal = ref(false)
const selectedItem = ref(null)
const modalContent = ref('')

async function loadList() {
  loading.value = true
  try {
    const res = await getRedeemList({ page: 1, pageSize: 20 })
    items.value = res?.list || res || []
  } finally {
    loading.value = false
  }
}

function openRedeem(item) {
  selectedItem.value = item
  modalContent.value = `确定使用 ${item.pointsCost || item.points} 积分兑换「${item.name}」？`
  showModal.value = true
}

async function doRedeem() {
  showModal.value = false
  await redeemPoints({ itemId: selectedItem.value.id })
  uni.showToast({ title: '兑换成功', icon: 'success' })
  loadList()
}

onShow(() => loadList())
onPullDownRefresh(async () => { await loadList(); uni.stopPullDownRefresh() })
</script>

<style lang="scss" scoped>
.redeem-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.info {
  flex: 1;

  .name {
    display: block;
    font-size: 28rpx;
    font-weight: bold;
  }

  .points {
    font-size: 24rpx;
    color: #6c5ce7;
    margin-top: 8rpx;
  }
}

.empty-wrap {
  padding-top: 120rpx;
}
</style>
