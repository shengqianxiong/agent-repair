<template>
  <view class="bar-page">
    <view v-if="!items.length" class="empty-wrap">
      <u-empty mode="car" text="购物车是空的">
        <template #bottom>
          <u-button type="primary" text="去点餐" @click="goOrder" />
        </template>
      </u-empty>
    </view>

    <view v-else>
      <u-swipe-action>
        <u-swipe-action-item
          v-for="item in items"
          :key="item.id"
          :options="swipeOptions"
          @click="removeItem(item)"
        >
          <view class="cart-item">
            <u-image :src="item.image || item.productImage" width="120rpx" height="120rpx" radius="8" />
            <view class="item-info">
              <text class="item-name">{{ item.productName || item.name }}</text>
              <text class="price-text">¥{{ formatPrice(item.unitPrice || item.price) }}</text>
              <u-number-box
                v-model="item.quantity"
                :min="1"
                @change="(e) => updateQty(item, e.value)"
              />
            </view>
            <text class="subtotal">¥{{ formatPrice(item.subtotal || item.price * item.quantity) }}</text>
          </view>
        </u-swipe-action-item>
      </u-swipe-action>

      <view class="bar-card summary">
        <u-cell title="商品合计" :value="'¥' + formatPrice(totalAmount)" />
        <u-button type="primary" text="去结算" @click="goConfirm" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { getCartList, updateCart, removeFromCart } from '@/api/order'
import { formatPrice } from '@/utils/request'

const items = ref([])
const swipeOptions = [{ text: '删除', style: { backgroundColor: '#d63031' } }]

const totalAmount = computed(() =>
  items.value.reduce((s, i) => s + Number(i.subtotal || (i.unitPrice || i.price) * i.quantity || 0), 0)
)

async function loadCart() {
  const list = await getCartList()
  items.value = Array.isArray(list) ? list : (list?.list || [])
}

async function updateQty(item, qty) {
  await updateCart({ id: item.id, quantity: qty })
  loadCart()
}

async function removeItem(item) {
  await removeFromCart(item.id)
  uni.showToast({ title: '已删除', icon: 'success' })
  loadCart()
}

function goConfirm() {
  uni.navigateTo({ url: '/pages/order/confirm' })
}

function goOrder() {
  uni.reLaunch({ url: '/pages/order/index' })
}

onShow(() => loadCart())

onPullDownRefresh(async () => {
  await loadCart()
  uni.stopPullDownRefresh()
})
</script>

<style lang="scss" scoped>
.cart-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #fff;
  gap: 20rpx;
}

.item-info {
  flex: 1;

  .item-name {
    display: block;
    font-size: 28rpx;
    margin-bottom: 8rpx;
  }
}

.subtotal {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.summary {
  margin-top: 24rpx;
}

.empty-wrap {
  padding-top: 200rpx;
}
</style>
