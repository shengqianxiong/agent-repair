<template>
  <view class="page">
    <view v-if="cartList.length">
      <u-swipe-action>
        <u-swipe-action-item
          v-for="item in cartList"
          :key="item.id"
          :options="swipeOptions"
          @click="onSwipe(item)"
        >
          <view class="cart-item">
            <u-image :src="item.image" width="120rpx" height="120rpx" radius="8" />
            <view class="item-info">
              <text class="name">{{ item.productName }}</text>
              <text class="price">¥{{ formatPrice(item.unitPrice) }}</text>
            </view>
            <u-number-box
              v-model="item.quantity"
              :min="1"
              @change="(e) => onQtyChange(item, e.value)"
            />
          </view>
        </u-swipe-action-item>
      </u-swipe-action>

      <u-cell title="合计" :value="`¥${formatPrice(total)}`" />
      <view class="bottom-bar">
        <u-button type="primary" text="去结算" @click="goConfirm" />
      </view>
    </view>
    <u-empty v-else mode="car" text="购物车是空的" />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getCartList, updateCart, removeCart } from '@/api/cart'
import { getLocalCart, updateLocalCartItem, removeLocalCartItem, getCartSummary } from '@/utils/cart'
import { formatPrice } from '@/utils/navigate'

const cartList = ref([])
const swipeOptions = [{ text: '删除', style: { backgroundColor: '#ef4444' } }]

const total = computed(() => getCartSummary(cartList.value).total)

onShow(() => loadCart())

async function loadCart() {
  try {
    const data = await getCartList()
    cartList.value = data?.list || data || []
  } catch {
    cartList.value = getLocalCart()
  }
}

async function onQtyChange(item, qty) {
  try {
    await updateCart({ id: item.id, quantity: qty })
  } catch {
    updateLocalCartItem(item.id, qty)
  }
}

async function onSwipe(item) {
  try {
    await removeCart(item.id)
  } catch {
    removeLocalCartItem(item.id)
  }
  cartList.value = cartList.value.filter((i) => i.id !== item.id)
}

function goConfirm() {
  uni.navigateTo({ url: '/pages/order/confirm' })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 140rpx;
}
.cart-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #1a1a2e;
}
.item-info {
  flex: 1;
}
.name {
  font-size: 28rpx;
  color: #fff;
  display: block;
}
.price {
  font-size: 28rpx;
  color: #f59e0b;
  margin-top: 8rpx;
}
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 24rpx;
  background: #1a1a2e;
}
</style>
