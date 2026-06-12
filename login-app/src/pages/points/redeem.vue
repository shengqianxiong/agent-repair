<template>
  <view class="page">
    <scroll-view scroll-y class="page-body">
    <view v-if="items.length" class="redeem-list">
      <view v-for="item in items" :key="item.id" class="redeem-card">
        <u-image :src="item.image || defaultImg" width="120rpx" height="120rpx" radius="8"></u-image>
        <view class="info">
          <text class="name">{{ item.name }}</text>
          <text class="points">{{ item.pointsCost || item.points }} 积分</text>
        </view>
        <u-button type="primary" size="mini" text="兑换" @click="confirmRedeem(item)"></u-button>
      </view>
    </view>
    <u-empty v-else mode="list" text="暂无可兑换商品"></u-empty>
    </scroll-view>

    <u-modal
      :show="showModal"
      title="确认兑换"
      :content="modalContent"
      showCancelButton
      @confirm="doRedeem"
      @cancel="showModal = false"
    ></u-modal>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getRedeemList, redeemPoints } from '@/api/index.js'

const defaultImg = 'https://cdn.uviewui.com/uview/album/3.jpg'
const items = ref([])
const showModal = ref(false)
const selectedItem = ref(null)
const modalContent = ref('')

async function loadData() {
  try {
    const data = await getRedeemList({ page: 1, pageSize: 50 })
    items.value = data?.list || data || []
  } catch (e) {
    items.value = []
  }
}

function confirmRedeem(item) {
  selectedItem.value = item
  modalContent.value = `确定使用 ${item.pointsCost || item.points} 积分兑换「${item.name}」？`
  showModal.value = true
}

async function doRedeem() {
  showModal.value = false
  try {
    await redeemPoints({ itemId: selectedItem.value.id })
    uni.showToast({ title: '兑换成功', icon: 'success' })
  } catch (e) {}
}

onMounted(loadData)
onPullDownRefresh(async () => {
  await loadData()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.redeem-list { padding: 24rpx; }
.redeem-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}
.info { flex: 1; }
.name { font-size: 28rpx; color: #fff; display: block; }
.points { color: #f59e0b; margin-top: 8rpx; display: block; }
</style>
