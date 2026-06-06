<template>
  <view class="bar-page">
    <view class="bar-card">
      <view class="section-title">预约日期</view>
      <u-cell :title="bookingDate" isLink @click="showDatePicker = true" />
      <u-datetime-picker
        :show="showDatePicker"
        v-model="dateValue"
        mode="date"
        :minDate="minDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
        @close="showDatePicker = false"
      />
    </view>

    <view class="bar-card">
      <view class="section-title">预约时段</view>
      <u-radio-group v-model="timeSlot" placement="column">
        <u-radio
          v-for="slot in timeSlots"
          :key="slot.value || slot"
          :label="slot.label || slot"
          :name="slot.value || slot"
          customStyle="margin-bottom: 16rpx"
        />
      </u-radio-group>
    </view>

    <view class="bar-card">
      <view class="section-title">席位类型</view>
      <u-radio-group v-model="seatType" placement="row">
        <u-radio label="卡座" name="卡座" />
        <u-radio label="吧台" name="吧台" />
        <u-radio label="包厢" name="包厢" />
      </u-radio-group>
    </view>

    <view class="bar-card">
      <view class="section-title">用餐人数</view>
      <u-number-box v-model="guestCount" :min="1" :max="20" />
    </view>

    <view class="bottom-bar safe-bottom">
      <u-button type="primary" text="提交预约" :loading="submitting" @click="submit" />
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getTimeSlots, submitBooking } from '@/api/booking'

const showDatePicker = ref(false)
const dateValue = ref(Date.now())
const bookingDate = ref('')
const timeSlot = ref('')
const seatType = ref('卡座')
const guestCount = ref(2)
const timeSlots = ref([])
const submitting = ref(false)
const minDate = Date.now()

function formatDate(ts) {
  const d = new Date(ts)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

async function loadTimeSlots() {
  const date = bookingDate.value || formatDate(dateValue.value)
  try {
    const res = await getTimeSlots({ date })
    timeSlots.value = (res || []).map((s) =>
      typeof s === 'string' ? { label: s, value: s } : s
    )
    if (timeSlots.value.length && !timeSlot.value) {
      timeSlot.value = timeSlots.value[0].value || timeSlots.value[0].label
    }
  } catch (e) {
    timeSlots.value = [
      { label: '18:00-20:00', value: '18:00-20:00' },
      { label: '20:00-22:00', value: '20:00-22:00' },
      { label: '22:00-24:00', value: '22:00-24:00' }
    ]
  }
}

function onDateConfirm(e) {
  bookingDate.value = formatDate(e.value)
  showDatePicker.value = false
  loadTimeSlots()
}

async function submit() {
  const date = bookingDate.value || formatDate(dateValue.value)
  if (!timeSlot.value) {
    uni.showToast({ title: '请选择时段', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const res = await submitBooking({
      bookingDate: date,
      timeSlot: timeSlot.value,
      guestCount: guestCount.value,
      seatType: seatType.value
    })
    uni.redirectTo({
      url: `/pages/booking/success?id=${res.id || res.bookingId}&date=${date}&time=${timeSlot.value}&count=${guestCount.value}`
    })
  } finally {
    submitting.value = false
  }
}

onShow(() => {
  bookingDate.value = formatDate(dateValue.value)
  loadTimeSlots()
})
</script>

<style lang="scss" scoped>
.section-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.bottom-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 40rpx;
}
</style>
