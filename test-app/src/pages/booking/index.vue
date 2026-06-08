<template>
  <view class="page page-fixed">
    <scroll-view scroll-y class="page-body form-scroll" :show-scrollbar="false">
    <u-form labelPosition="left" labelWidth="160rpx">
      <u-form-item label="预约日期">
        <view @click="showDatePicker = true">{{ bookingDate || '请选择日期' }}</view>
      </u-form-item>
      <u-form-item label="预约时段">
        <u-radio-group v-model="timeSlot" placement="column">
          <u-radio
            v-for="slot in timeSlots"
            :key="slot"
            :label="slot"
            :name="slot"
            customStyle="margin-bottom: 12rpx"
          ></u-radio>
        </u-radio-group>
      </u-form-item>
      <u-form-item label="席位类型">
        <u-radio-group v-model="seatType">
          <u-radio label="卡座" name="卡座"></u-radio>
          <u-radio label="吧台" name="吧台"></u-radio>
          <u-radio label="散台" name="散台"></u-radio>
        </u-radio-group>
      </u-form-item>
      <u-form-item label="人数">
        <u-number-box v-model="guestCount" :min="1" :max="20"></u-number-box>
      </u-form-item>
    </u-form>
    </scroll-view>

    <view class="footer">
      <u-button type="primary" text="提交预约" :loading="submitting" @click="submit"></u-button>
    </view>

    <u-datetime-picker
      :show="showDatePicker"
      v-model="dateValue"
      mode="date"
      :minDate="minDate"
      @confirm="onDateConfirm"
      @cancel="showDatePicker = false"
    ></u-datetime-picker>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBookingTimeSlots, submitBooking } from '@/api/index.js'

const bookingDate = ref('')
const dateValue = ref(Date.now())
const minDate = Date.now()
const showDatePicker = ref(false)
const timeSlots = ref([])
const timeSlot = ref('')
const seatType = ref('卡座')
const guestCount = ref(2)
const submitting = ref(false)

function formatDate(ts) {
  const d = new Date(ts)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function onDateConfirm(e) {
  bookingDate.value = formatDate(e.value)
  showDatePicker.value = false
  loadTimeSlots()
}

async function loadTimeSlots() {
  try {
    const data = await getBookingTimeSlots({ date: bookingDate.value })
    timeSlots.value = data?.slots || data || []
    if (timeSlots.value.length) timeSlot.value = timeSlots.value[0]
  } catch (e) {
    timeSlots.value = ['18:00-20:00', '20:00-22:00', '22:00-24:00']
    timeSlot.value = timeSlots.value[0]
  }
}

async function submit() {
  if (!bookingDate.value) {
    uni.showToast({ title: '请选择日期', icon: 'none' })
    return
  }
  if (!timeSlot.value) {
    uni.showToast({ title: '请选择时段', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const result = await submitBooking({
      bookingDate: bookingDate.value,
      timeSlot: timeSlot.value,
      seatType: seatType.value,
      guestCount: guestCount.value
    })
    uni.redirectTo({
      url: `/pages/booking/success?id=${result.id || ''}&date=${bookingDate.value}&time=${timeSlot.value}&count=${guestCount.value}`
    })
  } catch (e) {
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  bookingDate.value = formatDate(Date.now())
  loadTimeSlots()
})
</script>

<style lang="scss" scoped>
.page {
  background: #0f0f1a;
}
.form-scroll {
  padding: 32rpx;
}
.footer {
  flex-shrink: 0;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: #0f0f1a;
}
</style>
