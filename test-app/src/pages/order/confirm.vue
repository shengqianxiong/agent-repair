<template>
  <view class="page page-dark">
    <scroll-view scroll-y class="page-scroll">
    <u-cell-group title="选择桌位">
      <u-cell title="桌号" :value="selectedTable || '请选择'" isLink @click="showTablePicker = true"></u-cell>
    </u-cell-group>

    <view class="order-items">
      <view class="section-title">订单明细</view>
      <view v-for="item in items" :key="item.id" class="order-item">
        <text>{{ item.productName || item.name }} x{{ item.quantity }}</text>
        <text>¥{{ formatPrice(item.subtotal || item.price * item.quantity) }}</text>
      </view>
    </view>

    <view class="remark">
      <text class="label">备注</text>
      <u-textarea v-model="remark" placeholder="口味偏好、特殊要求等" count maxlength="200"></u-textarea>
    </view>
    </scroll-view>

    <view class="footer page-footer">
      <text class="total">合计 ¥{{ formatPrice(totalAmount) }}</text>
      <u-button type="primary" text="提交订单" :loading="submitting" @click="submit"></u-button>
    </view>

    <u-picker
      :show="showTablePicker"
      :columns="[tableOptions]"
      keyName="label"
      @confirm="onTableConfirm"
      @cancel="showTablePicker = false"
    ></u-picker>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCartList, getTableList, submitOrder } from '@/api/index.js'
import { formatPrice } from '@/utils/common.js'

const items = ref([])
const tables = ref([])
const selectedTable = ref('')
const selectedTableId = ref(null)
const remark = ref('')
const submitting = ref(false)
const showTablePicker = ref(false)

const tableOptions = computed(() =>
  tables.value.map((t) => ({ label: `${t.tableNo} (${t.area || ''})`, value: t.id }))
)

const totalAmount = computed(() =>
  items.value.reduce((s, i) => s + (i.subtotal || (i.unitPrice || i.price) * i.quantity), 0)
)

async function loadData() {
  try {
    const [cart, tableList] = await Promise.all([getCartList(), getTableList()])
    items.value = cart?.items || cart?.list || cart || []
    tables.value = tableList?.list || tableList || []
  } catch (e) {}
}

function onTableConfirm(e) {
  const item = e.value?.[0]
  if (item) {
    selectedTable.value = item.label
    selectedTableId.value = item.value
  }
  showTablePicker.value = false
}

async function submit() {
  if (!selectedTableId.value) {
    uni.showToast({ title: '请选择桌号', icon: 'none' })
    return
  }
  if (!items.value.length) {
    uni.showToast({ title: '购物车为空', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const order = await submitOrder({
      tableId: selectedTableId.value,
      remark: remark.value,
      items: items.value.map((i) => ({
        productId: i.productId,
        quantity: i.quantity
      }))
    })
    uni.redirectTo({
      url: `/pages/payment/index?orderId=${order.id || order.orderId}&amount=${order.payAmount || totalAmount.value}`
    })
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.section-title { padding: 24rpx 32rpx 8rpx; color: #a0a0b8; }
.order-items { padding: 0 32rpx; }
.order-item {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #2a2a40;
  color: #e8e8f0;
}
.remark { padding: 24rpx 32rpx; }
.label { color: #a0a0b8; margin-bottom: 12rpx; display: block; }
.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  background: #1a1a2e;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}
.total { font-size: 36rpx; color: #f59e0b; font-weight: 700; }
</style>
