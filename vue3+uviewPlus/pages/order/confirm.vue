<template>
  <view class="bar-page">
    <view class="bar-card">
      <view class="section-title">选择桌号</view>
      <u-cell
        title="桌号"
        :value="selectedTable?.tableNo || '请选择'"
        isLink
        @click="showTablePicker = true"
      />
    </view>

    <view class="bar-card">
      <view class="section-title">订单商品</view>
      <view v-for="item in cartItems" :key="item.id" class="order-item">
        <text>{{ item.productName || item.name }} x{{ item.quantity }}</text>
        <text>¥{{ formatPrice(item.subtotal || item.price * item.quantity) }}</text>
      </view>
      <u-cell title="合计" :value="'¥' + formatPrice(totalAmount)" />
    </view>

    <view class="bar-card">
      <view class="section-title">备注</view>
      <u-textarea v-model="remark" placeholder="口味、冰量等要求" count maxlength="200" />
    </view>

    <view class="bottom-bar safe-bottom">
      <view class="total">
        实付 <text class="price-text">¥{{ formatPrice(totalAmount) }}</text>
      </view>
      <u-button type="primary" text="提交订单" :loading="submitting" @click="submit" />
    </view>

    <u-action-sheet
      :show="showTablePicker"
      :actions="tableActions"
      title="选择桌号"
      @close="showTablePicker = false"
      @select="onSelectTable"
    />
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getCartList, getTableList, submitOrder } from '@/api/order'
import { formatPrice } from '@/utils/request'

const cartItems = ref([])
const tables = ref([])
const selectedTable = ref(null)
const showTablePicker = ref(false)
const remark = ref('')
const submitting = ref(false)

const totalAmount = computed(() =>
  cartItems.value.reduce((s, i) => s + Number(i.subtotal || (i.unitPrice || i.price) * i.quantity || 0), 0)
)

const tableActions = computed(() =>
  tables.value.map((t) => ({
    name: `${t.tableNo} (${t.area || ''} ${t.capacity}人)`,
    table: t
  }))
)

async function loadData() {
  const [cart, tableList] = await Promise.all([getCartList(), getTableList()])
  cartItems.value = Array.isArray(cart) ? cart : (cart?.list || [])
  tables.value = Array.isArray(tableList) ? tableList : (tableList?.list || [])
}

function onSelectTable(e) {
  selectedTable.value = e.table
  showTablePicker.value = false
}

async function submit() {
  if (!selectedTable.value) {
    uni.showToast({ title: '请选择桌号', icon: 'none' })
    return
  }
  if (!cartItems.value.length) {
    uni.showToast({ title: '购物车为空', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const order = await submitOrder({
      tableId: selectedTable.value.id,
      remark: remark.value
    })
    uni.redirectTo({
      url: `/pages/payment/index?orderId=${order.id || order.orderId}&amount=${order.payAmount || order.totalAmount || totalAmount.value}`
    })
  } finally {
    submitting.value = false
  }
}

onShow(() => loadData())
</script>

<style lang="scss" scoped>
.section-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.order-item {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
  font-size: 26rpx;
  color: #666;
  border-bottom: 1rpx solid #f0f0f0;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  padding: 20rpx 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.total {
  font-size: 28rpx;
}
</style>
