<template>
  <view class="page">
    <u-form labelPosition="left" labelWidth="160rpx">
      <u-form-item label="预约日期" @click="showDatePicker = true">
        <u-input v-model="form.bookingDate" readonly placeholder="请选择日期" />
      </u-form-item>

      <u-form-item label="预约时段">
        <u-radio-group v-model="form.timeSlot" placement="column">
          <u-radio
            v-for="slot in timeSlots"
            :key="slot"
            :label="slot"
            :name="slot"
            customStyle="margin-bottom: 12rpx"
          />
        </u-radio-group>
      </u-form-item>

      <u-form-item label="席位类型">
        <u-radio-group v-model="form.seatType" placement="row">
          <u-radio label="吧台" name="吧台" />
          <u-radio label="卡座" name="卡座" />
          <u-radio label="包厢" name="包厢" />
        </u-radio-group>
      </u-form-item>

      <u-form-item label="人数">
        <u-number-box v-model="form.guestCount" :min="1" :max="20" />
      </u-form-item>
    </u-form>

    <view class="bottom-bar">
      <u-button type="primary" text="提交预约" :loading="submitting" @click="submit" />
    </view>

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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTimeSlots, submitBooking } from '@/api/booking'

const form = reactive({
  bookingDate: '',
  timeSlot: '',
  seatType: '卡座',
  guestCount: 2
})
const timeSlots = ref(['18:00-20:00', '20:00-22:00', '22:00-24:00'])
const submitting = ref(false)
const showDatePicker = ref(false)
const dateValue = ref(Date.now())
const minDate = Date.now()

onMounted(() => loadTimeSlots())

async function loadTimeSlots() {
  try {
    const data = await getTimeSlots({ date: form.bookingDate })
    if (data?.length) timeSlots.value = data
  } catch { /* 使用默认时段 */ }
}

function onDateConfirm(e) {
  const d = new Date(e.value)
  const pad = (n) => String(n).padStart(2, '0')
  form.bookingDate = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
  showDatePicker.value = false
  loadTimeSlots()
}

async function submit() {
  if (!form.bookingDate) {
    uni.showToast({ title: '请选择日期', icon: 'none' })
    return
  }
  if (!form.timeSlot) {
    uni.showToast({ title: '请选择时段', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const data = await submitBooking(form)
    const id = data?.id || ''
    uni.redirectTo({ url: `/pages/booking/success?id=${id}` })
  } catch (e) {
    uni.showToast({ title: e.msg || '预约失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f0f1a;
  padding: 24rpx;
  padding-bottom: 140rpx;
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
