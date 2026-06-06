<template>
  <view class="page" v-if="booking">
    <view class="status-header">
      <u-tag :text="booking.status" :type="statusType" size="large" />
    </view>

    <u-cell-group>
      <u-cell title="预约日期" :value="booking.bookingDate" />
      <u-cell title="时段" :value="booking.timeSlot" />
      <u-cell title="人数" :value="`${booking.guestCount} 人`" />
      <u-cell title="席位类型" :value="booking.seatType" />
      <u-cell title="分配桌位" :value="booking.tableNo || '待分配'" />
      <u-cell title="预约时间" :value="formatDateTime(booking.createTime)" />
    </u-cell-group>

    <view class="actions" v-if="booking.status === '待确认'">
      <u-button type="error" text="取消预约" @click="doCancel" />
    </view>
  </view>
  <u-loading-page :loading="loading" />
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getBookingDetail, cancelBooking } from '@/api/booking'
import { formatDateTime } from '@/utils/navigate'

const booking = ref(null)
const loading = ref(true)
let bookingId = null

const statusType = computed(() => {
  const s = booking.value?.status
  if (s === '已确认' || s === '已到店') return 'success'
  if (s === '已取消') return 'error'
  return 'warning'
})

onLoad((options) => {
  bookingId = options.id
  loadDetail()
})

async function loadDetail() {
  loading.value = true
  try {
    booking.value = await getBookingDetail(bookingId)
  } catch (e) {
    uni.showToast({ title: e.msg || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function doCancel() {
  try {
    await cancelBooking(bookingId)
    uni.showToast({ title: '已取消', icon: 'success' })
    loadDetail()
  } catch (e) {
    uni.showToast({ title: e.msg || '取消失败', icon: 'none' })
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
  padding-bottom: 160rpx;
}
.status-header {
  text-align: center;
  padding: 32rpx;
  background: #1a1a2e;
  border-radius: 12rpx;
  margin-bottom: 24rpx;
}
.actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 24rpx;
  background: #1a1a2e;
}
</style>
