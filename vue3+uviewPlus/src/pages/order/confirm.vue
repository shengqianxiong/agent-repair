<template>
  <view class="page">
    <u-cell title="选择桌号" :value="selectedTable || '请选择'" isLink @click="showTablePicker = true" />

    <view class="section">
      <view class="section-title">订单明细</view>
      <view v-for="item in cartList" :key="item.id" class="order-item">
        <text>{{ item.productName }} x{{ item.quantity }}</text>
        <text>¥{{ formatPrice(item.unitPrice * item.quantity) }}</text>
      </view>
    </view>

    <view class="remark">
      <text class="label">备注</text>
      <u-textarea v-model="remark" placeholder="口味偏好、特殊要求等" count maxlength="200" />
    </view>

    <u-cell title="应付金额" :value="`¥${formatPrice(total)}`" />

    <view class="bottom-bar">
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
import { getCartList } from '@/api/cart'
import { getTableList, submitOrder } from '@/api/order'
import { getLocalCart, getCartSummary } from '@/utils/cart'
import { formatPrice } from '@/utils/navigate'

const cartList = ref([])
const tables = ref([])
const selectedTable = ref('')
const selectedTableId = ref(null)
const remark = ref('')
const submitting = ref(false)
const showTablePicker = ref(false)

const total = computed(() => getCartSummary(cartList.value).total)
const tableActions = computed(() =>
  tables.value.map((t) => ({ name: `${t.tableNo} (${t.area})`, id: t.id }))
)

onShow(async () => {
  await loadCart()
  await loadTables()
})

async function loadCart() {
  try {
    const data = await getCartList()
    cartList.value = data?.list || data || []
  } catch {
    cartList.value = getLocalCart()
  }
}

async function loadTables() {
  try {
    const data = await getTableList()
    tables.value = data?.list || data || []
  } catch {
    tables.value = [
      { id: 1, tableNo: 'A01', area: '吧台区' },
      { id: 2, tableNo: 'B02', area: '卡座区' }
    ]
  }
}

function onSelectTable(e) {
  selectedTable.value = e.name
  selectedTableId.value = e.id
  showTablePicker.value = false
}

async function submit() {
  if (!selectedTableId.value) {
    uni.showToast({ title: '请选择桌号', icon: 'none' })
    return
  }
  if (!cartList.value.length) {
    uni.showToast({ title: '购物车为空', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const data = await submitOrder({
      tableId: selectedTableId.value,
      remark: remark.value,
      items: cartList.value.map((i) => ({
        productId: i.productId,
        quantity: i.quantity
      }))
    })
    const orderId = data?.id || data?.orderId
    uni.redirectTo({ url: `/pages/payment/index?orderId=${orderId}&amount=${total.value}` })
  } catch (e) {
    uni.showToast({ title: e.msg || '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding-bottom: 140rpx;
}
.section {
  margin: 16rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #fff;
  margin-bottom: 16rpx;
}
.order-item {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
  color: #a0a0b8;
  font-size: 26rpx;
}
.remark {
  margin: 16rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  padding: 20rpx;
}
.label {
  color: #fff;
  font-size: 28rpx;
  display: block;
  margin-bottom: 12rpx;
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
