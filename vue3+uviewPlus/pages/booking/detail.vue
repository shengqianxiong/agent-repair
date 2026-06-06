<template>
  <view class="bar-page" v-if="booking">
    <view class="bar-card">
      <view class="status-header">
        <u-tag :text="booking.status" :type="statusType(booking.status)" size="large" />
      </view>
      <u-cell-group>
        <u-cell title="预约日期" :value="booking.bookingDate" />
        <u-cell title="预约时段" :value="booking.timeSlot" />
        <u-cell title="用餐人数" :value="String(booking.guestCount)" />
        <u-cell title="席位类型" :value="booking.seatType" />
        <u-cell title="分配桌位" :value="booking.tableNo || '待分配'" />
        <u-cell title="预约时间" :value="formatDateTime(booking.createTime)" />
      </u-cell-group>
    </view>

    <view class="bottom-bar safe-bottom" v-if="booking.status === '待确认'">
      <u-button type="error" text="取消预约" @click="doCancel" />
    </view>
  </view>
  <u-loading-page v-else loading />
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getBookingDetail, cancelBooking } from '@/api/booking'
import { formatDateTime } from '@/utils/request'

const booking = ref(null)
const bookingId = ref('')

function statusType(status) {
  const map = { '待确认': 'warning', '已确认': 'primary', '已到店': 'success', '已取消': 'info' }
  return map[status] || 'info'
}

async function loadDetail() {
  booking.value = await getBookingDetail(bookingId.value)
}

async function doCancel() {
  await cancelBooking(bookingId.value)
  uni.showToast({ title: '已取消', icon: 'success' })
  loadDetail()
}

onLoad((opts) => {
  bookingId.value = opts.id
  loadDetail()
})
</script>

<style lang="scss" scoped>
.status-header {
  margin-bottom: 24rpx;
}

.bottom-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 40rpx;
}
</style>
