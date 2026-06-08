<template>
  <view class="page page-fixed" v-if="booking">
    <scroll-view scroll-y class="page-body" :show-scrollbar="false">
    <view class="status-header">
      <u-tag :text="statusInfo.text" :type="statusInfo.type" size="large"></u-tag>
    </view>
    <u-cell-group>
      <u-cell title="预约日期" :value="booking.bookingDate"></u-cell>
      <u-cell title="预约时段" :value="booking.timeSlot"></u-cell>
      <u-cell title="人数" :value="`${booking.guestCount} 人`"></u-cell>
      <u-cell title="席位类型" :value="booking.seatType"></u-cell>
      <u-cell v-if="booking.tableNo" title="分配桌位" :value="booking.tableNo"></u-cell>
      <u-cell title="预约时间" :value="formatTime(booking.createTime)"></u-cell>
    </u-cell-group>
    </scroll-view>

    <view class="actions">
      <u-button
        v-if="booking.status === '待确认'"
        type="error"
        text="取消预约"
        @click="cancel"
      ></u-button>
      <u-button type="primary" text="返回首页" @click="goHome"></u-button>
    </view>
  </view>
  <u-loading-page v-else loading></u-loading-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getBookingDetail, cancelBooking } from '@/api/index.js'
import { BOOKING_STATUS_MAP } from '@/utils/common.js'

const booking = ref(null)

const statusInfo = computed(() =>
  BOOKING_STATUS_MAP[booking.value?.status] || { text: booking.value?.status, type: 'info' }
)

function formatTime(t) {
  if (!t) return ''
  return String(t).slice(0, 16).replace('T', ' ')
}

async function loadDetail(id) {
  try {
    booking.value = await getBookingDetail(id)
  } catch (e) {
    uni.navigateBack()
  }
}

async function cancel() {
  uni.showModal({
    title: '提示',
    content: '确定取消预约？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelBooking(booking.value.id)
          uni.showToast({ title: '已取消', icon: 'success' })
          loadDetail(booking.value.id)
        } catch (e) {}
      }
    }
  })
}

function goHome() {
  uni.switchTab({ url: '/pages/index/index' })
}

onMounted(() => {
  const pages = getCurrentPages()
  const cur = pages[pages.length - 1]
  const id = cur.options?.id || cur.$page?.options?.id
  if (id) loadDetail(id)
})
</script>

<style lang="scss" scoped>
.page { background: #0f0f1a; }
.status-header { text-align: center; padding: 48rpx; }
.actions {
  flex-shrink: 0;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  display: flex;
  gap: 20rpx;
  background: #0f0f1a;
}
</style>
