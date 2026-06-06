<template>
  <view class="page">
    <view v-if="items.length" class="cart-list">
      <u-swipe-action>
        <u-swipe-action-item
          v-for="item in items"
          :key="item.id"
          :options="swipeOptions"
          @click="removeItem(item)"
        >
          <view class="cart-item">
            <u-image :src="item.image || defaultImg" width="120rpx" height="120rpx" radius="8"></u-image>
            <view class="item-info">
              <text class="item-name">{{ item.productName || item.name }}</text>
              <text class="item-price">¥{{ formatPrice(item.unitPrice || item.price) }}</text>
            </view>
            <u-number-box
              v-model="item.quantity"
              :min="1"
              @change="updateQty(item)"
            ></u-number-box>
          </view>
        </u-swipe-action-item>
      </u-swipe-action>
    </view>
    <u-empty v-else mode="car" text="购物车是空的"></u-empty>

    <view v-if="items.length" class="summary">
      <u-cell title="商品合计" :value="`¥${formatPrice(totalAmount)}`"></u-cell>
      <u-button type="primary" text="去结算" @click="goConfirm"></u-button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { getCartList, updateCart, removeFromCart } from '@/api/index.js'
import { formatPrice } from '@/utils/common.js'

const defaultImg = 'https://cdn.uviewui.com/uview/album/1.jpg'
const items = ref([])
const swipeOptions = [{ text: '删除', style: { backgroundColor: '#ef4444' } }]

const totalAmount = computed(() =>
  items.value.reduce((s, i) => s + (i.subtotal || (i.unitPrice || i.price) * i.quantity), 0)
)

async function loadCart() {
  try {
    const data = await getCartList()
    items.value = (data?.items || data?.list || data || []).map((i) => ({ ...i }))
  } catch (e) {
    items.value = []
  }
}

async function updateQty(item) {
  try {
    await updateCart({ id: item.id, quantity: item.quantity })
  } catch (e) {
    loadCart()
  }
}

async function removeItem(item) {
  try {
    await removeFromCart(item.id)
    uni.showToast({ title: '已删除', icon: 'success' })
    loadCart()
  } catch (e) {}
}

function goConfirm() {
  uni.navigateTo({ url: '/pages/order/confirm' })
}

onMounted(loadCart)
onShow(loadCart)
onPullDownRefresh(async () => {
  await loadCart()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; background: #0f0f1a; padding-bottom: 200rpx; }
.cart-list { padding: 16rpx; }
.cart-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx;
  background: #1a1a2e;
  margin-bottom: 2rpx;
}
.item-info { flex: 1; }
.item-name { font-size: 28rpx; color: #fff; display: block; }
.item-price { color: #f59e0b; margin-top: 8rpx; display: block; }
.summary {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #1a1a2e;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}
</style>
