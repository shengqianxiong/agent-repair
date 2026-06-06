<template>
  <view class="page">
    <view v-if="list.length" class="redeem-list">
      <view v-for="item in list" :key="item.id" class="redeem-card">
        <u-image :src="item.image" width="120rpx" height="120rpx" radius="8" />
        <view class="info">
          <text class="name">{{ item.name }}</text>
          <text class="points">{{ item.points }} 积分</text>
        </view>
        <u-button size="mini" type="primary" text="兑换" @click="openRedeem(item)" />
      </view>
      <u-loadmore :status="loadStatus" />
    </view>
    <u-empty v-else mode="list" text="暂无可兑换商品" />

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
import { getRedeemList, redeemPoints } from '@/api/points'

const list = ref([])
const loadStatus = ref('loadmore')
const showModal = ref(false)
const selectedItem = ref(null)
const modalContent = ref('')
const redeeming = ref(false)

onShow(() => loadList())
onPullDownRefresh(async () => {
  await loadList()
  uni.stopPullDownRefresh()
})

async function loadList() {
  loadStatus.value = 'loading'
  try {
    const data = await getRedeemList({ page: 1, pageSize: 50 })
    list.value = data?.list || []
  } catch {
    list.value = []
  } finally {
    loadStatus.value = 'nomore'
  }
}

function openRedeem(item) {
  selectedItem.value = item
  modalContent.value = `确认使用 ${item.points} 积分兑换「${item.name}」？`
  showModal.value = true
}

async function doRedeem() {
  if (redeeming.value || !selectedItem.value) return
  redeeming.value = true
  try {
    await redeemPoints({ itemId: selectedItem.value.id })
    uni.showToast({ title: '兑换成功', icon: 'success' })
    showModal.value = false
  } catch (e) {
    uni.showToast({ title: e.msg || '兑换失败', icon: 'none' })
  } finally {
    redeeming.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 16rpx;
}
.redeem-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
}
.info { flex: 1; }
.name {
  font-size: 28rpx;
  color: #fff;
  display: block;
}
.points {
  font-size: 26rpx;
  color: #f59e0b;
  margin-top: 8rpx;
}
</style>
